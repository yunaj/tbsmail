package com.hs.mail.webmail.tags;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.jsp.JspException;

import org.apache.taglibs.standard.tag.common.core.OutSupport;

public class WmaAddressTag extends OutSupport {

	private static final long serialVersionUID = 1L;

	private Object value_;
	private int maxLength = 0;
	
	public WmaAddressTag() {
		super();
		init();
	}
	
    public void setValue(String value_) {
        this.value_ = value_;
    }
	
    public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

    private void init() {
		value_ = null;
		maxLength = 0;
	}

	@Override
	public void release() {
		super.release();
		init();
	}
	
	@Override
	public int doStartTag() throws JspException {
		transform();
		return super.doStartTag();
	}
	
	private void transform() {
		if (value_ != null) {
			String addresslist = value_.toString();
			try {
				StringBuilder buffer = new StringBuilder(addresslist.length());
				InternetAddress[] addresses = InternetAddress.parse(addresslist);
				for (int i = 0; i < addresses.length; i++) {
					if (i > 0) {
						if (maxLength == i) {
							buffer.append(",...");
							break;
						}
						buffer.append(',');
					}
					String personal = addresses[i].getPersonal();
					buffer.append((personal != null) ? personal : addresses[i].getAddress());
				}
				value = buffer.toString();
			} catch (AddressException e) {
				value = value_;
			}
		}
	}

}
