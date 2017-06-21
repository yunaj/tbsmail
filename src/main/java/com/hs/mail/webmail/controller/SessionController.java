package com.hs.mail.webmail.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.WmaQuota;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.util.RequestUtils;

@Controller
public class SessionController {

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestParam("username") String username,
			@RequestParam("password") String password, Model model,
			HttpSession httpsession) {
		WmaSession session = new WmaSession(httpsession);
		// authenticate user
		WmaStore store = null;
		try {
			store = session.connect(username, password);
		} catch (WmaException e) {
			model.addAttribute("error", e);
			return "login";
		}
		WmaPreferences prefs = session.getPreferences();
		// we have now a created store
		model.addAttribute("session", store);
		model.addAttribute("store", store);
		model.addAttribute("prefs", prefs);
		model.addAttribute("namespaces", store.getSharedNamespaces());
		return "main";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession httpsession) {
		try {
			return "login";
		} finally {
			WmaSession session = new WmaSession(httpsession);
			session.end();
		}
	}

	@RequestMapping(value = "/authcheck", method = RequestMethod.GET)
	public String password(HttpSession httpsession) {
		return "password";
	}

	@RequestMapping(value = "/authcheck", method = RequestMethod.POST)
	@ResponseBody
	public boolean password(@RequestParam("password") String password, HttpSession httpsession) {
		WmaSession session = new WmaSession(httpsession);
		return session.checkPassword(password);
	}

	@RequestMapping(value = "/quota", method = RequestMethod.GET)
	@ResponseBody
	public WmaQuota[] quota(HttpSession httpsession, HttpServletRequest request) {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getParameter(request, "path", "");
		WmaStore store = session.getWmaStore();
		return store.getQuota(path);
	}
	
}
