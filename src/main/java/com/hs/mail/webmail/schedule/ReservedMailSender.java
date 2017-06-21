package com.hs.mail.webmail.schedule;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.mail.MessagingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.InitializingBean;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.model.impl.WmaSendReservation;
import com.hs.mail.webmail.util.WmaUtils;

public class ReservedMailSender implements InitializingBean {

	private File watchDir = null;
	
	public void send() {
		String basetime = WmaUtils.SIMPLE_DATE_FORMAT.format(new Date());
		File[] files = watchDir.listFiles();
		if (ArrayUtils.isNotEmpty(files)) {
			for (File file : files) {
				String datetime = FilenameUtils.removeExtension(file.getName());
				WmaSendReservation sr = null;
				if (isAfter(basetime, datetime)) {
					try {
						sr = WmaSendReservation.readFrom(file);
						WmaSendReservation.send(sr);
					} catch (MessagingException e) {
						// connection is dead or not in the connected state
					} catch (Exception e) {
					} finally {
						forceDelete(file);
					}
				}
			}
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		watchDir = Configuration.getDeferDir(null);
	}
	
	private static void forceDelete(File file) {
		try {
			FileUtils.forceDelete(file);
		} catch (IOException e) {
		}
	}
	
	private static boolean isAfter(String basetime, String datetime) {
		return basetime.compareTo(datetime) >= 0;
	}
	
}
