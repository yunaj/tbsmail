package com.hs.mail.webmail.model;

public class WmaResource {

	/** The name of the resource. */
	private String name;

	/** The current usage of the resource. */
	private long usage;

	/** The usage limit for the resource. */
	private long limit;

	public WmaResource(String name, long usage, long limit) {
		this.name = name;
		this.usage = usage;
		this.limit = limit;
	}

	public String getName() {
		return name;
	}

	public long getUsage() {
		return usage;
	}

	public long getLimit() {
		return limit;
	}
	
}
