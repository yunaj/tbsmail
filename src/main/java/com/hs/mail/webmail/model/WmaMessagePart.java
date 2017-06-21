package com.hs.mail.webmail.model;

import javax.mail.Part;

public interface WmaMessagePart {

	Part getPart();
	
	/**
	 * Returns an <tt>int</tt> representing the number of this message part.
	 * <p>
	 * This number is the unique identifier for a part of a message.
	 * 
	 * @return the number of this message part.
	 */
	int getPartNumber();

	/**
	 * Returns a <tt>String</tt> representing the content type of this message
	 * part.
	 * 
	 * @return the content type of this message part as String.
	 */
	String getContentType();

	/**
	 * Tests if this <tt>JwmaMessagePart</tt> is of the given type.
	 * 
	 * @param type
	 *            the Mime type as <tt>String</tt>.
	 */
	boolean isMimeType(String type);
	
	boolean isBodyPart();
	
	boolean isAttachPart();

	boolean isMessagePart();
	
	/**
	 * Returns a <tt>String</tt> representing the description of this message
	 * part.
	 * 
	 * @return description of this message part as String.
	 */
	String getDescription();

	String getDisposition();
	
	/**
	 * Returns a <tt>String</tt> representing the name of this message part.
	 * 
	 * @return name of this message part as String.
	 */
	String getName();

	/**
	 * Returns an <tt>int</tt> representing the size of this message part in
	 * bytes.
	 * 
	 * @return the size of this message part in bytes.
	 */
	int getSize();

}
