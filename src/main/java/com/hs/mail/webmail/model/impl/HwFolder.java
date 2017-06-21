package com.hs.mail.webmail.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.sun.mail.iap.Argument;
import com.sun.mail.iap.ParsingException;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.IMAPResponse;

public class HwFolder extends WmaFolderImpl {
	
	private static Logger log = LoggerFactory.getLogger(HwFolder.class);

	public HwFolder(IMAPFolder folder) {
		super(folder);
	}

	@SuppressWarnings("rawtypes")
	public Map revoke(long uid, int num, String flag, String[] recipients)
			throws WmaException {
		try {
			return (num == -1)
					? revoke(String.valueOf(uid), flag, recipients, true)
					: revoke(String.valueOf(num), flag, recipients, false);
		} catch (MessagingException mex) {
			log.error(mex.getMessage(), mex);
			throw new WmaException("wma.revoke.failed")
					.setException(mex);
		}
	}
	
	public List<WmaRecipient> createWmaRecipientList(long uid, int num)
			throws WmaException {
		try {
			folder.open(Folder.READ_ONLY);
			Message msg = (num == -1) 
					? folder.getMessageByUID(uid)
					: folder.getMessage(num);
			return createWmaRecipientList(msg);
		} catch (MessagingException mex) {
			log.error(mex.getMessage(), mex);
			throw new WmaException("wma.addrlist.failedcreation")
					.setException(mex);
		} finally {
			shutdownFolder(folder);
		}
	}

	public static HwFolder createLight(Folder folder) throws WmaException {
		try {
			HwFolder hwfolder = new HwFolder((IMAPFolder) folder);
			hwfolder.setType(folder.getType());
			return hwfolder;
		} catch (MessagingException mex) {
			throw new WmaException("wma.folder.failedcreation");
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Map revoke(final String n, final String f,
			final String[] recipients, final boolean uid) throws MessagingException {
		try {
			folder.open(Folder.READ_ONLY);
			return (Map) folder
					.doCommand(new IMAPFolder.ProtocolCommand() {

				@SuppressWarnings({ "unchecked" })
				@Override
				public Object doCommand(IMAPProtocol p)
						throws ProtocolException {
					Argument args = new Argument();
					args.writeString(n);
					args.writeString(f);
					if (recipients != null) {
						for (String recipient : recipients) {
							args.writeString(recipient);
						}
					}
					Response[] r = (uid) 
							? p.command("UID XREVOKE", args) 
							: p.command("XREVOKE", args);

					Map result = null;
					Response response = r[r.length - 1];
					if (response.isOK()) { // command successful
						RevokeInfo rinfo = null;
						result = new HashMap();
						for (int i = 0, len = r.length; i < len; i++) {
							if (!(r[i] instanceof IMAPResponse))
								continue;

							IMAPResponse ir = (IMAPResponse) r[i];
							if (ir.keyEquals("XREVOKE")) {
								rinfo = new RevokeInfo(ir);
								result.put(rinfo.address, rinfo.responseCode);
								r[i] = null;
							}
						}
					}
					p.handleResult(response);
					return result;
				}
			});
		} finally {
			shutdownFolder(folder);
		}
	}

	private List<WmaRecipient> createWmaRecipientList(Message msg)
			throws MessagingException, WmaException {
		List<WmaRecipient> recipients = new ArrayList<WmaRecipient>();
		buildWmaRecipientList(recipients, msg, Message.RecipientType.TO);
		buildWmaRecipientList(recipients, msg, Message.RecipientType.CC);
		buildWmaRecipientList(recipients, msg, Message.RecipientType.BCC);
		return recipients;
	}

	private void buildWmaRecipientList(List<WmaRecipient> recipients,
			Message msg, Message.RecipientType type) throws MessagingException,
			WmaException {
		Address[] addresses = msg.getRecipients(type);
		if (addresses != null) {
			for (Address address : addresses) {
				recipients.add(WmaRecipient.createWmaRecipient(
						(InternetAddress) address, type));
			}
		}
	}

	static class RevokeInfo {
		
		public int responseCode = OK;
		public String address = null;
		
		public static final int OK          = 0;
		public static final int NOPERM      = 1;
		public static final int CANNOT      = 2;
		public static final int NONEXISTENT = 3;
		
		public RevokeInfo(IMAPResponse r) throws ParsingException {
			String c = r.readString(' ');
			if (c.equalsIgnoreCase("NOPERM"))
				responseCode = NOPERM;
			else if (c.equalsIgnoreCase("CANNOT"))
				responseCode = CANNOT;
			else if (c.equalsIgnoreCase("NONEXISTENT"))
				responseCode = NONEXISTENT;
			address = r.getRest();
		}

	}

}
