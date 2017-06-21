package com.hs.mail.webmail.tags;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.resources.Resources;

public class WmaSizeTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	protected static final String KB = "1KB"; 
	
	protected static final String[] units = { "bytes", "KB", "MB", "GB", "TB" };
	
	protected Object value;
	
	public WmaSizeTag() {
		super();
		init();
	}

	private void init() {
		value = null;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public int doEndTag() throws JspException {
		String formatted = null;
		Double input = null;

		if ((value == null) || value.equals("")) {
			return EVAL_PAGE;
		}

		try {
		if (value instanceof String) {
			input = Double.valueOf((String) value);
		} else if (!(value instanceof Double)) {
			input = Double.valueOf(value.toString());
		}
		} catch (NumberFormatException nfe) {
		throw new JspException(
				Resources.getMessage("FORMAT_NUMBER_PARSE_ERROR", input), nfe);
		}
		
		int i = 0;
		for (; i <= units.length; i++) {
			if (input < 1024) {
				break;
			} else {
				input /= 1024;
			}
		}
		
		if (0 == i) {
			formatted = new StringBuilder(1).append(units[1]).toString();
		} else {
			// Create formatter since DecimalFormat is not thread safe.
			NumberFormat formatter = new DecimalFormat("0");
			formatted = new StringBuilder().append(formatter.format(input))
					.append(units[i]).toString();
		}
		
		try {
			pageContext.getOut().print(formatted);
		} catch (IOException ioe) {
			throw new JspTagException(ioe.toString(), ioe);
		}
		
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		init();
		super.release();
	}
	
}
