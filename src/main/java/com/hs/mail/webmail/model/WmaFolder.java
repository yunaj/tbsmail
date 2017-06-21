package com.hs.mail.webmail.model;

import java.io.InputStream;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaDisplayMessage;
import com.sun.mail.imap.IMAPFolder;

/**
 * An interface defining the contract for interaction with
 * the WmaFolder model.
 * <p>
 * The JwmaFolder allows a view programmer to obtain
 * information about a folder.
 * 
 * @author WonChul,Do
 *
 */
public interface WmaFolder {

	/**
	 * Returns this folder's wrapped mail folder instance.
	 * 
	 * @return wrapped instance as <tt>javax.mail.Folder</tt>.
	 */
	IMAPFolder getFolder();

	/**
	 * Returns a <tt>String</tt> representing the name
	 * of this folder.
	 * 
	 * @return the name of this folder as String.
	 */
	String getName();
	
	/**
	 * Returns a <tt>String</tt> representing the path
	 * of this folder object.
	 * 
	 * @return the path of this folder as String.
	 */
	String getPath();
	
	/**
	 * Returns an <tt>int</tt> representing the type
	 * of this folder.
	 * 
	 * @return the type of this folder object as <tt>int</tt>.
	 */
	int getType();
	
	boolean isParent(WmaFolder child, char separator);

	int getMessageCount();

	WmaDisplayMessage getWmaMessage(int num) throws WmaException;
	
	/**
	 * Returns a <tt>WmaDisplayMessage</tt> instance that wraps the mail message
	 * with the given number.
	 * 
	 * @param uid
	 *            the UID of the message to be retrieved as <tt>long</tt>.
	 * @return the <tt>WmaDisplayMessage</tt> instance wrapping the retrieved
	 *         message.
	 * @throws WmaException
	 *             if the message does not exist, or cannot be retrieved from
	 *             the store.
	 */
	WmaDisplayMessage getWmaMessageByUID(long uid) throws WmaException;
	
	long getUID(int num, int offset) throws WmaException;

	/**
	 * Deletes the messages with the given numbers.
	 * 
	 * @param uids
	 *            array of UIDs
	 * @param purge
	 *            true if delete permanently
	 * 
	 * @throws WmaException
	 *             if it fails to delete any of the given messages.
	 */
	void deleteMessages(WmaStore wstore, long[] uids, boolean purge)
			throws WmaException;

	/**
	 * Copy the messages with the given numbers to the given destination folder.
	 * 
	 * @param uids
	 *            array of UIDs
	 * @param destfolder
	 *            the destination folder path as <tt>String</tt>.
	 * 
	 * @throws WmaException
	 *             if it fails to copy if the destination folder does not exist,
	 *             or if any of the given messages cannot be copied.
	 */
	void copyMessages(WmaStore wstore, long[] uids, String destfolder)
			throws WmaException;

	/**
	 * Move the messages with the given numbers to the given destination folder.
	 * 
	 * @param uids
	 *            array of UIDs
	 * @param destfolder
	 *            the destination folder path as <tt>String</tt>.
	 * 
	 * @throws WmaException
	 *             if it fails to move if the destination folder does not exist,
	 *             or if any of the given messages cannot be moved.
	 */
	void moveMessages(WmaStore wstore, long[] uids, String destfolder)
			throws WmaException;

	void appendMessage(InputStream source) throws WmaException;
	
	void setFlagMessages(long[] uids, Flags flags, boolean set)
			throws WmaException;
	
	void writeMessagePart(HttpServletRequest request,
			HttpServletResponse response, long uid, int part)
			throws WmaException;
	
	void writeMimeMessage(HttpServletResponse response, long uid, String filename)
			throws WmaException;

    /**
	 * Defines folder type that can only hold messages.
	 */
	static final int TYPE_MAILBOX = Folder.HOLDS_MESSAGES;
	
	/**
	 * Defines folder type that can only hold folders.
	 */
	static final int TYPE_FOLDER = Folder.HOLDS_FOLDERS;
	
	/**
	 * Defines folder type that can hold messages and folders.
	 */
	static final int TYPE_MIXED = TYPE_MAILBOX + TYPE_FOLDER;
	
	/**
	 * Defines a virtual type that represents all folders that
	 * can hold messages.
	 */
	static final int TYPE_MESSAGE_CONTAINER = TYPE_MAILBOX + TYPE_MIXED;
	
	/**
	 * Defines a virtual type that represents all folders that can
	 * hold folders.
	 */
	static final int TYPE_FOLDER_CONTAINER = TYPE_FOLDER + TYPE_MIXED;
	
	/**
	 * Defines a virtual type that represents all of the above.
	 */
	static final int TYPE_ALL = 10;

}
