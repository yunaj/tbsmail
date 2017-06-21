/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.webmail.fetch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.model.impl.WmaFetchAccount;
import com.hs.mail.webmail.util.WmaUtils;
import com.sun.mail.pop3.POP3Folder;

/**
 * Gateway between an external message store and Hedwig. Mail is fetched from
 * the external message store and injected into the Hedwig message store.
 * 
 * @author Won Chul Doh
 * @since Sep 2, 2010
 * 
 */
public class FetchMailer {
	
	private static Logger log = LoggerFactory.getLogger(FetchMailer.class);
	
	private final static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

	private WmaFetchAccount account;
	
	private Folder dest;

	private Store store;

	private Folder mbox;
	
	private int fetched = 0;
	
	private int fetchSize = 50;
	
	private Latest latest;
	
	public FetchMailer(WmaFetchAccount account, Folder dest) {
		this.account = account;
		this.dest = dest;
	}
	
	public String getUID() {
		return account.getUID();
	}
	
	public WmaFetchAccount getFetchAccount() {
		return account;
	}
	
	public void setDestination(Folder dest) {
		this.dest = dest;
	}

	public boolean connect() throws MessagingException {
		Properties props = System.getProperties();
		if (account.getUseSSL()) {
			props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
			props.setProperty("mail.pop3.socketFactory.fallback", "false");
			props.setProperty("mail.pop3.port", "995");
			props.setProperty("mail.pop3.socketFactory.port", "995");
		}
		Session session = Session.getDefaultInstance(props, null);
		store = session.getStore("pop3");
		store.connect(account.getServerName(), account.getUserName(), account
				.getPassword());
		log.debug("Connected to {}", account.getServerName());
		mbox = store.getDefaultFolder();
		mbox = mbox.getFolder("INBOX");
		mbox.open(Folder.READ_WRITE);
		return true;
	}
	
	public boolean disconnect(boolean expunge, int[] msgnums) {
		try {
			close(expunge, msgnums);
			return true;
		} catch (MessagingException e) {
			return false;
		} finally {
			if (store != null && store.isConnected()) {
				try {
					store.close();
				} catch (MessagingException e) {
				}
			}
		}
	}
	
	public int fetch() throws MessagingException {
		latest = new Latest();
		boolean expunge = account.getAutoEmpty();
		fetched = 0;
		try {
			connect();
			if (existNewMessages()) {
				int begin = 1;
				int count = getTotalMessageCount();
				if (count > begin) {
					int end = begin + fetchSize - 1;
					// Paginate to minimize the heap usage 
					while (end <= count) {
						// Get messages from begin to end
						fetched +=  appendMessages(begin, end, expunge);
						begin += fetchSize;
						end += fetchSize;
					}
					// Get the remaining messages
					fetched += appendMessages(begin, count, expunge);
					// Mark time and UID stamp
					account.setLastXUID(latest.XUID);
					account.setLastReceivedDate(latest.date);
				}
			} else {
				log.debug("No new mails!");
			}
		} catch (MessagingException e) {
			throw e;
		} finally {
			disconnect(expunge, null);
		}
		return fetched;
	}
	
	private int appendMessages(int begin, int end, boolean expunge)
			throws MessagingException {
		Message[] msgs = null;
		if (begin < end) {
			// Get messages from begin to end
			log.debug("Fetch messages from {} to {}", begin, end);
			msgs = retrieveMessages(begin, end);
			if (msgs != null && msgs.length > 0) {
				int[] msgnums = null;
				try {
					dest.open(Folder.READ_WRITE);
					if (expunge) {
						msgnums = new int[msgs.length];
						for (int i = 0; i < msgs.length; i++) {
							// Save the original message numbers
							msgnums[i] = msgs[i].getMessageNumber();
						}
					}
					// TODO If exception is thrown here, we must recalculate the
					// XUID and only appended message must be deleted from POP3
					// server.
					dest.appendMessages(msgs);
					if (log.isDebugEnabled()) {
						log.debug("{} messages are appended...", msgs.length);
					}
					dest.close(false);
					delete(expunge, msgnums);
				} catch (MessagingException e) {
					// Rethrow exception
					throw e;
				} finally {
					if (dest != null && dest.isOpen()) {
						try {
							dest.close(false);
						} catch (MessagingException mesx) {
							// don't care, the specs say it IS closed anyway
						}
					}
				}
			}
		}
		return (msgs != null) ? msgs.length : 0;
	}
	
	private boolean delete(boolean expunge, int[] msgnums) {
		try {
			if (expunge && msgnums != null && msgnums.length > 0) {
				if (mbox != null && mbox.isOpen()) {
					mbox.setFlags(msgnums, new Flags(Flags.Flag.DELETED), true);
				}
			}
			return true;
		} catch (MessagingException mex) {
			return false;
		}
	}
	
	private Message[] retrieveMessages(int begin, int end) {
		if (begin < end) {
			try {
				Message[] msgs = getMbox().getMessages(begin, end);
				if (!account.getAutoEmpty()
						&& account.getLastReceivedDate() != null) {
					msgs = getNewMessages(msgs);
				}
				if (msgs != null && msgs.length > 0) {
					try {
						POP3Folder pf = (POP3Folder) mbox;
						latest.XUID = pf.getUID(msgs[msgs.length - 1]);
						latest.date = getLastReceivedDate(msgs); 
					} catch (MessagingException e) {
						// Ignore this error
						log.warn(e.getMessage());
					} catch (Exception e) {
						log.warn(e.getMessage());
					}
				}
				return msgs;
			} catch (MessagingException e) {
				log.error(e.getMessage(), e);
			}
		}
		return null;
	}
	
	private Date getLastReceivedDate(Message[] msgs) {
		Date ret = latest.date;
		for (int i = 0; i < msgs.length; i++) {
			Date date = WmaUtils.getReceivedDate(msgs[i]);
			if (date != null && (ret == null || ret.before(date))) {
				ret = date;
			}
		}
		return ret;
	}
	
	private Message[] getNewMessages(Message[] msgs) {
		String XUID = account.getLastXUID();
		Date date = account.getLastReceivedDate();
		POP3Folder pf = (POP3Folder) mbox;
		List<Message> results = new ArrayList<Message>();
		boolean done = false;
		// The received date of incoming mail does not guarantee the order of
		// incoming mails.
		// So, we check the XUID first.
		for (int i = msgs.length - 1; i >= 0; i--) {
			try {
				String tempXUID = pf.getUID(msgs[i]);
				if (tempXUID != null) {
					if (tempXUID.equalsIgnoreCase(XUID)) {
						for (int j = i + 1; j < msgs.length; j++) {
							results.add(msgs[j]);
						}
						done = true;
						break;
					}
				} else {
					break;
				}
			} catch (MessagingException e) {
				log.warn(e.getMessage());
				break;
			}
		}
		if (!done) {
			Date rdate = null;
			for (int i = 0; i < msgs.length; i++) {
				rdate = WmaUtils.getReceivedDate(msgs[i]);
				if (rdate != null && date.before(rdate)) {
					results.add(msgs[i]);
				}
			}
		}
		return (results.size() > 0) 
				? results.toArray(new Message[results.size()]) 
				: null;
	}
	
	private Folder getMbox() throws MessagingException {
		boolean loggedIn = true;
		if (null == mbox || !mbox.isOpen()) {
			loggedIn = false;
			for (int i = 0; i < 2; i++) {
				try {
					connect();
					if (mbox.isOpen()) {
						loggedIn = true;
						break;
					}
					Thread.sleep(1000);
				} catch (Exception e) {
					loggedIn = false;
				}
			}
		}
		if (!loggedIn) {
			throw new MessagingException("Can not open mailbox");
		}
		return mbox;
	}
	
	private int getTotalMessageCount() {
		try {
			return getMbox().getMessageCount();
		} catch (MessagingException e) {
			log.error(e.getMessage(), e);
			return -1;
		}
	}

	private boolean existNewMessages() {
		int messageCount = getTotalMessageCount();
		log.debug("Total {} messages exist.", messageCount);
		if (messageCount > 0) {
			if (!account.getAutoEmpty()) {
				String XUID = account.getLastXUID();
				POP3Folder pf = (POP3Folder) mbox;
				try {
					Message msg = getMbox().getMessage(messageCount);
					String tempXUID = pf.getUID(msg);
					if (tempXUID != null) {
						if (tempXUID.equalsIgnoreCase(XUID)) {
							return false;
						}
					}
				} catch (MessagingException e) {
					log.error(e.getMessage(), e);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void close(boolean expunge, int[] msgnums)
			throws MessagingException {
		if (mbox != null && mbox.isOpen()) {
			if (expunge && msgnums != null && msgnums.length > 0) {
				mbox.setFlags(msgnums, new Flags(Flags.Flag.DELETED), true);
			}
			mbox.close(expunge);
		}
	}

	class Latest {
		public Date date = account.getLastReceivedDate();
		public String XUID = account.getLastXUID();
	}
	
}
