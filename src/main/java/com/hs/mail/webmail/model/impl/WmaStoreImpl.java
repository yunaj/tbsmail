package com.hs.mail.webmail.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Quota;
import javax.mail.Store;
import javax.mail.UIDFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaQuota;
import com.hs.mail.webmail.model.WmaResource;
import com.hs.mail.webmail.model.WmaStore;
import com.sun.mail.imap.IMAPStore;

public class WmaStoreImpl implements WmaStore {
	
	// logging
	private static Logger log = LoggerFactory.getLogger(WmaStoreImpl.class);

	// instance attributes
	private WmaSession session;
	private Store store;
	private char folderSeparator = '.';

	// special folders
	private WmaFolder inboxFolder;
	private WmaFolder trashFolder;
	private WmaFolder draftFolder;
	private WmaFolder sentMailFolder;
	private WmaFolder toSendFolder;
	private WmaFolder personalFolder;

	/**
	 * Constructs a <tt>WmaStoreImpl</tt> instance.
	 * 
	 * @param session the <tt>WmaSession</tt> instance this store
	 *        belongs to.
	 */
	public WmaStoreImpl(WmaSession session, Store store) {
		this.session = session;
		this.store = store;
	}
	
	/**
     * @return Returns the session.
     */
    public WmaSession getSession() {
        return session;
    }
    
    /**
	 * @return Returns the store.
	 */
	public Store getStore() {
		return store;
	}

	public char getFolderSeparator() {
		return folderSeparator;
	}

    public void setFolderSeparator(char folderSeparator) {
		this.folderSeparator = folderSeparator;
	}

	/*** wma special folders **********************************/

    public Folder getRootFolder() throws MessagingException {
		return store.getDefaultFolder();
	}

	public WmaFolder getInboxInfo() {
		return inboxFolder;
	}

	public WmaFolder getTrashInfo() {
		return trashFolder;
	}

	private Folder getTrashFolder() throws WmaException {
		String name = session.getPreferences().getTrashFolder();
		return getFolder(name);
	}

	private void setTrashFolder() throws WmaException, MessagingException {
		Folder trash = getTrashFolder();
		if (!trash.exists()) {
			if (!trash.create(WmaFolderImpl.TYPE_MAILBOX)) {
				throw new WmaException("wma.store.createfolder.failed");
			}
		}
		// ensure subscription
		trash.setSubscribed(true);
		trashFolder = WmaFolderImpl.createLight(trash);
	}

	public WmaFolder getDraftInfo() {
		return draftFolder;
	}

	private Folder getDraftFolder() throws WmaException {
		String name = session.getPreferences().getDraftFolder();
		return getFolder(name);
	}

	private void setDraftFolder() throws WmaException, MessagingException {
		Folder draft = getDraftFolder();
		if (!draft.exists()) {
			if (!draft.create(WmaFolderImpl.TYPE_MAILBOX)) {
				throw new WmaException("wma.store.createfolder.failed");
			}
		}
		// ensure subscription
		draft.setSubscribed(true);
		draftFolder = WmaFolderImpl.createLight(draft);
	}

	public WmaFolder getSentMailArchive() {
		return sentMailFolder;
	}

	private Folder getSentMailFolder() throws WmaException {
		String name = session.getPreferences().getSentMailArchive();
		return getFolder(name);
	}
	
	private void setSentMailFolder() throws WmaException, MessagingException {
		Folder sentMail = getSentMailFolder();
		if (!sentMail.exists()) {
			if (!sentMail.create(WmaFolderImpl.TYPE_MAILBOX)) {
				throw new WmaException("wma.store.createfolder.failed");
			}
		}
		// ensure subscription
		sentMail.setSubscribed(true);
		sentMailFolder = WmaFolderImpl.createLight(sentMail);
	}

	public WmaFolder getToSendArchive() {
		return toSendFolder;
	}

	private Folder getToSendFolder() throws WmaException {
		String name = session.getPreferences().getToSendFolder();
		return getFolder(name);
	}

	private void setToSendFolder() throws WmaException, MessagingException {
		Folder toSend = getToSendFolder();
		if (!toSend.exists()) {
			if (!toSend.create(WmaFolderImpl.TYPE_MAILBOX)) {
				throw new WmaException("wma.store.createfolder.failed");
			}
		}
		// ensure subscription
		toSend.setSubscribed(true);
		toSendFolder = WmaFolderImpl.createLight(toSend);
	}

	public WmaFolder getPersonalArchive() {
		return personalFolder;
	}

	private Folder getPersonalFolder() throws WmaException {
		String name = session.getPreferences().getPersonalFolder();
		return getFolder(name);
	}

	private void setPersonalFolder() throws WmaException, MessagingException {
		Folder personal = getPersonalFolder();
		if (!personal.exists()) {
			if (!personal.create(WmaFolderImpl.TYPE_MIXED)) {
				throw new WmaException("wma.store.createfolder.failed");
			}
		}
		// ensure subscription
		personal.setSubscribed(true);
		personalFolder = WmaFolderImpl.createLight(personal);
	}
	
	public void archiveMail(Folder archive, Message message)
			throws WmaException {
		archiveMail(archive, message, UIDFolder.LASTUID);
	}

	public void archiveMail(Folder archive, Message message, long uid)
			throws WmaException {
		boolean replace = (uid != UIDFolder.LASTUID);
		try {
			// open it read write
			archive.open(Folder.READ_WRITE);
			if (replace) {
				// existing message will be updated
				Message orgmsg = ((UIDFolder) archive).getMessageByUID(uid);
				// mark old deleted
				orgmsg.setFlag(Flags.Flag.DELETED, true);
			}
			// save the message in archive, append only works as array
			Message[] tosave = { message };
			// append it
			archive.appendMessages(tosave);
			// close with or without expunging
			archive.close(replace);
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.archivemail.failed")
					.setException(mex);
		} finally {
			if (archive != null) {
				try {
					if (archive.isOpen()) {
						// close without expunging
						archive.close(false);
					}
				} catch (Exception e) {
					// ignore, will be closed anyway
				}
			}
		}
	}

	/*** end wma special folders ******************************/

	/**
	 * Closes the associated mail store.
	 */
	public void close() {
		try {
			store.close();
		} catch (Exception mex) {
			log.error(mex.getMessage(), mex);
		}
	}

	/*** folder management methods *****************************/

	/**
	 * Returns a <tt>WmaFolderImpl</tt> with the given path
	 * from the store.
	 * 
	 * @return the folder as <tt>WmaFolderImpl</tt>.
	 * @throws WmaException
	 */
	public WmaFolder getWmaFolder(String fullname) throws WmaException {
		return WmaFolderImpl.createLight(getFolder(fullname));
	}

	public Folder getFolder(String fullname) throws WmaException {
		try {
			// FIXME: Microsoft Exchange returns "" as Default Folder, but
			// asking for the folder "" does not return any subfolders.
			if (fullname.length() > 0) {
				return store.getFolder(fullname);
			} else {
				// assume to return the default folder...
				return store.getDefaultFolder();
			}
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.getfolder").setException(mex);
		}
	}

	public Folder createFolder(String fullname, int type) throws WmaException {
		try {
			Folder newfolder = getFolder(fullname);
			if (newfolder.exists()) {
				throw new WmaException("wma.store.createfolder.exists");
			}
			newfolder.create(type);
			// ensure new folder is subscribed
			newfolder.setSubscribed(true);
			return newfolder;
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.createfolder.failed")
					.setException(mex);
		}
	}

	public Folder renameFolder(String fullname, String destfolder)
			throws WmaException {
		try {
			Folder newfolder = getFolder(destfolder);
			if (newfolder.exists()) {
				throw new WmaException(
						"wma.store.movefolder.destination.missing");
			}
			Folder oldfolder = getFolder(fullname);
			// UW does not change subscriptions on moving!
			if (oldfolder.isSubscribed()) {
				oldfolder.setSubscribed(false);
			}
			oldfolder.renameTo(newfolder);
			newfolder.setSubscribed(true);
			return newfolder;
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.createfolder.failed")
					.setException(mex);
		}
	}
	
	public void emptyFolder(String fullname) throws WmaException {
		Folder folder = null;
		try {
			folder = getFolder(fullname);
			folder.open(Folder.READ_WRITE);
			Message[] msgs = folder.getMessages();
			folder.setFlags(msgs, new Flags(Flags.Flag.DELETED), true);
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.emptyfolder.failed")
					.setException(mex);
		} finally {
			if (folder != null) {
				try {
					folder.close(true);
				} catch (MessagingException e) {
				}
			}
		}
	}

	public void deleteFolder(String fullname) throws WmaException {
		try {
			Folder delfolder = getFolder(fullname);
			if (isSpecialFolder(fullname)) {
				throw new WmaException("wma.store.deletefolder.systemfolder");
			} else {
				// UW does not update subscriptions
				delfolder.setSubscribed(false);
				delfolder.delete(true);
			}
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.deletefolder.failed")
					.setException(mex);
		}
	}

	public void deleteFolders(String[] folders) throws WmaException {
		for (int i = 0; i < folders.length; i++) {
			deleteFolder(folders[i]);
		}
	}

	public List<String> moveFolders(String[] foldernames, String destfolder)
			throws WmaException {
		try {
			Folder dest = getFolder(destfolder);
			// ensure existing destination folder
			if (!dest.exists()) {
				throw new WmaException(
						"wma.store.movefolder.destination.missing");
			}
			// ensure basically valid destination
			if (!(WmaFolder.TYPE_FOLDER == dest.getType()
					|| WmaFolder.TYPE_MIXED == dest.getType())) {
				throw new WmaException("wma.store.movefolder.destination.foul");
			}
			// create list that does not contain any special folder
			List<Folder> folders = getFolders(foldernames);
			List<String> newFolders = new ArrayList<String>();
			for (int i = 0; i < folders.size(); i++) {
				Folder oldfolder = (Folder) folders.get(i);
				Folder newfolder = getFolder(destfolder + getFolderSeparator()
						+ oldfolder.getName());
				if (newfolder.exists()) {
					throw new WmaException(
							"wma.store.movefolder.destination.exists");
				}
				// UW does not change subscriptions on moving!
				if (oldfolder.isSubscribed()) {
					oldfolder.setSubscribed(false);
				}
				oldfolder.renameTo(newfolder);
				newfolder.setSubscribed(true);
				
				newFolders.add(newfolder.getFullName());
			}
			return newFolders;
		} catch (MessagingException mex) {
			throw new WmaException("wma.store.movefolder.failed")
					.setException(mex);
		}
	}

	/**
	 * An utility method that collects all non special
	 * folders of a given array of folder paths, into
	 * a list of jvax.mail.Folder instances.
	 */
	private List<Folder> getFolders(String[] foldernames)
			throws MessagingException, WmaException {
		List<Folder> folders = new ArrayList<Folder>(foldernames.length);
		Folder f = null;
		for (int i = 0; i < foldernames.length; i++) {
			f = getFolder(foldernames[i]);
			// add if existant and NOT the trash folder
			if (f.exists() && !isSpecialFolder(foldernames[i])) {
				folders.add(f);
			}
		}
		return folders;
	}

	public boolean isSpecialFolder(String fullname) {
		return ("".equals(fullname)
				|| fullname.equals(inboxFolder.getPath())
				|| fullname.equals(trashFolder.getPath())
				|| fullname.equals(draftFolder.getPath())
				|| fullname.equals(sentMailFolder.getPath())
				|| fullname.equals(toSendFolder.getPath())
				|| fullname.equals(personalFolder.getPath()));
	}

	private void prepare() throws WmaException {
		try {
			Folder root = getRootFolder();

			if (!root.exists()) {
				throw new WmaException("wma.store.rootfolder");
			}
			// set folder separator
			setFolderSeparator(root.getSeparator());

			// Inbox the folder that contains the incoming mail
			// this has to exist, regarding to the IMAP specification
			inboxFolder = getWmaFolder("INBOX");

			// Trash
			setTrashFolder();
			
			// Draft
			setDraftFolder();
			
			// sent-mail archive
			setSentMailFolder();
			
			// send-mail archive
			setToSendFolder();
			
			// Personal
			setPersonalFolder();
		} catch (MessagingException ex) {
			throw new WmaException("wma.store.prepare").setException(ex);
		}
	}
	
	/**
	 * Creates a new <tt>WmaStore</tt> instance.
	 * 
	 * @param session the actual <tt>WmaSession</tt>.
	 * @param store the mail store that should be wrapped.
	 * @return the newly created <tt>WmaStore</tt> instance.
	 * @throws WmaException 
	 * 
	 * @see com.hs.mail.webmail.WmaSession
	 * @see javax.mail.Store
	 */
	public static WmaStore createStore(WmaSession session, Store store)
			throws WmaException {
		WmaStoreImpl wstore = new WmaStoreImpl(session, store);
		// prepare this store
		wstore.prepare();
		return wstore;
	}
	
	public WmaQuota[] getQuota(String root) {
		try {
			Quota[] quotas = ((IMAPStore) store).getQuota(root);
			WmaQuota[] wmaQuotas = new WmaQuota[quotas.length];
			for (int i = 0; i < quotas.length; i++) {
				wmaQuotas[i] = new WmaQuota(); 
				Quota.Resource[] resources = quotas[i].resources;
				for (int j = 0; j < resources.length; j++) {
					WmaResource wr = new WmaResource(resources[j].name, resources[j].usage,
							resources[j].limit);
					wmaQuotas[i].setResource(wr);
				}
			}
			return wmaQuotas;
		} catch (MessagingException e) {
			// Server doesn't support the QUOTA extension
			return new WmaQuota[0];
		}
	}
	
	public WmaFolder[] getSharedNamespaces() {
		try {
			Folder[] ns = store.getSharedNamespaces();
			WmaFolder[] wns = new WmaFolder[ns.length];
			for (int i = 0; i < ns.length; i++) {
				wns[i] = WmaFolderImpl.createNamespace(ns[i]);
			}
			return wns;
		} catch (Exception e) {
			// ignore it
			return new WmaFolder[0];
		}
	}

	public Folder getSharedNamespace(String name) throws WmaException {
		try {
			Folder[] ns = store.getSharedNamespaces();
			for (Folder folder : ns) {
				if (folder.getName().equals(name)) {
					return folder;
				}
			}
			// Namespace not found
			throw new WmaException("wma.store.getfolder");
		} catch (MessagingException ex) {
			throw new WmaException("wma.store.getfolder").setException(ex);
		}
	}

}
