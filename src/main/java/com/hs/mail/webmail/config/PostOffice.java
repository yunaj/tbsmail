package com.hs.mail.webmail.config;

import java.io.Serializable;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostOffice implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(PostOffice.class);

	// instance attributes
	private String name = "Default";

	private String address = "localhost";
	
	private int port = -1; // protocol default

	private String domain = "";

	private String rootFolder = "";

	private boolean secure = false;

	private String type = "mixed";

	private String protocol = "imap";

	private String replyToDomain;

	/**
	 * Constructs a new <tt>PostOffice</tt> instance.
	 */
	public PostOffice() {
	}

	/**
	 * Returns a common name for this <tt>PostOffice</tt>. Naming for
	 * different post offices should be kept unique.
	 * 
	 * @return the name as <tt>String</tt>.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the common name for this <tt>PostOffice</tt>.
	 * 
	 * @param name
	 *            the name as <tt>String</tt>.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the protocol required for this <tt>PostOffice</tt>.
	 * 
	 * @return the protocol as <tt>String</tt>.
	 */
	public String getProtocol() {
		return this.protocol;
	}

	/**
	 * Sets the protocol required for this <tt>PostOffice</tt>.
	 * 
	 * @param protocol
	 *            the protocol as <tt>String</tt>.
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Returns the type of this <tt>PostOffice</tt>.
	 * 
	 * @return the type as <tt>String</tt>.
	 * @see #TYPE_MIXED
	 * @see #TYPE_PLAIN
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets the type of this <tt>PostOffice</tt>.
	 * 
	 * @param type
	 *            the type as <tt>String</tt>.
	 * @see #TYPE_MIXED
	 * @see #TYPE_PLAIN
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Tests if this <tt>PostOffice</tt> is of a given type.
	 * 
	 * @return true if of the given type, false otherwise.
	 * @see #TYPE_MIXED
	 * @see #TYPE_PLAIN
	 */
	public boolean isType(String type) {
		return this.type.equals(type);
	}

	/**
	 * Returns the internet address of this <tt>PostOffice</tt>.
	 * 
	 * @return the address as <tt>String</tt>.
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * Sets the internet address of this <tt>PostOffice</tt>.
	 * 
	 * @param address
	 *            the address as <tt>String</tt>.
	 */

	public void setAddress(String address) {
		try {
			if (address == null || address.equals("")
					|| address.equals("localhost")) {
				this.address = InetAddress.getLocalHost().getHostName();
			}
		} catch (Exception ex) {
			log.error("setAddress()", ex);
		}
		this.address = address;
	}

	// setAddress

	/**
	 * Returns the port of this <tt>PostOffice</tt>.
	 * 
	 * @return the port as <tt>int</tt>.
	 */
	public int getPort() {
		return this.port;
	}// getPort

	/**
	 * Sets the port of this this <tt>PostOffice</tt>.
	 * 
	 * @param port
	 *            the port as <tt>int</tt>
	 */
	public void setPort(int port) {
		this.port = port;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Tests if the communication with this <tt>PostOffice</tt> should be
	 * secure.
	 * 
	 * @return true if secure, false otherwise.
	 * @see #getSecureSocketFactory()
	 */
	public boolean isSecure() {
		return this.secure;
	}

	/**
	 * Sets the communication with this <tt>PostOffice</tt> to be secure.
	 * 
	 * @param secure
	 *            true if secure, false otherwise.
	 * @see #setSecureSocketFactory(String factory)
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	/**
	 * Returns the default root folder this <tt>PostOffice</tt>.
	 * 
	 * @return the folder as <tt>String</tt>.
	 */
	public String getRootFolder() {
		return this.rootFolder;
	}

	/**
	 * Sets the default root folder for this <tt>PostOffice</tt>.
	 * 
	 * @param folder
	 *            the folder as <tt>String</tt>.
	 */
	public void setRootFolder(String folder) {
		this.rootFolder = folder;
	}

	/**
	 * Returns the reply-to domain setting for this <tt>PostOffice</tt>.
	 * 
	 * @return the reply-to domain as <tt>String</tt>.
	 */
	public String getReplyToDomain() {
		return this.replyToDomain;
	}

	/**
	 * Sets the reply-to domain for this <tt>PostOffice</tt>.
	 * 
	 * @param domain
	 *            the reply-to domain as <tt>String</tt>.
	 */
	public void setReplyToDomain(String domain) {
		this.replyToDomain = domain;
	}

	/**
	 * Defines a mixed type post office, were folders in the store can have
	 * subfolders and messages at the same time.
	 */
	public static final String TYPE_MIXED = "mixed";

	/**
	 * Defines a plain type post office, were folders in the store cannot have
	 * subfolders and messages at the same time.
	 */
	public static final String TYPE_PLAIN = "plain";
    
    public String toString() {
        return new StringBuffer().append(protocol).append("://")
                .append(address).append(":").append(port).toString();
    }

}
