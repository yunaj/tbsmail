package com.hs.mail.webmail.model.impl;

import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hs.mail.webmail.WmaAuthenticator;
import com.hs.mail.webmail.config.MailTransportAgent;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaTransport;

public class WmaTransportImpl implements WmaTransport {
	
	private static Logger log = LoggerFactory.getLogger(WmaTransportImpl.class);

	private Transport transport;

	private WmaTransportImpl(Transport transport) {
		this.transport = transport;
	}

	/**
     * @return Returns the transport.
     */
	public Transport getTransport() {
		return transport;
	}

	public void connect() throws MessagingException {
		if (!transport.isConnected()) {
			transport.connect();
			log.debug("Connected to transport: {}", 
					transport.getURLName().toString());
		}
	}

	public void close() {
		if (transport != null) {
			try {
				transport.close();
			} catch (MessagingException e) {
			}
		}
	}

	public void sendMessage(Message msg) throws MessagingException {
		connect();
		// ensure that a sent date is set
		msg.setSentDate(new Date());
		try {
			transport.sendMessage(msg, msg.getAllRecipients());
		} finally {
			close();
		}
	}

	public static WmaTransportImpl createWmaTransportImpl(Session mailSession,
			MailTransportAgent mta, WmaAuthenticator authenticator)
			throws WmaException {
		Transport transport = null;
		try {
			if (mta.isAuthenticated()) {
				transport = mailSession.getTransport(new URLName(mta
						.getProtocol(), mta.getAddress(), mta.getPort(), null,
						authenticator.getUserName(), authenticator
								.getPassword()));
			} else {
				transport = mailSession.getTransport(new URLName(mta
						.getProtocol(), mta.getAddress(), mta.getPort(), null,
						null, null));
			}
			return new WmaTransportImpl(transport);
		} catch (Exception ex) {
			throw new WmaException("session.login.authentication")
					.setException(ex);
		}
	}

}
