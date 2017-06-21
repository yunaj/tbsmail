package com.hs.mail.webmail.model.impl;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.util.WmaUtils;

public class WmaRecipient {

	/**
	 * address type
	 */
	private String type;
	
	/**
	 * email address
	 */
	private String address;

    /**
     * The personal name.
     */
	private String personal;

	private WmaRecipient() {
	}
	
	public String getType() {
		return type;
	}

	public String getAddress() {
		return address;
	}

	public String getPersonal() {
		return personal;
	}
	
	public String toString() {
		if (StringUtils.isNotEmpty(personal))
			return personal + " <" + address + ">";
		else
			return address;
	}

	public static WmaRecipient createWmaRecipient(InternetAddress address,
			Message.RecipientType type) throws WmaException {
		try {
			WmaRecipient recipient = new WmaRecipient();
			recipient.type = type.toString();
			recipient.address = address.getAddress();
			recipient.personal = WmaUtils.prepareString(address.getPersonal());
			return recipient;
		} catch (Exception e) {
			throw new WmaException(e);
		}
	}
	
}
