package com.hs.mail.webmail.model;

import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;

import com.hs.mail.webmail.exception.WmaException;

public interface WmaStore {

	/**
	 * @return Returns the store.
	 */
	Store getStore();

	char getFolderSeparator();

	/*** wma special folders **********************************/

	Folder getRootFolder() throws MessagingException;

	/**
	 * Returns the <tt>WmaFolder</tt> instance that can be used to retrieve
	 * information about the store's INBOX folder (i.e. where new messages
	 * should be arriving).
	 * 
	 * @return the store's INBOX folder as <tt>WmaFolder</tt>.
	 */
	WmaFolder getInboxInfo();

	/**
	 * Returns the <tt>WmaFolder</tt> instance that can be used to retrieve
	 * information about the store's trash folder (i.e. where deleted messages
	 * end up first).
	 * 
	 * @return the store's trash folder as <tt>WmaFolder</tt>.
	 */
	WmaFolder getTrashInfo();

	WmaFolder getDraftInfo();

	WmaFolder getSentMailArchive();

	WmaFolder getToSendArchive();

	WmaFolder getPersonalArchive();

	WmaFolder getWmaFolder(String fullname) throws WmaException;
	
	/**
	 * Put's a message into the read-mail archive, if archiving is enabled.
	 * 
	 * @param message
	 *            the <tt>Message</tt> to be archived.
	 * 
	 * @throws WmaException
	 *             if it fails to archive the message.
	 * 
	 * @see javax.mail.Message
	 */
	void archiveMail(Folder archive, Message message, long uid)
			throws WmaException;

	void archiveMail(Folder archive, Message message) throws WmaException;

	/*** end wma special folders ******************************/

	/**
	 * Closes the associated mail store.
	 */
	void close();

	/*** folder management methods *****************************/

	/**
	 * Returns a <tt>Folder</tt> with a given path from the mail store.
	 * 
	 * @return the folder as <tt>Folder</tt>.
	 * @throws WmaException
	 *             if a folder with the given path does not exist on the store
	 *             or a MessagingException occurs.
	 */
	Folder getFolder(String fullname) throws WmaException;

	/**
	 * Creates a new folder on the store.
	 * 
	 * @throws WmaException
	 *             if the folder already exists, or if it fails to create the
	 *             folder.
	 */
	Folder createFolder(String fullname, int type) throws WmaException;

	/**
	 * Rename a folder to given name.
	 * @return 
	 * 
	 * @throws WmaException
	 *             if the folder already exists, or if it fails to rename the
	 *             folder.
	 */
	Folder renameFolder(String fullname, String destfolder) throws WmaException;

	/**
	 * Empty a given folder.
	 * 
	 * @param fullname
	 *            the folder's path as <tt>String</tt>.
	 * 
	 * @throws WmaException
	 *             if a folder does not exist, or if an error occurs when
	 *             emptying.
	 */
	void emptyFolder(String fullname) throws WmaException;
	
	/**
	 * Deletes a given folders from the store.
	 * <p>
	 * Note that this method will not remove the folder if it is a special
	 * folder.
	 * 
	 * @param fullname
	 *            the folder's path as <tt>String</tt>.
	 * 
	 * @throws WmaException
	 *             if a folder does not exist, or if an error occurs when
	 *             deleting.
	 */
	void deleteFolder(String fullname) throws WmaException;

	/**
	 * Deletes the given folders from the store.
	 * <p>
	 * Note that this method will not remove any special folder from the store.
	 * Despite that, it is a convenience method, looping over the array and
	 * calling <tt>deleteFolder()</tt>
	 * 
	 * @param folders
	 *            an array of strings; each <tt>String</tt> representing the
	 *            full path of a valid folder of the actual store.
	 * 
	 * @throws WmaException
	 *             if a folder does not exist, or if an error occurs when
	 *             deleting.
	 * 
	 * @see #deleteFolder(String)
	 */
	void deleteFolders(String[] folders) throws WmaException;

	/**
	 * Moves the given folders to the given destination folder.
	 * 
	 * @param foldernames
	 *            an array of strings; each <tt>String</tt> representing the
	 *            full path of a valid folder of the actual store.
	 * @param destfolder
	 *            the full path of a valid folder of the actual store.
	 * 
	 * @throws WmaException
	 *             if the source folders or the destination folder do not exist,
	 *             the destination is a subfolder of a source folder, the
	 *             destination cannot contain any subfolders, or if an error
	 *             occurs when moving.
	 */
	List<String> moveFolders(String[] foldernames, String destfolder)
			throws WmaException;

	/**
	 * Tests if a given path is a special jwma folder.
	 */
	boolean isSpecialFolder(String fullname);

	public WmaQuota[] getQuota(String root);
	
	public WmaFolder[] getSharedNamespaces();
	
	public Folder getSharedNamespace(String name) throws WmaException;

}
