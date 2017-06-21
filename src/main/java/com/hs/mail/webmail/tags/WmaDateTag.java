package com.hs.mail.webmail.tags;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.fmt.FormatDateSupport;

public class WmaDateTag extends FormatDateSupport {

	private static final long serialVersionUID = 1L;

    //*********************************************************************
    // Private constants

    private static final String YEAR = "yyyy-MM-dd";
    private static final String DATE = "MM-dd";
    private static final String TIME = "HH:mm";

    //*********************************************************************
    // Constructor and initialization

	public WmaDateTag() {
	super();
	}

    //*********************************************************************
    // Tag attributes known at translation time
	public void setValue(Date value) {
		this.value = value;
	}
	
    //*********************************************************************
    // Tag logic

    /*
     * Formats the given date and time.
     */
	
	@Override
	public int doEndTag() throws JspException {
		if (value == null) {
			return EVAL_PAGE;	
		}

		Calendar date = Calendar.getInstance();
        int nowYear = date.get(Calendar.YEAR);
        int nowMonth = date.get(Calendar.MONTH);
        int nowDay = date.get(Calendar.DATE);
        
        date.setTime(value);
        int rYear = date.get(Calendar.YEAR);
        int rMonth = date.get(Calendar.MONTH);
        int rDay = date.get(Calendar.DATE);
		
		if (rYear != nowYear) {
			pattern = YEAR;
		} else if (rMonth != nowMonth || rDay != nowDay) {
			pattern = DATE;
		} else {
			pattern = TIME;
		}
		return super.doEndTag();
	}

}
