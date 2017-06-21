package com.hs.mail.webmail.dao;

import java.util.List;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.impl.WmaFetchAccount;

public interface PreferencesDAO {
	
	WmaPreferences getPreferences(String identity) throws WmaException;

	void savePreferences(WmaPreferences prefs) throws WmaException;

	List<WmaFetchAccount> getFetchAccounts(String identity) throws WmaException;

	WmaFetchAccount getFetchAccount(String identity, String uid) throws WmaException;

	void saveFetchAccount(String identity, WmaFetchAccount account) throws WmaException;

	void deleteFetchAccount(String identity, String uid) throws WmaException;

}
