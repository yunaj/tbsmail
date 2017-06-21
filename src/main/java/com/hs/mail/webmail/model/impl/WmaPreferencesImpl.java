package com.hs.mail.webmail.model.impl;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.model.WmaPreferences;

public class WmaPreferencesImpl implements WmaPreferences {

	private static final long serialVersionUID = 3577049312892807585L;

	// instance attributes
	protected String userIdentity;
	protected String username;
	protected String inboxType = "inbox";
	
	// Auto features
	protected boolean autoQuote = true;
	protected boolean autoAttach = true;
	protected boolean autoSign = false;
	protected boolean autoArchiveSent = false;
	
	// Page size
	protected int pageSize = 10;

	protected String signature;

	public WmaPreferencesImpl() {
	}
	
	public WmaPreferencesImpl(String userIdentity) {
		setUserIdentity(userIdentity);
	}

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInboxType() {
		return inboxType;
	}

	public void setInboxType(String inboxType) {
		this.inboxType = inboxType;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public boolean isAutoQuote() {
		return autoQuote;
	}

	public void setAutoQuote(boolean doquote) {
		this.autoQuote = doquote;
	}

	public boolean isAutoAttach() {
		return autoAttach;
	}

	public void setAutoAttach(boolean doattach) {
		this.autoAttach = doattach;
	}

	public boolean isAutoSign() {
		return autoSign;
	}
	
	public void setAutoSign(boolean autoSign) {
		this.autoSign = autoSign;
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public boolean isAutoArchiveSent() {
		return autoArchiveSent;
	}

	public void setAutoArchiveSent(boolean doarchive) {
		this.autoArchiveSent = doarchive;
	}

	public String getDraftFolder() {
		return Configuration.getMessage("prefs.draftfolder");
	}

	public String getPersonalFolder() {
		return Configuration.getMessage("prefs.personalfolder");
	}

	public String getSentMailArchive() {
		return Configuration.getMessage("prefs.sentmailarchive");
	}

	public String getToSendFolder() {
		return Configuration.getMessage("prefs.tosendfolder");
	}

	public String getTrashFolder() {
		return Configuration.getMessage("prefs.trashfolder");
	}
	
}
