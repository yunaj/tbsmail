package com.hs.mail.webmail.util;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MailDateFormat;
import javax.mail.internet.MimeUtility;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import com.hs.mail.webmail.exception.WmaException;

public class WmaUtils {
	
	private static final String ISO_8859_1 = "ISO-8859-1";

	public static final FastDateFormat DEFAULT_DATETIME_FORMAT = FastDateFormat
			.getInstance("yyyy-MM-dd HH:mm:ss");

	public static final FastDateFormat SIMPLE_DATE_FORMAT = FastDateFormat
			.getInstance("yyyyMMddHHmm");
	
	private WmaUtils() {
	}
	
	public static String formatDate(Date date) {
		return DEFAULT_DATETIME_FORMAT.format(date);
	}
	
	public static Date parseDate(String str, String pattern)
			throws WmaException {
		DateFormat fmt = new SimpleDateFormat(pattern);
		try {
			return fmt.parse(str);
		} catch (ParseException pe) { // warn to user
			throw new WmaException("jwma.date.format");
		}
	}
	
	public static boolean isHangul(String text) {
		char[] ach = text.toCharArray();
		for (int i = 0; i < ach.length; i++) {
			if (ach[i] >= '\uAC00' && ach[i] <= '\uD7A3') // hangul
				return true;
		}
		return false;
	}
	
	private static String getEncoding(String text)
			throws UnsupportedEncodingException {
		String[] preferred = { "ISO-8859-1", "EUC-KR", "KSC5601" };
		for (int i = 0; i < preferred.length; i++) {
			if (isHangul(new String(text.getBytes(preferred[i])))) {
				return preferred[i];
			}
		}
		return ISO_8859_1;
	}
	
	private static String fixEncoding(String str) {
		// IBM-eucKR is database codeset of "euc-kr"
		if (str.indexOf("IBM-euc") > 0) return str.replaceAll("IBM-euc", "euc-");
		if (str.indexOf("ks_c_5601-1987") > 0) return str.replaceAll("ks_c_5601-1987", "euc-kr");
		if (str.indexOf("ks_c_5601") > 0) return str.replaceAll("ks_c_5601", "euc-kr");
		if (str.indexOf("iso-2022-kr") > 0) return str.replaceAll("iso-2022-kr", "euc-kr");
		if (str.indexOf("?==?") > 0) return StringUtils.replaceOnce(str, "?==?", "?= =?");
		return str;
	}

	/**
	 * Method that prepares the given <tt>String</tt> by decoding it through the
	 * <tt>MimeUtility</tt> and encoding it through the <tt>EntitiyHandler</tt>.
	 */
	public static String prepareString(String str) throws Exception {
		if (null == str) {
			return "";
		} else {
			try {
				int i = -1;
				if ((i = str.indexOf("=?")) > -1) {
					return MimeUtility
							.decodeText(fixEncoding(str.substring(i)));
				} else {
					return new String(str.getBytes(getEncoding(str)));
				}
			} catch (UnsupportedEncodingException skip) {
				// ignore
			}
			return str;
		}
	}
	
	public static String getHeader(Part p, String name)
			throws MessagingException {
		String[] headers = p.getHeader(name);
		return (headers != null) ? headers[0] : null;
	}
	
	public static String getHeader(Part p, String name, String defaultValue) {
		try {
			return getHeader(p, name);
		} catch (MessagingException e) {
			return defaultValue;
		}
	}
	
	public static Date getReceivedDate(Message msg) {
		try {
			Date d = msg.getReceivedDate();
			if (null == d) {
				String[] s = msg.getHeader("Received");
				if (null == s) {
					if ((s = msg.getHeader("Date")) == null) {
						return null;
					}
				}
				// RFC 2822 - Internet Message Format
				// received = "Received:" name-val-list ";" date-time CRLF
				StringTokenizer st = new StringTokenizer(s[0], "\n");
				while (st.hasMoreTokens()) {
					s[0] = st.nextToken();
				}
				s[0] = s[0].substring(s[0].indexOf(";") + 1).trim();
				MailDateFormat mdf = new MailDateFormat();
				d = mdf.parse(s[0]);
			}
			return d;
		} catch (ParseException e) {
			return null;
		} catch (MessagingException mes) {
			return null;
		}
	}
	
	public static int getPriority(Message msg) {
		try {
			String p = getHeader(msg, "X-Priority");
			if (p != null)
				return Integer.parseInt(p.substring(0, 1));
		} catch (Exception e) {
		}
		return 3; // 3 (Normal)
	}
	
	public static Flags getFlags(String flag) {
		return (Flags) flagMap.get(flag);
	}
	
	public static FetchProfile getFetchProfile() {
		FetchProfile fp = new FetchProfile();
		fp.add(FetchProfile.Item.ENVELOPE); // contains the headers
		fp.add(FetchProfile.Item.FLAGS); // contains the flags
		fp.add(FetchProfile.Item.CONTENT_INFO); // contains the content types
		// fetch header values
		fp.add("Subject");
		fp.add("Received");
		fp.add("X-Priority");
		fp.add("X-Secure");
		return fp;
	}
	
	static private Map<String, Flags> flagMap = new Hashtable<String, Flags>();
	// declare supported message flags
	static {
		flagMap.put("answered", new Flags(Flags.Flag.ANSWERED));
		flagMap.put("deleted", new Flags(Flags.Flag.DELETED));
		flagMap.put("draft", new Flags(Flags.Flag.DRAFT));
		flagMap.put("flagged", new Flags(Flags.Flag.FLAGGED));
		flagMap.put("recent", new Flags(Flags.Flag.RECENT));
		flagMap.put("seen", new Flags(Flags.Flag.SEEN));
	}

}
