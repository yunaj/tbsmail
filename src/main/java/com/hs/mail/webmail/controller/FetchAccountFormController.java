package com.hs.mail.webmail.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.config.Configuration;
import com.hs.mail.webmail.dao.PreferencesDAO;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.fetch.FetchMailRunner;
import com.hs.mail.webmail.fetch.FetchMailRunner.Status;
import com.hs.mail.webmail.model.impl.WmaFetchAccount;

@Controller
public class FetchAccountFormController implements Validator {

	// Set a form validator
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(this);
	}
	
	@RequestMapping(value = "/prefs/accounts/{uid}", method = RequestMethod.GET)
	public String account(@PathVariable("uid") String uid, Model model,
			HttpSession httpsession) throws WmaException {
		WmaSession session = new WmaSession(httpsession);
		model.addAttribute(
				"fetchForm",
				"create".equals(uid) 
					? new WmaFetchAccount() 
					: getFetchAccount(session, uid));
		return "fetch";
	}

	@RequestMapping(value = "/prefs/accounts", method = RequestMethod.POST)
	public String update(
			@ModelAttribute("fetchForm") @Validated WmaFetchAccount fetch,
			BindingResult result, HttpSession httpsession) {
		if (result.hasErrors()) {
			return "fetch";
		}
		try {
			WmaSession session = new WmaSession(httpsession);
			String identity = session.getUserIdentity();
			PreferencesDAO dao = Configuration.getPreferencesDAO();
			dao.saveFetchAccount(identity, fetch);
			return "redirect:/prefs/accounts";
		} catch (WmaException e) {
			result.reject("error", e.getMessage());
			return "fetch";
		}
	}

	@RequestMapping(value = "/prefs/accounts", method = RequestMethod.GET)
	public String accounts(Model model, HttpSession httpsession)
			throws WmaException {
		WmaSession session = new WmaSession(httpsession);
		PreferencesDAO dao = Configuration.getPreferencesDAO();
		List<WmaFetchAccount> accounts = dao.getFetchAccounts(session
				.getUserIdentity());
		model.addAttribute("accounts", accounts);
		return "accounts";
	}
	
	@RequestMapping(value = "/prefs/accounts/delete", method = RequestMethod.POST)
	public String delete(@RequestParam(value = "uid") String uid,
			HttpSession httpsession) throws WmaException {
		WmaSession session = new WmaSession(httpsession);
		PreferencesDAO dao = Configuration.getPreferencesDAO();
		dao.deleteFetchAccount(session.getUserIdentity(), uid);
		return "redirect:/prefs/accounts";
	}
	
	@RequestMapping(value = "/prefs/accounts/fetch", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void fetch(@RequestParam(value = "uid") String uid,
			HttpSession httpsession) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		WmaFetchAccount account = getFetchAccount(session, uid);
		if (account == null) {
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
		}
		FetchMailRunner runner = new FetchMailRunner(
				session.getUserIdentity(), 
				account, 
				session.getStore(),
				"INBOX");
		runner.start();
	}
	
	@RequestMapping(value = "/prefs/accounts/status", method = RequestMethod.GET)
	@ResponseBody
	public Status status(HttpSession httpsession) {
		WmaSession session = new WmaSession(httpsession);
		Status status = FetchMailRunner.status(session.getUserIdentity());
		return (status != null) ? status : Status.NULL;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return WmaFetchAccount.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "serverName", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "field.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
	}
	
	private WmaFetchAccount getFetchAccount(WmaSession session, String uid)
			throws WmaException {
		PreferencesDAO dao = Configuration.getPreferencesDAO();
		return dao.getFetchAccount(session.getUserIdentity(), uid);
	}
	
}
