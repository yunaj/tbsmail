package com.hs.mail.webmail.dao.std;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.dao.PreferencesDAO;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.impl.WmaFetchAccount;
import com.hs.mail.webmail.util.MD5;

public class SimplePreferencesDAO implements PreferencesDAO {

	@Override
	public WmaPreferences getPreferences(String identity) throws WmaException {
		File file = getFile(identity, MD5.hash(identity));
		return deserialize(file);
	}

	@Override
	public void savePreferences(WmaPreferences prefs) throws WmaException {
		String identity = prefs.getUserIdentity();
		File file = getFile(identity, MD5.hash(identity));
		serialize(prefs, file);
	}

	public List<WmaFetchAccount> getFetchAccounts(String identity)
			throws WmaException {
		File file = getFile(identity, "accounts");
		return deserialize(file);
	}
	
	public WmaFetchAccount getFetchAccount(String identity, String uid)
			throws WmaException {
		List<WmaFetchAccount> accounts = getFetchAccounts(identity);
		if (accounts != null) {
			for (WmaFetchAccount account : accounts) {
				if (account.getUID().equals(uid)) {
					return account;
				}
			}
		}
		return null;
	}
	
	public void deleteFetchAccount(String identity, String uid) throws WmaException {
		List<WmaFetchAccount> accounts = getFetchAccounts(identity);
		if (accounts != null) {
			for (int i = 0; i < accounts.size(); i++) {
				WmaFetchAccount account = accounts.get(i); 
				if (account.getUID().equals(uid)) {
					accounts.remove(i);
					saveFetchAccounts(identity, accounts);
				}
			}
		}
	}
	
	public void saveFetchAccount(String identity, WmaFetchAccount account)
			throws WmaException {
		if (StringUtils.isEmpty(account.getUID())) {
			createFetchAccount(identity, account);
		} else {
			updateFetchAccount(identity, account);
		}
	}
	
	private void createFetchAccount(String identity, WmaFetchAccount account)
			throws WmaException {
		List<WmaFetchAccount> accounts = getFetchAccounts(identity);
		if (accounts == null) {
			accounts = new ArrayList<WmaFetchAccount>();
		}
		account.setUID(UUID.randomUUID().toString());
		accounts.add(account);
		saveFetchAccounts(identity, accounts);
	}

	private void updateFetchAccount(String identity, WmaFetchAccount account)
			throws WmaException {
		List<WmaFetchAccount> accounts = getFetchAccounts(identity);
		if (accounts != null) {
			int index = accounts.indexOf(account);
			if (index != -1) {
				accounts.set(index, account);
				saveFetchAccounts(identity, accounts);
			}
		}
	}
	
	private void saveFetchAccounts(String identity,
			List<WmaFetchAccount> accounts) throws WmaException {
		File file = getFile(identity, "accounts");
		serialize((Serializable) accounts, file);
	}
	
	private static <T> T deserialize(File file) throws WmaException {
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			return SerializationUtils.deserialize(input);
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new WmaException("wma.prefs.load").setException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}
	
	private static void serialize(final Serializable obj, final File file)
			throws WmaException {
		OutputStream output = null;
		try {
			FileUtils.forceMkdir(file.getParentFile());
			output = new FileOutputStream(file);
			SerializationUtils.serialize(obj, output);
		} catch (Exception e) {
			throw new WmaException("wma.prefs.save").setException(e);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	private static File getFile(String identity, String fname) {
		return new File(Configuration.getUserHome(identity), fname);
	}

}
