package com.hs.mail.webmail.model.impl;

import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;
import javax.mail.internet.MimeUtility;

import org.springframework.web.multipart.MultipartFile;

import com.hs.mail.webmail.model.WmaAttachment;
import com.hs.mail.webmail.util.MultipartFileDataSource;

public class WmaMultipartFileAttach implements WmaAttachment {

    // instance attributes
    private MultipartFileDataSource dataSource;
    private MultipartFile mf;
    private String encoding;

    public WmaMultipartFileAttach(MultipartFile mf, String encoding) {
        this.mf = mf;
        this.encoding = encoding;
    }

	public String getContentType() {
		return mf.getContentType();
	}

	public DataSource getDataSource() {
		if (null == dataSource) {
            String filename = mf.getOriginalFilename();
            // normally without file the control should be not successful.
            // but neither netscape nor mircosoft iexploder care much.
            // the only feature is an empty filename.
            if ("".equals(filename)) {
                // kick it out
            } else {
                dataSource = new MultipartFileDataSource(filename, mf);
            }
		}
		return dataSource;
	}

	public String getDisposition() {
		return ATTACHMENT;
	}

	public String getName() {
        String filename = mf.getOriginalFilename();
        // IExploder sends files with complete path.
        // wma doesnt want this.
        int lastindex = filename.lastIndexOf("\\");
        if (lastindex != -1) {
            filename = filename.substring(lastindex + 1, filename.length());
        }
        String name = filename;
        try {
            name = MimeUtility.encodeText(filename, encoding,"B");
        } catch (UnsupportedEncodingException uex) {
        }
        return name;
	}

}
