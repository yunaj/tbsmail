package com.hs.mail.webmail.fetch;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.dao.PreferencesDAO;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaFetchAccount;

public class FetchMailRunner {

	private static Map<String, Status> _locks = new HashMap<String, Status>();
	
	private String identity;
	private Store store;
	private String destination;
	private FetchMailer mailer;

	public FetchMailRunner(String identity, WmaFetchAccount account,
			Store store, String destination) {
		this.identity = identity;
		this.mailer = new FetchMailer(account, null);
		this.store = store;
		this.destination = destination;
	}
	
	public void start() throws MessagingException {
		String uid = mailer.getUID();
		Folder dest = store.getFolder(destination);
		mailer.setDestination(dest);

		String threadName = "FETCH-" + uid;
		Thread jobThread = new Thread(new MainJob(), threadName);
		jobThread.start();
	}
	
	public static Status status(String key) {
		return _locks.get(key);
	}
	
	static Status lock(String key, String value) {
		if (_locks.containsKey(key)) {
			if (_locks.get(key).status >= 0) {
				throw new RuntimeException("Failed to lock for " + key);
			}
		}
		Status status = new Status(value);
		_locks.put(key, status);
		return status;
	}
	
	static void unlock(String key) {
		_locks.remove(key);
	}
	
	class MainJob implements Runnable {

		public MainJob() {
		}

		@Override
		public void run() {
			String uid = mailer.getUID();
			try {
				lock(identity, uid);
				int fetched = mailer.fetch();
				if (fetched > 0) {
					rememberLastFetch();
				}
			} catch (MessagingException e) {
				
			} finally {
				unlock(identity);
				try {
					store.close();
				} catch (MessagingException e) {
				}
			}
		}
		
		private void rememberLastFetch() {
			WmaFetchAccount account = mailer.getFetchAccount();
			if (!account.getAutoEmpty()) {
				// Update last XUID and received date
				try {
					PreferencesDAO dao = Configuration.getPreferencesDAO();
					dao.saveFetchAccount(identity, account);
				} catch (WmaException e) {
				}
			}
		}

	}

	public static class Status {
		
		public static Status NULL = new Status(null);
		
		protected String uid;
		
		protected int status;

		protected Status(String uid) {
			this.uid = uid;
		}

		public String getUID() {
			return uid;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

	}
	
}
