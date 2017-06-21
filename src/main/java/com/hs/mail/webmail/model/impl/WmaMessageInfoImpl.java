package com.hs.mail.webmail.model.impl;

import java.util.Date;

import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaMessageInfo;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaMessageInfoImpl implements WmaMessageInfo {
	
	// logging
	private static Logger log = LoggerFactory.getLogger(WmaMessageInfoImpl.class);	

	// instance attributes
	private int number;
	private long uid;
	private boolean read;
	private boolean answered;
	private boolean recent;
	private boolean deleted;
	private boolean flagged;
	private boolean draft;
	private boolean secure;
	private boolean received;
	private boolean multipart;
	private int priority;
	private Date sentDate;
	private Date receivedDate;
	private String from;
	private String to;
	private String subject;
	private int size;
	
	/**
	 * Constructs a new <tt>WmaMessageInfoImpl</tt>.
	 * @param uid 
	 */
	protected WmaMessageInfoImpl(long uid) {
		setUID(uid);
	}

	public int getNumber() {
		return number;
	}

	/**
	 * Sets the message number of this MessageInfo. This method is public to
	 * allow caching in the <tt>JwmaMessageInfoListImpl</tt>. The number should
	 * reflect the number of the wrapped <tt>javax.mail.Message</tt> instance.
	 * 
	 * @param number
	 *            the number as <tt>int</tt>.
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	public long getUID() {
		return uid;
	}

	public void setUID(long uid) {
		this.uid = uid;
	}

	public boolean isRead() {
		return read;
	}

	/**
	 * Sets the read flag of this MessageInfo. It flags if the wrapped message
	 * was already read.
	 * 
	 * @param b
	 *            true if read, false otherwise.
	 */
	public void setRead(boolean b) {
		this.read = b;
	}

	public boolean isAnswered() {
		return answered;
	}

	/**
	 * Sets the answered flag of this MessageInfo. It flags if the wrapped
	 * message was answered.
	 * 
	 * @param b
	 *            true if answered, false otherwise.
	 */
	public void setAnswered(boolean b) {
		this.answered = b;
	}

	public boolean isRecent() {
		return recent;
	}

	public void setRecent(boolean recent) {
		this.recent = recent;
	}

	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Sets the deleted flag of this MessageInfo. It flags if the wrapped
	 * message was deleted.
	 * 
	 * @param b
	 *            true if deleted, false otherwise.
	 */
	public void setDeleted(boolean b) {
		this.deleted = b;
	}

	public boolean isDraft() {
		return draft;
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlagged(boolean flagged) {
		this.flagged = flagged;
	}

	/**
	 * Sets the draft flag of this MessageInfo. It flags if the wrapped message
	 * is a draft.
	 * 
	 * @param b
	 *            true if draft, false otherwise.
	 */
	public void setDraft(boolean b) {
		this.draft = b;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean b) {
		this.secure = b;
	}

	public boolean isReceived() {
		return received;
	}

	public boolean isMultipart() {
		return multipart;
	}
	
	public void setMultipart(boolean multipart) {
		this.multipart = multipart;
	}

	/**
	 * Sets the received flag of this MessageInfo. Flags if the wrapped message
	 * was received.
	 * 
	 * @param b
	 *            true if received, false otherwise.
	 */
	public void setReceived(boolean b) {
		this.received = b;
	}

	public boolean isSent() {
		return !received;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public Date getSentDate() {
		return sentDate;
	}

	/**
	 * Sets the sent date of this MessageInfo.
	 * 
	 * @param d
	 *            the date when the wrapped message was sent.
	 */
	public void setSentDate(Date d) {
		this.sentDate = d;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	/**
	 * Sets the received date of this MessageInfo.
	 * 
	 * @param d
	 *            the date when the wrapped message was received.
	 */	
	public void setReceivedDate(Date d) {
		this.receivedDate = d;
	}

	public Date getDate() {
		return (isReceived()) ? getReceivedDate() : getSentDate();
	}

	public String getFrom() {
		return from;
	}

	/**
	 * Sets the author's address of this MessageInfo.
	 * 
	 * @param from
	 *            the address of the author as <tt>String</tt>.
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	/**
	 * Sets the receiver's address(es) of this MessageInfo.
	 * 
	 * @param to
	 *            the address(es) of the receiver(s) as <tt>String</tt>.
	 */
	public void setTo(String to) {
		this.to = to;
	}

	public String getWho() {
		return (isReceived()) ? getFrom() : getTo();
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the subject of this MessageInfo. Note that the subject will be set
	 * to an empty string, if the given String is null, or if it cannot be
	 * decoded.
	 * 
	 * @param subject
	 *            the subject of the message as <tt>String</tt>.
	 */	
	public void setSubject(String subject) {
		this.subject = (null != subject) ? subject : "";
	}

	public int getSize() {
		return size;
	}

	/**
	 * Sets the message size for this MessageInfo.
	 * 
	 * @param bytes
	 *            the size of the message in bytes.
	 */
	public void setSize(int bytes) {
		this.size = bytes;
	}

	protected void prepare(Message msg) throws Exception {
		// set flags
		Flags flags = msg.getFlags();
		setNumber(msg.getMessageNumber());
		setRecent(flags.contains(Flag.RECENT));
		setRead(flags.contains(Flag.SEEN));
		setAnswered(flags.contains(Flag.ANSWERED));
		setDeleted(flags.contains(Flag.DELETED));
		setFlagged(flags.contains(Flag.FLAGGED));
		setDraft(flags.contains(Flag.DRAFT));

		// determine if received, will have a header named Received
		setReceived((msg.getHeader("Received") != null));

		// priority
		setPriority(WmaUtils.getPriority(msg));
		// secure flag
		setSecure("true".equals(WmaUtils.getHeader(msg, "X-Secure", "false")));

		// senders and receivers
		if (msg.getFrom() != null) {
			setFrom(WmaUtils.prepareString(InternetAddress.toString(msg
					.getFrom())));
		}
		setTo(WmaUtils.prepareString(InternetAddress.toString(msg
				.getRecipients(Message.RecipientType.TO))));

		// dates
		setReceivedDate(msg.getReceivedDate());
		setSentDate(msg.getSentDate());

		// subject
		setSubject(WmaUtils.prepareString(WmaUtils.getHeader(msg, "Subject")));

		// size
		setSize(msg.getSize());

		// attachments or none
		setMultipart(msg.isMimeType("multipart/mixed")
				|| msg.isMimeType("multipart/report"));
	}
	
	/**
	 * Factoy method that creates a new <tt>WmaMessageInfoImpl</tt> instance.
	 * The passed in message should have been loaded with a slim profile. Values
	 * are extracted and set in the newly created instance.
	 * 
	 * @param msg
	 *            the message to be wrapped as <tt>javax.mail.Message</tt>.
	 */
	public static WmaMessageInfo createMessageInfo(long uid, Message msg)
			throws WmaException {
		WmaMessageInfoImpl messageinfo = null;
		try {
			messageinfo = new WmaMessageInfoImpl(uid);
			messageinfo.prepare(msg);
			return messageinfo;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return messageinfo;
		}
	}

}
