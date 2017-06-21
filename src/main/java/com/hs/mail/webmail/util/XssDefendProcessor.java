package com.hs.mail.webmail.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class XssDefendProcessor {

	public String process(String str) {
		return Jsoup.clean(str, Whitelist.relaxed());
	}
	
}
