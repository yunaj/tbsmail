package com.hs.mail.webmail.model.impl;

import java.io.Serializable;
import java.util.Date;

public class WmaFetchAccount implements Serializable {

	private static final long serialVersionUID = 8658217995708078553L;

	private String uid;
	
	private String protocol;

	private String userName;

	private String serverName;

	private String password;

	private int port = 110;
	
	private Boolean useSSL = false;

	private Boolean autoEmpty = false;

	private long fetchInterval;

	private String lastXUID;

	private Date lastReceivedDate;

	private int failureCount = 0;
	
	public String getUID() {
		return uid;
	}

	public void setUID(String uid) {
		this.uid = uid;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Boolean getUseSSL() {
		return useSSL;
	}

	public void setUseSSL(Boolean useSSL) {
		this.useSSL = useSSL;
	}

	public Boolean getAutoEmpty() {
		return autoEmpty;
	}

	public void setAutoEmpty(Boolean autoEmpty) {
		this.autoEmpty = autoEmpty;
	}

	public long getFetchInterval() {
		return fetchInterval;
	}

	public void setFetchInterval(long fetchInterval) {
		this.fetchInterval = fetchInterval;
	}

	public String getLastXUID() {
		return lastXUID;
	}

	public void setLastXUID(String lastXUID) {
		this.lastXUID = lastXUID;
	}

	public Date getLastReceivedDate() {
		return lastReceivedDate;
	}

	public void setLastReceivedDate(Date lastReceivedDate) {
		this.lastReceivedDate = lastReceivedDate;
	}

	public int getFailureCount() {
		return failureCount;
	}

	public void setFailureCount(int failureCount) {
		this.failureCount = failureCount;
	}

	public boolean equals(Object obj) {
		if (obj instanceof WmaFetchAccount) {
			WmaFetchAccount account = (WmaFetchAccount) obj;
			return uid.equals(account.getUID());
		} else {
			return false;
		}
	}

}
