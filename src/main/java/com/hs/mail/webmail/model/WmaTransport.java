package com.hs.mail.webmail.model;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

public interface WmaTransport {

	Transport getTransport();

	void connect() throws MessagingException;

	void close() throws MessagingException;

	void sendMessage(Message msg) throws MessagingException;

}
