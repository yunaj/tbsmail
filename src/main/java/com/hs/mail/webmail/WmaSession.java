package com.hs.mail.webmail;

import java.io.Serializable;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.config.MailTransportAgent;
import com.hs.mail.webmail.config.PostOffice;
import com.hs.mail.webmail.dao.PreferencesDAO;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.model.WmaTransport;
import com.hs.mail.webmail.model.impl.WmaPreferencesImpl;
import com.hs.mail.webmail.model.impl.WmaStoreImpl;
import com.hs.mail.webmail.model.impl.WmaTransportImpl;
import com.hs.mail.webmail.search.Query;
import com.hs.mail.webmail.search.QueryBuilder;

public class WmaSession implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(WmaSession.class);

	private static final String WMA_AUTH = "wma.auth";
	private static final String WMA_STORE = "wma.store";
	private static final String WMA_QUERY = "wma.query";
	
	transient private WmaPreferences prefs;

	transient private HttpSession httpsession;

	public WmaSession(HttpSession httpsession) {
		super();
		this.httpsession = httpsession;
	}
	
	public WmaAuthenticator getAuthenticator() {
		return (WmaAuthenticator) retrieveBean(WMA_AUTH);
	}

	public WmaStore getWmaStore() {
		return (WmaStore) retrieveBean(WMA_STORE);
	}
	
	public Store getStore() throws WmaException {
		WmaAuthenticator authenticator = getAuthenticator();
		Store store = connect(getMailSession(), authenticator.getUserName(),
				authenticator.getPassword());
		return store;
	}
	
	public Query getQuery(HttpServletRequest request) throws ServletException {
		if (request != null) {
			Query query = QueryBuilder.build(request);
			storeBean(WMA_QUERY, query);
			return query;
		} else {
			return (Query) retrieveBean(WMA_QUERY);
		}
	}
	
	public PostOffice getPostOffice() {
		return (PostOffice) Configuration.getBean("postOffice");
	}
	
	public Session getMailSession() {
		return initMailSession(getAuthenticator());
	}
	
	private boolean isDebugSession() {
		return "true".equals(Configuration.getProperty("wma.session.debug"));
	}

	protected Session initMailSession(Authenticator authenticator) {
		Session mailSession = Session.getInstance(System.getProperties(),
				authenticator);
		if (isDebugSession()) {
			mailSession.setDebug(true);
		}
		return mailSession;
	}

	private void endMailSession() {
		WmaStore wstore = getWmaStore();
		if (wstore != null) {
			wstore.close();
			wstore = null;
		}
	}

	protected Store connect(Session mailSession, String username, String password)
			throws WmaException {
		try {
			PostOffice postOffice = getPostOffice();
			Store store = mailSession.getStore(postOffice.getProtocol());
			store.connect(postOffice.getAddress(), postOffice.getPort(),
					username, password);
			return store;
		} catch (AuthenticationFailedException afe) {
			mailSession = null;
			throw new WmaException("wma.session.authentication");
		} catch (MessagingException e) {
			mailSession = null;
			throw new WmaException("wma.session.initmail");
		}
	}
	
	public WmaStore connect(String username, String password)
			throws WmaException {
		WmaAuthenticator authenticator = new WmaAuthenticator(username,
				password);
		storeBean(WMA_AUTH, authenticator);
		Store store = connect(initMailSession(authenticator), username,
				password);
		WmaStore wstore = WmaStoreImpl.createStore(this, store);
		storeBean(WMA_STORE, wstore);
		return wstore;
	}
	
	public boolean checkPassword(String password) {
		WmaAuthenticator authenticator = (WmaAuthenticator) retrieveBean(WMA_AUTH);
		return password.equals(authenticator.getPassword());
	}

	/**
	 * Returns a reference to the associated preferences.
	 * 
	 * @return the associated preferences <tt>WmaPreferences</tt>.
	 * @see com.hs.wmail.model.WmaPreferences
	 */
	public WmaPreferences getPreferences() {
		if (prefs == null) {
			PreferencesDAO dao = Configuration.getPreferencesDAO();
			String identity = getUserIdentity();
			try {
				prefs = dao.getPreferences(identity);
			} catch (WmaException e) {
				log.error(e.getMessage(), e);
			}
			if (prefs == null) {
				prefs = new WmaPreferencesImpl(identity);
			}
		}
		return prefs;
	}
	
	public void savePreferences(WmaPreferences prefs) throws WmaException {
		if (prefs != null) {
			PreferencesDAO dao = Configuration.getPreferencesDAO();
			dao.savePreferences(prefs);
			prefs = null;
		}
	}
	
	public void storeBean(String name, Object bean) {
		httpsession.setAttribute(name, bean);
	}
	
	public Object retrieveBean(String name) {
		return httpsession.getAttribute(name);
	}
	
	public void removeBean(String name) {
		httpsession.removeAttribute(name);
	}

	public void end() {
		endMailSession();
		if (httpsession != null) {
			removeBean(WMA_AUTH);
			removeBean(WMA_STORE);
			removeBean(WMA_QUERY);
			httpsession.invalidate();
		}
	}
	
	public WmaTransport getTransport() throws WmaException {
		MailTransportAgent mta = (MailTransportAgent) Configuration
				.getBean("mta");
		WmaAuthenticator authenticator = getAuthenticator();
		Session mailSession = initMailSession(authenticator);
		return WmaTransportImpl.createWmaTransportImpl(mailSession, mta,
				authenticator);
	}
	
	/**
	 * Returns the assembled user's identity as <tt>String</tt>.
	 * 
	 * @return the identifier assembled like a standard email address using the
	 *         set username and hostname.
	 */
	public String getUserIdentity() {
		String username = getAuthenticator().getUserName();
		if (username.indexOf('@') == -1) {
			if (StringUtils.isNotEmpty(getPostOffice().getDomain())) {
				return new StringBuffer(username).append('@')
						.append(getPostOffice().getDomain()).toString();
			} else {
				return new StringBuffer(username).append('@')
						.append(getPostOffice().getAddress()).toString();
			}
		} else {
			return username;
		}
	}
	
}
