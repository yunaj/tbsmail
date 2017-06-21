package com.hs.mail.webmail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class WmaAuthenticator extends Authenticator {

	private PasswordAuthentication authenticator;

	public WmaAuthenticator(String username, String password) {
		this.authenticator = new PasswordAuthentication(username, password);
	}

	protected PasswordAuthentication getAuthenticator() {
		return authenticator;
	}

	/**
	 * Returns the password associated with this Authenticator.
	 */
	public String getPassword() {
		return authenticator.getPassword();
	}

	/**
	 * Returns the username associated with this Authenticator.
	 * 
	 */
	public String getUserName() {
		return authenticator.getUserName();
	}

}
