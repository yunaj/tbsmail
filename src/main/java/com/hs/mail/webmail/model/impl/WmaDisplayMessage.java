package com.hs.mail.webmail.model.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.omg.CORBA.IntHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaMessagePart;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaDisplayMessage extends WmaMessageInfoImpl {
	
	private static Logger log = LoggerFactory.getLogger(WmaDisplayMessage.class);

	// instance attributes
	private String folderFullName;
	private Message message;
	private String messageID;
	private String replyTo;
	private String CC;
	private String BCC;
	private String contentType;
	private String notifyURL;
	private String body;
	private List<WmaMessagePart> messageParts;
	private List<WmaMessagePart> attachParts;

	/**
	 * Constructs a <tt>WmaDisplayMessage</tt> instance.
	 * 
	 * @param number
	 *            the number of the message as <tt>int</tt>
	 */
	protected WmaDisplayMessage(Message message, long uid) {
		super(uid);
		this.message = message;
	}

	public String getFolderFullName() {
		return folderFullName;
	}

	public void setFolderFullName(String folderFullName) {
		this.folderFullName = folderFullName;
	}

	public Message getMessage() {
		return message;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getBCC() {
		return BCC;
	}
	
	public void setBCC(String bcc) {
		BCC = bcc;
	}

	public String getContentType() {
		try {
			return new ContentType(contentType).getBaseType().toLowerCase();
		} catch (Exception ex) {
			return "text/plain";
		}
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setBodyText(String text) {
		setContentType("text/plain");
		setBody(text);
	}

	public String getCC() {
		return CC;
	}

	public void setCC(String cc) {
		CC = cc;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	
	public void setNotifyURL(String url) {
		this.notifyURL = url;
	}
	
	public String getNotifyURL() {
		return notifyURL;
	}

	public List<WmaMessagePart> getMessageParts() {
		return messageParts;
	}

	public void setMessageParts(List<WmaMessagePart> messageParts) {
		this.messageParts = messageParts;
	}
	
	public WmaMessagePart getMessagePart(int index) {
		return (messageParts != null && messageParts.size() > index) 
				? (WmaMessagePart) messageParts.get(index)
				: null;
	}
	
	public List<WmaMessagePart> getAttachParts() {
		return attachParts;
	}

	public void setAttachParts(List<WmaMessagePart> attachParts) {
		this.attachParts = attachParts;
	}
	
	public int getAttachCount() {
		return (CollectionUtils.isEmpty(attachParts)) ? 0 : attachParts.size();
	}

	private static WmaMessagePart getWmaMessagePart(Multipart mp, int number,
			IntHolder current, String cid) throws Exception {
		for (int i = 0; i < mp.getCount(); i++) {
			Part part = mp.getBodyPart(i);
			if (part.isMimeType("multipart/*")) {
				WmaMessagePart wpart = getWmaMessagePart(
						(Multipart) part.getContent(), number, current, cid);
				if (wpart != null) {
					return wpart;
				}
			} else {
				if (cid != null) {
					String header = WmaUtils.getHeader((Part) part,
							"Content-ID", null);
					if (header != null
							&& cid.equals(StringUtils.strip(header, "<>"))) {
						// we found the part!
						return WmaMessagePartImpl.createWmaMessagePart(part,
								number);
					}
				} else {
					if (number == current.value++) {
						// we found the part!
						return WmaMessagePartImpl.createWmaMessagePart(part,
								number);
					}
				}
			}
		}
		return null;
	}

	public static WmaMessagePart getWmaMessagePart(Message msg, int part,
			String cid) throws Exception {
		if (msg.isMimeType("multipart/*")) {
			return getWmaMessagePart((Multipart) msg.getContent(), part,
					(cid == null) ? new IntHolder(0) : null, cid);
		} else {
			throw new WmaException("wma.displaymessage.notmultipart");
		}
	}
	
	private void setSafeBody(String body) {
		setBody("text/html".equals(getContentType()) ? Jsoup.clean(body, Whitelist.relaxed()) : body);
	}

	private void buildBodyText(Part part) throws Exception {
		Object o = part.getContent();
		if (o instanceof String) {
			setSafeBody((String) o);
		} else if (o instanceof InputStream) {
			setSafeBody(IOUtils.toString((InputStream) o));
		} else {
			setBodyText("Unknown type " + o.toString());
		}
	}

	private static void buildPartInfoList(List<WmaMessagePart> partlist,
			Multipart mp) throws Exception {
		for (int i = 0; i < mp.getCount(); i++) {
			// get part
			Part part = mp.getBodyPart(i);
			// handle single & multiparts
			if (part.isMimeType("multipart/*")) {
				// recurse
				buildPartInfoList(partlist, (Multipart) part.getContent());
			} else {
				// append the part
				partlist.add(WmaMessagePartImpl.createWmaMessagePart(part,
						partlist.size()));
			}
		}
	}
	
	private static WmaMessagePart findBodyPart(List<WmaMessagePart> partlist) {
		if (partlist.size() > 0) {
			WmaMessagePart bodyPart = (WmaMessagePart) partlist.get(0);
			if (bodyPart.isBodyPart()) {
				if (partlist.size() > 1) {
					// in the case of multipart/alternative
					WmaMessagePart alternative = (WmaMessagePart) partlist
							.get(1);
					if (alternative.isBodyPart()
							&& alternative.isMimeType("text/html")) {
						return alternative;
					}
				}
				return bodyPart;
			}
		}
		return null;
	}
	
	private static void filterAttachInfoList(List<WmaMessagePart> partlist,
			List<WmaMessagePart> attlist) {
		for (int i = 0; i < partlist.size(); i++) {
			WmaMessagePart part = (WmaMessagePart) partlist.get(i);
			if (part.isAttachPart() || part.isMessagePart()) {
				attlist.add(part);
			}
		}
	}

	protected void prepare(Message msg) {
		try {
			super.prepare(msg);
			// set folder
			if (msg.getFolder() != null) {
				setFolderFullName(msg.getFolder().getFullName());
			}
			// set recipients
			setReplyTo(WmaUtils.prepareString(InternetAddress.toString(msg
					.getReplyTo())));
			setCC(WmaUtils.prepareString(InternetAddress.toString(msg
					.getRecipients(Message.RecipientType.CC))));
			setBCC(WmaUtils.prepareString(InternetAddress.toString(msg
					.getRecipients(Message.RecipientType.BCC))));
			// set message ID
			setMessageID(((MimeMessage) msg).getMessageID());
			if (!msg.isMimeType("multipart/*")) {
				// set body as String processed with the users msgprocessor
				try {
					setContentType(msg.getContentType());
					setSafeBody(msg.getContent().toString());
				} catch (IOException ex) {
					// handle?
					setBodyText("System puzzled by corrupt singlepart message.");
				}
			} else {
				try {
					// get main body part
					Multipart mp = (Multipart) msg.getContent();

					// build partlist
					List<WmaMessagePart> partlist = new ArrayList<WmaMessagePart>(
							10);
					buildPartInfoList(partlist, mp);

					// set flatlist
					setMessageParts(partlist);

					// set body part
					WmaMessagePart part = findBodyPart(partlist);
					if (part != null) {
						setContentType(part.getContentType());
						buildBodyText(part.getPart());
					}

					// filter attach parts
					List<WmaMessagePart> attlist = new ArrayList<WmaMessagePart>(
							partlist.size());
					filterAttachInfoList(partlist, attlist);

					// set attach parts
					setAttachParts(attlist);
				} catch (IOException ex) {
					// handle?
					setMultipart(false);
					setBodyText("System puzzled by corrupt singlepart message.");
				}
			}
		} catch (Exception ex) {
			log.warn(ex.getMessage(), ex);
		}
	}

	public static WmaDisplayMessage createWmaDisplayMessage(long uid, Message msg)
			throws WmaException {
		WmaDisplayMessage message = null;
		try {
			message = new WmaDisplayMessage(msg, uid);
			message.prepare(msg);
			return message;
		} catch (Exception ex) {
			throw new WmaException("wma.displaymessage.failedcreation")
					.setException(ex);
		}
	}

	public static WmaDisplayMessage createWmaDisplayMessage(File eml)
			throws WmaException {
		WmaDisplayMessage message = null;
		try {
			Properties props = System.getProperties();
			props.put("mail.host", "smtp.dummydomain.com");
			props.put("mail.transport.protocol", "smtp");
			Session mailSession = Session.getDefaultInstance(props, null);
			InputStream source = new FileInputStream(eml);
			MimeMessage msg = new MimeMessage(mailSession, source);
			message = new WmaDisplayMessage(msg, -1);
			message.prepare(msg);
			return message;
		} catch (Exception ex) {
			throw new WmaException("wma.displaymessage.failedcreation")
					.setException(ex);
		}
	}

}
