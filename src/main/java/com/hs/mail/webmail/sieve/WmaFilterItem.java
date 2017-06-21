package com.hs.mail.webmail.sieve;

public class WmaFilterItem {

	private String match;
	private String subject;
	private String sender;

	private String action;
	private String actionparam;

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionparam() {
		return actionparam;
	}

	public void setActionparam(String actionparam) {
		this.actionparam = actionparam;
	}

}
