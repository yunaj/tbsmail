package com.hs.mail.webmail.model;

import javax.activation.DataSource;

public interface WmaAttachment {

    public static final String ATTACHMENT = "attachment";

    public static final String INLINE = "inline";
    
    public String getName();
    
    public String getContentType();
    
    public String getDisposition();
    
    public DataSource getDataSource();

}
