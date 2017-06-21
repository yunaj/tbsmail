package com.hs.mail.webmail.model;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;


public class FancytreeNode implements Comparable<FancytreeNode> {
	
	private Folder folder;
	
	private List<FancytreeNode> children;

	public FancytreeNode(Folder folder) {
		this.folder = folder;
	}

	public String getTitle() {
		return folder.getName();
	}

	public String getKey() {
		return folder.getFullName();
	}

	public boolean isExpanded() {
		return true;
	}

	public boolean isFolder() {
		return true;
	}

	public FancytreeNode[] getChildren() {
		return (children != null) 
				? children.toArray(new FancytreeNode[children.size()]) 
				: null;
	}

	public FancytreeNode addChild(FancytreeNode child) {
		if (children == null) {
			children = new ArrayList<FancytreeNode>();
		}
		children.add(child);
		return child;
	}
	
	public boolean isParent(FancytreeNode child, char separator) {
		return child.getKey().startsWith(getKey() + separator);
	}
	
	@Override
	public int compareTo(FancytreeNode o) {
		return getKey().compareTo(o.getKey());
	}

}
