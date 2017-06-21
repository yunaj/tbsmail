package com.hs.mail.webmail.search;

import java.io.Serializable;

import javax.mail.search.SearchTerm;

import com.sun.mail.imap.SortTerm;

public class Query implements Serializable {

	private static final long serialVersionUID = 1L;

	private SearchTerm searchterm;
	private SortTerm[] sortterm;
	private boolean ascending = false;

	public Query(SearchTerm searchterm, SortTerm[] sortterm, boolean ascending) {
		this.searchterm = searchterm;
		this.sortterm = sortterm;
		if (sortterm != null) {
			this.ascending = ascending;
		}
	}

	public SearchTerm getSearchTerm() {
		return searchterm;
	}

	public SortTerm[] getSortTerm() {
		return sortterm;
	}

	public boolean isAscending() {
		return ascending;
	}

}
