package com.hs.mail.webmail.model.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.Date;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;
import javax.mail.search.SearchTerm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.webmail.WmaAuthenticator;
import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaSendReservation implements Serializable {

	private static final long serialVersionUID = -701368604879234736L;

	private String tosendpath;
	private String sentpath;
	private String messageID;
	private String username;
	private String password;

	public String getTosendPath() {
		return tosendpath;
	}

	public void setTosendPath(String path) {
		this.tosendpath = path;
	}

	public String getSentPath() {
		return sentpath;
	}

	public void setSentPath(String path) {
		this.sentpath = path;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public void saveTo(File file) {
		try {
			SerializationUtils.serialize(this, new FileOutputStream(file));
		} catch (FileNotFoundException e) {
		}
	}
	
	public static WmaSendReservation readFrom(File file) {
		try {
			return SerializationUtils.deserialize(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
	
	private static WmaSendReservation create(WmaSession session,
			String tosendpath, String sentpath, WmaComposeMessage message)
			throws MessagingException {
		WmaSendReservation sr = new WmaSendReservation();
		WmaAuthenticator authenticator = session.getAuthenticator();
		sr.setTosendPath(tosendpath);
		sr.setSentPath(sentpath);
		sr.setMessageID(message.getMessageID());
		sr.setUsername(authenticator.getUserName());
		sr.setPassword(authenticator.getPassword());
		return sr;
	}

	public static void serialize(WmaSession session, String tosendpath,
			String sentpath, WmaComposeMessage message, Date date)
			throws MessagingException {
		try {
			WmaSendReservation sr = create(session, tosendpath, sentpath, message);
			String prefix = WmaUtils.SIMPLE_DATE_FORMAT.format(date);
			File trigger = generateFile(prefix);
			FileUtils.forceMkdir(trigger.getParentFile());
			sr.saveTo(trigger);
		} catch (IOException e) {
			// this cannot happen
		}
	}
	
	private Message send(WmaSession session, Folder tosend)
			throws WmaException, MessagingException {
		MimeMessage message = getMimeMessage(tosend, getMessageID());
		if (message != null) {
			try {
				MimeMessage msg = new MimeMessage(message);
				session.getTransport().sendMessage(msg);
				return message;
			} catch (SendFailedException sex) {
				throw new WmaException("wma.composemessage.send.failed")
						.setException(sex);
			}
		}
		// User canceled sending
		return null;
	}
	
	public static void send(WmaSendReservation sr) throws WmaException,
			MessagingException {
		WmaStore store = null;
		Folder folder = null;
		try {
			WmaSession session = new MockSession();
			store = session.connect(sr.getUsername(), sr.getPassword());
			folder = store.getFolder(sr.getTosendPath());
			folder.open(Folder.READ_WRITE);
			Message msg = sr.send(session, folder);
			deleteMessage(store, folder, msg, sr.getSentPath());
			// close with expunge
			folder.close(true);
		} finally {
			if (store != null) {
				if (folder != null && folder.isOpen()) {
					folder.close(false);
				}
				store.close();
			}
		}
	}

    // file name generation
    private static final SecureRandom random = new SecureRandom();

	private static File generateFile(String prefix) {
		long n = random.nextLong();
		if (n == Long.MIN_VALUE) {
			n = 0; // corner case
		} else {
			n = Math.abs(n);
		}
		return new File(Configuration.getDeferDir(prefix), prefix + "." + n);
	}

	private MimeMessage getMimeMessage(Folder folder, String msgid)
			throws WmaException {
		try {
			SearchTerm term = new MessageIDTerm(msgid);
			Message[] msgs = folder.search(term);
			return (ArrayUtils.isNotEmpty(msgs)) 
					? (MimeMessage) msgs[0] : null;
		} catch (MessagingException ex) {
			throw new WmaException("wma.folder.wmamessage").setException(ex);
		}
	}

	private static void deleteMessage(WmaStore wstore, Folder folder,
			Message message, String destfolder) throws WmaException {
		if (message == null) {
			return;
		}
		try {
			Folder dest = StringUtils.isNotBlank(destfolder) 
					? wstore.getFolder(destfolder) : null;
			Message[] msgs = { message };
			if (dest != null) {
				// copy messages to destination folder
				folder.copyMessages(msgs, dest);
			}
			folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.deletemessage.failed")
					.setException(mex);
		}
	}

	static class MockSession extends WmaSession {
		
		private static final long serialVersionUID = 1L;

		WmaAuthenticator authenticator = null;

		public MockSession() {
			super(null);
		}

		@Override
		public WmaAuthenticator getAuthenticator() {
			return authenticator;
		}

		@Override
		public WmaStore connect(String username, String password)
				throws WmaException {
			authenticator = new WmaAuthenticator(username, password);
			Store store = connect(initMailSession(authenticator), username, password);
			return WmaStoreImpl.createStore(this, store);
		}

	}
	
}
