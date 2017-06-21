package com.hs.mail.webmail.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class WmaException extends Exception {

	private static final long serialVersionUID = -6571922363172323891L;

	private Exception exception;

	public WmaException(String msg) {
		super(msg);
	}
	
	public WmaException(Exception ex) {
		setException(ex);
	}

	public Exception getException() {
		return exception;
	}

	public WmaException setException(Exception ex) {
		exception = ex;
		return this;
	}
	
	public boolean hasException() {
		return (exception != null);
	}
	
	public String getExceptionTrace() {
		if (hasException()) {
			StringWriter trace = new StringWriter();
			exception.printStackTrace(new PrintWriter(trace));
			return trace.toString();
		} else {
			return "";
		}
	}

}
