package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.List;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaMessageInfoImpl;
import com.hs.mail.webmail.util.Pager;
import com.hs.mail.webmail.util.WmaUtils;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.SortTerm;

public class WmaMessageInfoList {

	private static Logger log = LoggerFactory.getLogger(WmaMessageInfoList.class);
	
	public static WmaMessageInfoList EMPTY_LIST = new WmaMessageInfoList();

	// instance attributes
	protected List<WmaMessageInfo> messageInfos;

	/**
	 * Constructs a new <tt>WmaMessageInfoList</tt>.
	 */
	private WmaMessageInfoList() {
	}

	public List<WmaMessageInfo> getMessageInfos() {
		return messageInfos;
	}

	/**
	 * Returns the size of this list.
	 * 
	 * @return the size of this list.
	 */
	public int size() {
		return messageInfos.size();
	}
	
	/**
	 * Builds the list of <tt>WmaMessageInfoImpl</tt> instances from the given
	 * array of messages.
	 * @param uids 
	 * 
	 * @param msgs
	 *            array of <tt>javax.mail.Message</tt> instances.
	 * 
	 * @throws WmaException
	 *             if it fails to create a <tt>JwmaMessageInfoImpl</tt>
	 *             instance.
	 */
	private void buildMessageInfoList(long[] uids, Message[] msgs, boolean asc)
			throws WmaException {
		this.messageInfos = new ArrayList<WmaMessageInfo>(msgs.length);
		WmaMessageInfo msginfo = null;
		if (asc) {
			for (int i = 0; i < msgs.length; i++) {
				msginfo = WmaMessageInfoImpl.createMessageInfo(uids[i], msgs[i]);
				addMessageInfo(msginfo);
			}
		} else {
			for (int i = msgs.length - 1; i >= 0; i--) {
				msginfo = WmaMessageInfoImpl.createMessageInfo(uids[i], msgs[i]);
				addMessageInfo(msginfo);
			}
		}
	}

	private void addMessageInfo(WmaMessageInfo msginfo) {
		if (msginfo != null) {
			this.messageInfos.add(msginfo);
		}
	}
	
	/**
	 * Factory method that creates a new <tt>WmaMessageInfoListImpl</tt>
	 * instance from the given array of messages.
	 * @param uids 
	 * 
	 * @param msgs
	 *            array of <tt>javax.mail.Message</tt> instances.
	 * 
	 * @return the newly created <tt>WmaMessageInfoListImpl</tt> instance.
	 * 
	 * @throws WmaException
	 *             if it fails to build the list.
	 */
	private static WmaMessageInfoList createWmaMessageInfoList(long[] uids, Message[] msgs,
			boolean asc) throws WmaException {
		WmaMessageInfoList msglist = new WmaMessageInfoList();
		msglist.buildMessageInfoList(uids, msgs, asc);
		return msglist;
	}

	/**
	 * Factory method that creates a new <tt>WmaMessageInfoList</tt>
	 * instance wrapping the list of messages in the given folder.
	 * 
	 * @param f
	 *            the <tt>javax.mail.Folder</tt> instance, the new list instance
	 *            should be created for.
	 * 
	 * @return the newly created <tt>WmaMessageInfoList</tt> instance.
	 * 
	 * @throws WmaException
	 *             if it fails retrieve the list of <tt>javax.mail.Message</tt>
	 *             instances from the folder, or when it fails to build the
	 *             list.
	 * @throws MessagingException 
	 */
	private static Message[] getMessages(Folder f, Pager pager) 
			throws MessagingException {
		int total = f.getMessageCount();
		if (total == 0) {
			return null;
		}
		// Both start and end inclusive
		pager.setItemCount(total);
		return f.getMessages(pager.getBegin() + 1, pager.getEnd() + 1);
	}
	
	private static Message[] getMessages(Folder f, SearchTerm term, 
			SortTerm[] sortterm, Pager pager) throws MessagingException {
		Message[] msgs = null;
		if (sortterm != null) {
			msgs = ((IMAPFolder) f).getSortedMessages(sortterm, term);
		} else {
			msgs = f.search(term);
		}
		if (msgs == null) {
			return null;
		}

		pager.setItemCount(msgs.length);
		// end index is exclusive
		return (Message[]) ArrayUtils.subarray(msgs, pager.getBegin(), pager.getEnd() + 1);
	}
	
	private static long[] getUIDList(IMAPFolder f, Message[] mesgs) {
		long[] uids = new long[mesgs.length];
		for (int i = 0; i < mesgs.length; i++) {
			try {
				uids[i] = f.getUID(mesgs[i]);
			} catch (MessagingException e) {
				uids[i] = -1;
			}
		}
		return uids;
	}
	
	public static WmaMessageInfoList createWmaMessageInfoList(Folder f,
			SearchTerm term, SortTerm[] sortterm, Pager pager)
			throws WmaException {
		try {
			// for listing only
			if (!f.isOpen()) {
				f.open(Folder.READ_ONLY);
			}
			Message[] mesgs = null;
			if (term != null || sortterm != null) {
				mesgs = getMessages(f, term, sortterm, pager);
			} else {
				mesgs = getMessages(f, pager);
			}
			if (mesgs == null) {
				return EMPTY_LIST;
			}
			
			// fetch messages with a slim profile
			FetchProfile fp = WmaUtils.getFetchProfile();
			fp.add(UIDFolder.FetchProfileItem.UID);
			f.fetch(mesgs, fp);

			long[] uids = getUIDList((IMAPFolder) f, mesgs);
			return createWmaMessageInfoList(uids, mesgs, pager.isAscending());
		} catch (MessagingException mex) {
			log.error(mex.getMessage(), mex);
			throw new WmaException("wma.messagelist.failedcreation")
					.setException(mex);
		} finally {
			try {
				// close the folder
				if (f.isOpen()) {
					f.close(false);
				}
			} catch (MessagingException mesx) {
				// don't care, the specs say it IS closed anyway
			}
		}
	}

}
