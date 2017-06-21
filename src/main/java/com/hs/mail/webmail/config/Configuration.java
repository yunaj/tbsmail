package com.hs.mail.webmail.config;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.hs.mail.webmail.dao.PreferencesDAO;

public class Configuration implements InitializingBean, ApplicationContextAware {

	private static Properties properties = new Properties();

	private static MessageSourceAccessor messageSourceAccessor;
	
	private static ApplicationContext appContext;
	
	public static ApplicationContext getApplicationContext() {
		return appContext;
	}

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		appContext = context;
	}

	/**
	 * This is about the same as context.getBean("beanName"), except it has its
	 * own static handle to the Spring context, so calling this method
	 * statically will give access to the beans by name in the Spring
	 * application context. As in the context.getBean("beanName") call, the
	 * caller must cast to the appropriate target class. If the bean does not
	 * exist, then a Runtime error will be thrown.
	 * 
	 * @param beanName
	 *            the name of the bean to get.
	 * @return an Object reference to the named bean.
	 */
	public static Object getBean(String beanName) throws BeansException {
		return appContext.getBean(beanName);
	}

	public static <T> T getBean(Class<T> type) throws BeansException {
		return appContext.getBean(type);
	}

	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		messageSourceAccessor = accessor;
	}
	
	public static String getMessage(String code) {
		return messageSourceAccessor.getMessage(code);
	}
	
	public static String getMessage(String code, Object[] args) {
		return messageSourceAccessor.getMessage(code, args);
	}
	
	public void setProperties(Properties props) {
		properties = props;
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static PreferencesDAO getPreferencesDAO() {
		return (PreferencesDAO) getBean(PreferencesDAO.class);
	}
	
	public static File getUserHome(String identity) {
		String user = getUser(identity);
		String host = getHost(identity);
		StringBuilder sb = new StringBuilder(
				(host != null) ? host : getProperty("postoffice.domain"))
				.append(File.separator)
				.append("users")
				.append(File.separator)
				.append(user.charAt(0))
				.append(user.charAt(user.length() - 1))
				.append(File.separator)
				.append(user);
		return new File(getProperty("wma.data.path"), sb.toString());
	}
	
	public static File getDeferDir(String dateStr) {
		return new File(getProperty("wma.data.path"), "defer");
	}

	public void afterPropertiesSet() throws Exception {
		Enumeration<?> e = properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			if (key.startsWith("mail.")) {
				System.setProperty(key, properties.getProperty(key));
			}
		}
	}

	private static String getHost(String address) {
		int index = address.lastIndexOf('@');
		if (index != -1) {
			String host = address.substring(index + 1).toLowerCase();
			if (host.charAt(0) == '[')
				host = host.substring(1, host.length() - 1);
			return host;
		}
		return null;
	}

	private static String getUser(String address) {
		int index = address.lastIndexOf('@');
		return (index != -1) ? address.substring(0, index) : address;
	}

}
