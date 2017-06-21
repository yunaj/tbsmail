package com.hs.mail.webmail.model.impl;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaMessagePart;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.util.RequestUtils;
import com.hs.mail.webmail.util.WmaUtils;
import com.sun.mail.imap.IMAPFolder;

public class WmaFolderImpl implements WmaFolder {
	
	private static Logger log = LoggerFactory.getLogger(WmaFolderImpl.class);

	// associations
	protected IMAPFolder folder;

	// instance attributes
	protected int type;
    protected int messageCount = -1;

	/**
	 * Creates a <tt>WmaFolderImpl</tt> instance.
	 * 
	 * @param folder the <tt>javax.mail.Folder</tt> instance to be wrapped.
	 */
	protected WmaFolderImpl(IMAPFolder folder) {
		this.folder = folder;
	}

	/*** Basic info ************************************************************/
	
	public IMAPFolder getFolder() {
		return folder;
	}

	public String getName() {
		return folder.getName();
	}

	public String getPath() {
		return folder.getFullName();
	}

	public int getType() {
		return type;
	}

	/**
	 * Sets this folder's type.
	 * 
	 * @param type
	 *            this folder's type as <tt>int</tt>.
	 */
	protected void setType(int type) {
		this.type = type;
	}

	public boolean isParent(WmaFolder child, char separator) {
		return child.getPath().startsWith(getPath() + separator);
	}

	/*** Messages related ******************************************************/
	
	public int getMessageCount() {
        try {
            // NOTE - Message count must be cached. Otherwise continuous call of
            // this method causes pending of IMAP server.
            if (messageCount < 0)
                messageCount = folder.getMessageCount();
            return messageCount;
        } catch (MessagingException ex) {
            return 0;
        }
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

	@Override
	public WmaDisplayMessage getWmaMessage(int num) throws WmaException {
		return getWmaMessage(-1, num);
	}

	@Override
	public WmaDisplayMessage getWmaMessageByUID(long uid) throws WmaException {
		return getWmaMessage(uid, -1);
	}

	private WmaDisplayMessage getWmaMessage(long uid, int num)
			throws WmaException {
		try {
			folder.open(Folder.READ_WRITE);
			// get message and create wrapper
			Message msg = null;
			if (num == -1) {
				msg = folder.getMessageByUID(uid);
			} else {
				msg = folder.getMessage(num);
				uid = folder.getUID(msg);
			}
			folder.fetch(new Message[] { msg }, WmaUtils.getFetchProfile());
			WmaDisplayMessage message = WmaDisplayMessage
					.createWmaDisplayMessage(uid, msg);
			// set body as String processed with the users msgprocessor
			// MessageBodyHandler.process(message);
			// close without expunge
			folder.close(false);
			return message;
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.wmamessage").setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	public long getUID(int num, int offset) throws WmaException {
		try {
			folder.open(Folder.READ_ONLY);
			Message msg = folder.getMessage(num + offset);
			return folder.getUID(msg);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.wmamessage").setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}

	public void deleteMessages(WmaStore wstore, long[] uids, boolean purge)
			throws WmaException {
		// don't work with null or empty arrays
		if (uids == null || uids.length == 0) {
			return;
		}
		try {
			folder.open(Folder.READ_WRITE);
			// prepare messages
			Message[] msgs = folder.getMessagesByUID(uids);
			if (msgs.length > 0) {
				Folder trash = wstore.getTrashInfo().getFolder();
				if (!folder.getFullName().equals(trash.getFullName()) && !purge) {
					// if not the trash copy the messages to the trash
					folder.copyMessages(msgs, trash);
				}
				// flag deleted, so when closing with expunge
				// the messages are erased.
				folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
			}
			// close with expunge
			folder.close(true);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.deletemessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	public void copyMessages(WmaStore wstore, long[] uids, String destfolder)
			throws WmaException {
		copyMessages(wstore, uids, destfolder, false);
	}

	public void moveMessages(WmaStore wstore, long[] uids, String destfolder)
			throws WmaException {
		copyMessages(wstore, uids, destfolder, true);
	}
	
	private void copyMessages(WmaStore wstore, long[] uids, String destfolder,
			boolean move) throws WmaException {
		// dont work with null or empty arrays
		if (destfolder == null || destfolder.length() == 0) {
			return;
		}
		try {
			Folder dest = wstore.getFolder(destfolder);
			if (!dest.exists()) {
				throw new WmaException(
						"wma.folder.movemessage.destination.missing");
			}
			// check destination type
			if (WmaFolder.TYPE_FOLDER == dest.getType()) {
				throw new WmaException(
						"wma.folder.movemessage.destination.foul");
			}
			folder.open((move) ? Folder.READ_WRITE : Folder.READ_ONLY);
			// prepare messages
			Message[] msgs = (null == uids || uids.length == 0) 
					? folder.getMessages() : folder.getMessagesByUID(uids);
			if (msgs.length > 0) {
				folder.copyMessages(msgs, dest);
				if (move) {
					folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
				}
			}
			folder.close(move);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.movemessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	public void appendMessage(InputStream source) throws WmaException {
		try {
			Properties props = System.getProperties();
			props.put("mail.host", "smtp.dummydomain.com");
			props.put("mail.transport.protocol", "smtp");

			Session mailSession = Session.getDefaultInstance(props, null);
			MimeMessage message = new MimeMessage(mailSession, source);
			folder.appendMessages(new Message[]{message});
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.appendmessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}

	public void setFlagMessages(long[] uids, Flags flags, boolean set)
			throws WmaException {
		// don't work with null or empty arrays
		if (uids == null || uids.length == 0) {
			return;
		}
		try {
			folder.open(Folder.READ_WRITE);
			// prepare messages
			Message[] msgs = folder.getMessagesByUID(uids);
			if (msgs.length > 0) {
				folder.setFlags(msgs, flags, set);
			}
			// close with expunge
			folder.close(false);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.flagmessage.failed")
					.setException(mex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	/*** Message Part related 
	 * @throws WmaException **********************************************/
	
	private static void writeMessagePart(HttpServletRequest request,
			HttpServletResponse response, WmaMessagePart wpart,
			String disposition) throws WmaException {
		String filename = wpart.getName();
		// we do it all for fun or not?
		if (null == filename) {
			filename = "easter.egg";
		}
		String contentType = null;
		try {
			filename = "MSIE".equals(RequestUtils.getBrowser(request))
					? URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20")
					: new String(filename.getBytes("UTF-8"), "ISO-8859-1");
			contentType = new ContentType(wpart.getContentType()).getBaseType();
		}catch(Exception e){
			contentType = "application/octet-stream";
		}
		response.setContentType(contentType);
		// RFC 2047 since IE does not support RFC 2231
		response.setHeader("Content-Disposition", disposition + "; " + "filename=\"" + filename + "\"");			
		
		// stream out part
		ServletOutputStream output = null;
		try {
			output = response.getOutputStream();
			IOUtils.copyLarge(wpart.getPart().getInputStream(), output);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new WmaException("wma.folder.displaypart.failed").setException(e);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	public void writeMessagePart(HttpServletRequest request,
			HttpServletResponse response, long uid, int part)
			throws WmaException {
		try {
			folder.open(Folder.READ_ONLY);
			Message msg = folder.getMessageByUID(uid);
			WmaMessagePart wpart = WmaDisplayMessage.getWmaMessagePart(msg, part, null);
			writeMessagePart(request, response, wpart, Part.INLINE);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WmaException("wma.folder.displaypart.failed")
					.setException(ex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}

	public void writeMimeMessage(HttpServletResponse response, long uid,
			String filename) throws WmaException {
		try {
			folder.open(Folder.READ_ONLY);
			Message msg = folder.getMessageByUID(uid);
			String type = "";
			String disposition = "";
			// we do it all for fun or not?
			if (filename == null) {
				filename = WmaUtils.prepareString(WmaUtils.getHeader(msg, "Subject")) + ".eml";
				filename = StringUtils.replaceChars(filename, "\\/:*?\"<>|", "_");
				type = "message/rfc822";
				disposition = Part.ATTACHMENT;
			} else {
				type = "text/plain";
				disposition = Part.INLINE;
			}
			// set content type and file name
			response.setContentType(type);
			response.setHeader("Content-Disposition", disposition + "; "
					+ "filename=\"" + filename + "\"");

			// stream out message
			ServletOutputStream out = response.getOutputStream();
			msg.writeTo(out);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new WmaException("wma.folder.displaymime.failed")
					.setException(ex);
		} finally {
			// close the folder
			shutdownFolder(folder);
		}
	}
	
	/**
	 * Creates a <tt>WmaFolderImpl</tt> instance from the given <tt>Folder</tt>
	 * .
	 * 
	 * @param folder
	 *            mail <tt>Folder</tt> this instance will "wrap".
	 * 
	 * @return the newly created instance.
	 * 
	 * @throws WmaException
	 *             if it fails to create the new instance.
	 */
	public static WmaFolder createLight(Folder folder) throws WmaException {
		try {
			WmaFolderImpl wmafolder = new WmaFolderImpl((IMAPFolder) folder);
			wmafolder.setType(folder.getType());
			return wmafolder;
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.failedcreation");
		}
	}
	
	public static WmaFolder createNamespace(Folder ns) {
		WmaFolderImpl wmafolder = new WmaFolderImpl((IMAPFolder) ns);
		wmafolder.setType(TYPE_FOLDER);
		return wmafolder;
	}

	/*** Helper methods ****************************************************/

	protected static void shutdownFolder(Folder f) {
		try {
			// close the folder
			if (f != null && f.isOpen()) {
				f.close(false);
			}
		} catch (MessagingException mesx) {
			// don't care, the specs say it IS closed anyway
		}
	}

	/*** End Helper methods ************************************************/

}
