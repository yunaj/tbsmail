package com.hs.mail.webmail.util;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

public class WmaAddress {

	public static Address[] parse(String addresslist) {
		if (addresslist == null) {
			return null;
		}

		String[] addresses = StringUtils.split(addresslist, ",;");
		List<Address> list = new ArrayList<Address>();
		for (String address : addresses) {
			try {
				list.add(parseAddress(address));
			} catch (Exception e) {
			}
		}
		return list.toArray(new Address[list.size()]);
	}
	
	public static Address parseAddress(String s) throws Exception {
		int start = s.indexOf('<');
		if (start == -1) {
			return new InternetAddress(s);
		}
		return new InternetAddress(strip(s.substring(start), '<', '>'), // address 
				strip(s.substring(0, start), '"', '"')); // personal
	}
	
	private static String strip(final String str, final char startChar, final char endChar) {
		int start = 0, end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}
		
		// strip start
		while (start < end && Character.isWhitespace(str.charAt(start))) {
			start++;
		}
		if (start < end && str.charAt(start) == startChar) {
			start++;
		}

		// strip end
		while (end > start && Character.isWhitespace(str.charAt(end - 1))) {
			end--;
		}
		if (end > start && str.charAt(end - 1) == endChar) {
			end--;
		}
		return str.substring(start, end);
	}
	
}
