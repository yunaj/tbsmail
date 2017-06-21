package com.hs.mail.webmail.model.impl;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaMessagePart;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaMessagePartImpl implements WmaMessagePart {
	
	private static Logger log = LoggerFactory.getLogger(WmaMessagePartImpl.class);

	// instance attributes
	private int number;
	private Part part;
	private String contentType;
	private String description;
	private String disposition;
	private String name;
	private int size;

	/**
	 * Private empty constructor, to prevent construction.
	 */
	private WmaMessagePartImpl() {
	}

	/**
	 * Constructs a <tt>WmaMessagePartImpl</tt> with a given part and number.
	 * 
	 * @param part
	 *            the part that is wrapped.
	 * @param number
	 *            an <tt>int</tt> that represents the part number.
	 */
	private WmaMessagePartImpl(Part part, int number) {
		super();
		this.part = part;
		this.number = number;
	}

	/**
	 * Gets the <tt>javax.mail.Part</tt> wrapped by this instance.
	 * 
	 * @return the wrapped part instance.
	 */
	public Part getPart() {
		return part;
	}

	public int getPartNumber() {
		return number;
	}

	public String getContentType() {
		return contentType;
	}

	/**
	 * Sets the content-type (mime) of this <tt>WmaMessagePart</tt>.
	 * 
	 * @param type
	 *            the content type of this <tt>WmaMessagePart</tt> as
	 *            <tt>String</tt>.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isMimeType(String type) {
		if (part != null) {
			try {
				return part.isMimeType(type);
			} catch (MessagingException mex) {
				log.error("isMimeType()", mex);
			}
		}
		return false;
		// FIX: probably evaluate based on m_ContentType otherwise
	}

	public boolean isBodyPart() {
		if ((0 == number || 1 == number)
				&& !Part.ATTACHMENT.equalsIgnoreCase(disposition)) {
			if (isMimeType("text/*")) {
				return true;
			}
		}
		return false;
	}

	public boolean isAttachPart() {
		return Part.ATTACHMENT.equalsIgnoreCase(disposition);
	}

	public boolean isMessagePart() {
		return isMimeType("message/rfc822");
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this <tt>WmaMessagePart</tt>.
	 * 
	 * @param description
	 *            of this <tt>WmaMessagePart</tt> as <tt>String</tt>.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this <tt>WmaMessagePart</tt>.
	 * 
	 * @param name
	 *            the name of this <tt>WmaMessagePart</tt> as <tt>String</tt>.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the size of this <tt>WmaMessagePart</tt>.
	 * 
	 * @param size
	 *            of this <tt>WmaMessagePart</tt> in bytes.
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of this <tt>WmaMessagePart</tt>.
	 * 
	 * @param size
	 *            of this <tt>WmaMessagePart</tt> in bytes.
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Creates a <tt>WmaMessagePartImpl</tt> instance from a given
	 * <tt>javax.mail.Part</tt> instance.
	 * 
	 * @param part
	 *            a <tt>javax.mail.Part</tt> instance.
	 * @param number
	 *            the number of the part as <tt>int</tt>.
	 * 
	 * @return the newly created instance.
	 * @throws WmaException
	 *             if it fails to create the new instance.
	 */
	public static WmaMessagePart createWmaMessagePart(Part part, int number)
			throws WmaException {
		WmaMessagePartImpl partinfo = new WmaMessagePartImpl(part, number);
		try {
			// content type
			partinfo.setContentType(part.getContentType());
			// size
			int size = part.getSize();
			if (part.isMimeType("message/rfc822")) {
				partinfo.setSize(size);
				Message msg = (Message) part.getContent();
				partinfo.setName(WmaUtils.prepareString(WmaUtils.getHeader(msg,
						"Subject")));
			} else {
				// correct size of encoded parts
				String[] encoding = part.getHeader("Content-Transfer-Encoding");
				if (encoding != null
						&& encoding.length > 0
						&& (encoding[0].equalsIgnoreCase("base64") || encoding[0]
								.equalsIgnoreCase("uuencode"))) {
					// an base64 encoded file is 25% larger in reality,
					// and adds additional two bytes('\r' and '\n') for every 78
					// characters, so correct the size
					size = (int) ((size - ((size / 78) * 2)) * 0.75);
				}
				partinfo.setSize(size);
				// description
				partinfo.setDescription(part.getDescription());
				// disposition
				partinfo.setDisposition(part.getDisposition());
				// filename
				partinfo.setName(WmaUtils.prepareString(part.getFileName()));
			}
		} catch (Exception ex) {
			throw new WmaException("wma.messagepart.failedcreation")
					.setException(ex);
		}
		return partinfo;
	}

}
