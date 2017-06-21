/*
 * Copyright 2010 the original author or authors.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.hs.mail.webmail.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Won Chul Doh
 * @since Feb 17, 2007
 *
 */
public class RequestUtils {

	public static String getParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				if ((StringUtils.isNotEmpty(request.getContentType()))
						&& (request.getContentType()
								.startsWith("multipart/form-data"))) {
					// If POST, do not decode.
					return value;
				} else {
					String encoding = request.getCharacterEncoding();
					// If the request.getCharacterEncoding() is null, the
					// default parsing value of the String is ISO-8859-1.
					return new String(
							value.getBytes((null == encoding) ? "ISO-8859-1"
									: encoding), "UTF-8");
				}
			} catch (Exception ex) {
			}
		}
		return value;
	}

	public static String getParameter(HttpServletRequest request, String name,
			String defaultValue) {
		String value = getParameter(request, name);
		return (value != null) ? value : defaultValue;
	}

	public static String getRequiredParameter(HttpServletRequest request,
			String name) throws ServletException {
		String value = getParameter(request, name);
		if (value == null) {
			throw new ServletException("Required " + String.class.getName()
					+ " parameter '" + name + "' is not present");
		}
		return value;
	}

	public static boolean getParameterBool(HttpServletRequest request,
			String name) {
		String value = getParameter(request, name);
		return Boolean.valueOf(value).booleanValue();
	}

	public static boolean getParameterBool(HttpServletRequest request,
			String name, boolean defaultValue) {
		String value = getParameter(request, name, Boolean
				.toString(defaultValue));
		return Boolean.valueOf(value).booleanValue();
	}

	public static int getParameterInt(HttpServletRequest request, String name)
			throws ServletException {
		String value = getParameter(request, name);
		if (value != null) {
			return toInt(value.trim());
		} else {
			throw new ServletException("Required " + Integer.class.getName()
					+ " parameter '" + name + "' is not present");
		}
	}

	public static int getParameterInt(HttpServletRequest request, String name,
			int defaultValue) {
		try {
			return getParameterInt(request, name);
		} catch (Exception ex) {
			return defaultValue;
		}
	}
	
	public static int[] getParameterInts(HttpServletRequest request,
			String name) throws ServletException {
		String[] values = request.getParameterValues(name);
		return (values != null) ? toInts(values) : null;
	}

	public static long getParameterLong(HttpServletRequest request, String name)
			throws ServletException {
		String value = getParameter(request, name);
		if (value != null) {
			return toLong(value.trim());
		} else {
			throw new ServletException("Required " + Long.class.getName()
					+ " parameter '" + name + "' is not present");
		}
	}

	public static long getParameterLong(HttpServletRequest request, String name,
			long defaultValue) {
		try {
			return getParameterLong(request, name);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static String[] getParameterValues(HttpServletRequest request,
			String name) {
		String[] values = request.getParameterValues(name);
		if (values != null) {
			try {
				String encoding = request.getCharacterEncoding();
				for (int i = 0; i < values.length; i++) {
					values[i] = (null == values[i]) 
						? null 
						: new String(values[i].getBytes((null == encoding) 
								? "ISO-8859-1" : encoding), "UTF-8");
				}
			} catch (Exception ex) {
			}
		}
		return values;
	}
	
	public static long[] getParameterLongs(HttpServletRequest request,
			String name) throws ServletException {
		String[] values = request.getParameterValues(name);
		return (values != null) ? toLongs(values) : null;
	}

	public static int toInt(String number) throws ServletException {
		try {
			return Integer.parseInt(number);
		} catch (Exception ex) {
			throw new ServletException("Failed to input string '" + number
					+ "' to requied type [" + Integer.class.getName() + "]");
		}
	}

	public static long toLong(String number) throws ServletException {
		try {
			return Long.parseLong(number);
		} catch (Exception ex) {
			throw new ServletException("Failed to input string '" + number
					+ "' to requied type [" + Long.class.getName() + "]");
		}
	}

	public static int[] toInts(String[] values) throws ServletException {
		int[] numbers = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			numbers[i] = toInt(values[i].trim());
		}
		return numbers;
	}

	public static long[] toLongs(String[] values) throws ServletException {
		long[] numbers = new long[values.length];
		for (int i = 0; i < values.length; i++) {
			numbers[i] = toLong(values[i].trim());
		}
		return numbers;
	}

	public static String getBrowser(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent.contains("Trident") || userAgent.contains("MSIE")) {
			return "MSIE";
		} else if (userAgent.contains("Opera") || userAgent.contains("OPR")) {
			return "Opera";
		} else if (userAgent.contains("Firefox")) {
			return "Firefox";
		} else if (userAgent.contains("Chrome")) {
			return "Chrome";
		} else if (userAgent.contains("Safari")) {
			return "Safari";
		} else {
			return "Unknown";
		}
	}

}
