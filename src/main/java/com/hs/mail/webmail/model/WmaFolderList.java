package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.mail.Folder;
import javax.mail.MessagingException;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.impl.WmaFolderImpl;


public class WmaFolderList {

	/**
	 * Builds this list of folders from the given array of folders.
	 * If recursive, then it will build a flat list of the complete
	 * folder tree.
	 * @throws MessagingException 
	 */
	private static FancytreeNode[] buildFolderList(Folder folder,
			boolean recursive) throws MessagingException {
		Folder[] folders = folder.list(recursive ? "*" : "%");
		FancytreeNode[] nodes = new FancytreeNode[folders.length];
		for (int i = 0; i < folders.length; i++) {
			nodes[i] = new FancytreeNode(folders[i]);
		}
		return nodes;
	}
	
	private static void buildFolderTree(Stack<FancytreeNode> stack,
			FancytreeNode[] flist, char separator) {
		int i = 0;
		while (i < flist.length) {
			FancytreeNode parent = stack.peek();
			if (parent.isParent(flist[i], separator)) {
				parent.addChild(flist[i]);
				stack.push(flist[i++]);
			} else {
				do {
					stack.pop();
				} while (!stack.isEmpty() && !stack.peek().isParent(flist[i], separator));
			}
		}
	}
	
	/**
	 * Rebuilds this list of folders to tree.
	 */
	private static FancytreeNode[] rebuild(FancytreeNode folder, FancytreeNode[] subfolders, char separator) {
		Arrays.sort(subfolders);
		Stack<FancytreeNode> stack = new Stack<FancytreeNode>();
		stack.push(folder);
		buildFolderTree(stack, subfolders, separator);
		return folder.getChildren();
	}
	
	/**
	 * Factory method that creates a list of all subfolders of the given
	 * folder.
	 * 
	 * @param folder the <tt>Folder</tt> instance to be listed.
	 * @param recursive flags if the list should be build recursive.
	 * @return the newly created <tt>FancytreeNode</tt> instance.
	 * @throws WmaException if it fails to build the folder list.
	 */
	public static FancytreeNode[] createSubfolderList(Folder folder,
			boolean recursive, char separator) throws WmaException {
		try {
			FancytreeNode[] subfolders = buildFolderList(folder, recursive);
			if (recursive && subfolders.length > 0) {
				return rebuild(new FancytreeNode(folder), subfolders, separator);
			} else {
				return subfolders;
			}
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage());
		}
	}
	
	public static List<WmaFolder> createSubfolderList(Folder folder)
			throws WmaException {
		try {
			Folder[] folders = folder.list("*");
			List<WmaFolder> sublist = new ArrayList<WmaFolder>(folders.length);
			for (int i = 0; i < folders.length; i++) {
				sublist.add(WmaFolderImpl.createLight(folders[i]));
			}
			return sublist;
		} catch (MessagingException mex) {
			throw new WmaException(mex.getMessage());
		}
	}

}
