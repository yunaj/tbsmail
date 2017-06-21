package com.hs.mail.webmail.config;

import java.io.Serializable;

public class MailTransportAgent implements Serializable {

	private static final long serialVersionUID = 1L;

	// instance attributes
	private String name = "Default";
	private String address="localhost";
	private int port = -1; // protocol default
	private boolean secure = false;
	private String protocol = "smtp";
	private boolean authenticated = false;
	private int transportLimit = 2048;

	/**
	 * Constructs a new <tt>MailTransportAgent</tt> instance.
	 */
	public MailTransportAgent() {

	}

	/**
	 * Returns a common name for this <tt>MailTransportAgent</tt>. Naming for
	 * different transport agents should be kept unique.
	 * 
	 * @return the name as <tt>String</tt>.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the common name for this <tt>MailTransportAgent</tt>.
	 * 
	 * @param name
	 *            the name as <tt>String</tt>.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the protocol required for this <tt>MailTransportAgent</tt>.
	 * 
	 * @return the protocol as <tt>String</tt>.
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Sets the protocol required for this <tt>MailTransportAgent</tt>.
	 * 
	 * @param protocol
	 *            the protocol as <tt>String</tt>.
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Returns the internet address of this <tt>MailTransportAgent</tt>.
	 * 
	 * @return the address as <tt>String</tt>.
	 */
	
	public String getAddress() { 
		 return address; 
	}
	
	/**
	 * Sets the internet address of this <tt>MailTransportAgent</tt>.
	 * 
	 * @param address
	 *            the address as <tt>String</tt>.
	 */
	public void setAddress(String address) { 
		this.address = address; 
	}
	

	/**
	 * Returns the port of this <tt>MailTransportAgent</tt>.
	 * 
	 * @return the port as <tt>int</tt>.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port of this this <tt>MailTransportAgent</tt>.
	 * 
	 * @param port
	 *            the port as <tt>int</tt>
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Tests if the communication with this <tt>MailTransportAgent</tt> should
	 * be secure.
	 * 
	 * @return true if secure, false otherwise.
	 * @see #getSecureSocketFactory()
	 */
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Sets the communication with this <tt>MailTransportAgent</tt> to be
	 * secure.
	 * 
	 * @param secure
	 *            true if secure, false otherwise.
	 * @see #setSecureSocketFactory(String factory)
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * Tests if this <tt>MailTransportAgent</tt> should be used with
	 * authentication.
	 * 
	 * @return true if authenticated use, false otherwise.
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * Sets the use of this <tt>MailTransportAgent</tt> to be authenticated.
	 * 
	 * @param auth
	 *            true if authenticated use, false otherwise.
	 */
	public void setAuthenticated(boolean auth) {
		this.authenticated = auth;
		if (this.authenticated) {
			System.setProperty("mail.smtp.auth", "true");
		}
	}

	/**
	 * Returns the maximum message size in kB's allowed for transport.
	 * 
	 * @return maximum size as <tt>int</tt>.
	 */
	public int getTransportLimit() {
		return transportLimit;
	}

	/**
	 * Sets the maximum message size in kB's allowed for transport.
	 * 
	 * @param size
	 *            the maximum size as <tt>int</tt>.
	 */
	public void setTransportLimit(int size) {
		this.transportLimit = size;
	}

}
