package com.hs.mail.webmail.model;

import java.io.Serializable;

public interface WmaPreferences extends Serializable {

	/**
	 * Returns a <tt>String</tt> representing identity of the owner of this
	 * <tt>WmaPreferences</tt>.
	 * <p>
	 * <em>Note</em>:<br>
	 * The format of the string has to be
	 * <tt>&lt;username&gt;@&lt;postofficehost&gt;</tt>. <br>
	 * 
	 * @return the identity string of this preferences owner.
	 */
	String getUserIdentity();

	void setUserIdentity(String userIdentity);
	
	/**
	 * Returns a <tt>String</tt> representing the username of the owner of this
	 * <tt>WmaPreferences</tt>.
	 * 
	 * @return the username of this preferences owner.
	 */
	String getUsername();

	/**
	 * Sets the name of the owner of this <tt>WmaPreferences</tt>.
	 * 
	 * @param username
	 *            the owner's name.
	 */
	void setUsername(String username);
	
	int getPageSize();
	
	/**
	 * Tests if messages should be quoted automatically when replying.
	 * 
	 * @return true if messages should be quoted automatically, false otherwise.
	 */
	boolean isAutoQuote();

	/**
	 * Sets the flag that controls whether messages should be automatically
	 * quoted on reply.
	 * 
	 * @param doquote
	 *            true if messages being replied to should be automatically
	 *            quoted, false otherwise.
	 */
	void setAutoQuote(boolean doquote);
	
	boolean isAutoSign();
	
	void setAutoSign(boolean autoSign);
	
	/**
	 * Tests if messages should be automatically archived when sent.
	 * 
	 * @return true if messages should be archived automatically, false
	 *         otherwise.
	 */
	boolean isAutoArchiveSent();

	/**
	 * Sets the flag that controls whether messages should be automatically
	 * archived when sent.
	 * 
	 * @param doarchive
	 *            true if messages being sent should be automatically archived,
	 *            false otherwise.
	 */
	void setAutoArchiveSent(boolean doarchive);
	
	/**
	 * Returns a <tt>String</tt> representing the full name of the draft folder.
	 * 
	 * @return the full name of the draft folder as String.
	 */
	String getDraftFolder();

	/**
	 * Returns a <tt>String</tt> representing the path of the sent-mail-archive.
	 * 
	 * @return the path of the sent-mail-archive as <tt>String</tt>.
	 */
	String getSentMailArchive();

	/**
	 * Returns a <tt>String</tt> representing the path of the send-mail-archive.
	 * 
	 * @return the path of the send-mail-archive as <tt>String</tt>.
	 */
	String getToSendFolder();
	
	/**
	 * Returns a <tt>String</tt> representing the path of the
	 * trash-mail-archive.
	 * 
	 * @return the path of the trash-mail-archive as String.
	 */
	String getTrashFolder();

	/**
	 * Returns a <tt>String</tt> representing the path of the
	 * personal-mail-archive.
	 * 
	 * @return the path of the personal-mail-archive as String.
	 */
	String getPersonalFolder();
	
}
