package com.hs.mail.webmail.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.springframework.web.multipart.MultipartFile;

public class MultipartFileDataSource implements DataSource {

	private MultipartFile mf;
	private String name;

	public MultipartFileDataSource(String name, MultipartFile mf) {
		this.name = name;
		this.mf = mf;
	}
	
	public String getContentType() {
		return mf.getContentType();
	}

	public InputStream getInputStream() throws IOException {
		return mf.getInputStream();
	}

	public String getName() {
		return name;
	}

	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Not supported.");
	}

}
