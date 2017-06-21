package com.hs.mail.webmail.model;

import java.util.Date;

public interface WmaMessage {

	static final String MIMETYPE_TEXT = "text/plain";
	
	static final String MIMETYPE_HTML = "text/html";
	
	static String defaultContentType = MIMETYPE_TEXT;

	/**
	 * Returns an <tt>int</tt> representing the number of this message.
	 * <p>
	 * This number is the unique identifier for a message within a folder, or
	 * <tt>-1</tt> in case of a message created for being composed.
	 * 
	 * @return the number of this message, or -1 if newly created for composing.
	 */
	long getUID();

	/**
	 * Tests if the message was received.
	 * 
	 * @return true if the message was received, false otherwise.
	 */
	boolean isReceived();

	/**
	 * Tests if the message was sent.
	 * <p>
	 * Note that this method will always return the opposite of isReceived()
	 * (i.e. represents !getReceived()).
	 * 
	 * @return true if the message was sent, false otherwise.
	 */
	boolean isSent();

	/**
	 * Returns a <tt>WmaAddress</tt> representing the sender(s) of the message.
	 * 
	 * @return the sender's address of the message.
	 */
	//WmaAddress getFrom();

	/**
	 * Returns a <tt>String</tt> representing the Reply-To address(es) of the
	 * message.
	 * 
	 * @return the Reply-To address(es) of the message as String.
	 */
	String getReplyTo();

	/**
	 * Returns a <tt>String</tt> representing the receivers(s) of the message.
	 * 
	 * @return the receiver(s) of the message as String.
	 */
	String getTo();

	/**
	 * Returns a <tt>String</tt> representing the carbon copy receivers(s) of
	 * the message.
	 * 
	 * @return the carbon copy receiver(s) of the message as String.
	 */
	String getCC();

	/**
	 * Returns a <tt>String</tt> representing the blind carbon copy receivers(s)
	 * of the message.
	 * 
	 * @return the blind carbon copy receiver(s) of the message as String.
	 */
	String getBCC();

	/**
	 * Convenience method that returns a <tt>Date</tt> representing the received
	 * or sent date of the message. (Depending on whether it was sent or
	 * received).
	 * 
	 * @return the received or sent date of the message.
	 */
	Date getDate();

	/**
	 * Returns a <tt>Date</tt> representing the date when the message was
	 * received.
	 * 
	 * @return the received date of the message.
	 */
	Date getReceivedDate();

	/**
	 * Returns a <tt>Date</tt> representing the date when the message was sent.
	 * 
	 * @return the sent date of the message.
	 */
	Date getSentDate();

	/**
	 * Returns a <tt>String</tt> representing the subject of the message.
	 * 
	 * @return the subject of the message as String.
	 */
	String getSubject();

	/**
	 * Returns a <tt>String</tt> representing the body of the message.
	 * <p>
	 * Note that the body will be the (plain text) content of a singlepart
	 * message. The method will return an empty <tt>String</tt> for a multipart
	 * message.
	 * <p>
	 * A view programmer should base display decisions on the
	 * <tt>isSinglepart()</tt> (or <tt>isMultipart()</tt> method.
	 * 
	 * @return the content of the message as String.
	 * 
	 * @see #isSinglepart()
	 * @see #isMultipart()
	 */
	String getBody();
	
}
