package com.hs.mail.webmail.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.impl.WmaPreferencesImpl;
import com.hs.mail.webmail.sieve.SieveScriptUtils;
import com.hs.mail.webmail.sieve.WmaFilterItem;

@Controller
public class PrefsController {

	@RequestMapping(value = "/prefs", method = RequestMethod.GET)
	public String showPrefsForm(Model model, HttpSession httpsession) {
		WmaSession session = new WmaSession(httpsession);
		WmaPreferences prefs = session.getPreferences();
		model.addAttribute("prefs", prefs);
		return "prefs";
	}
	
	@RequestMapping(value = "/prefs", method = RequestMethod.POST)
	public String savePrefsForm(
			@ModelAttribute("prefs") WmaPreferencesImpl prefs,
			HttpSession httpsession) throws WmaException {
		WmaSession session = new WmaSession(httpsession);
		session.savePreferences(prefs);
		return "saved";
	}
	
	@RequestMapping(value = "/prefs/filters/create", method = RequestMethod.GET)
	public String showFilterForm() {
		return "filter";
	}
	
	@RequestMapping(value = "/prefs/filters", method = RequestMethod.GET)
	@ResponseBody
	public WmaFilterItem[] getFilters(HttpSession httpsession) {
		WmaSession session = new WmaSession(httpsession);
		List<WmaFilterItem> items = SieveScriptUtils.readScript(session
				.getUserIdentity());
		return (items != null) 
				? items.toArray(new WmaFilterItem[items.size()])
				: null;
	}
	
	@RequestMapping(value = "/prefs/filters", method = RequestMethod.POST)
	public String saveFilterForm(HttpSession httpsession,
			HttpServletRequest request) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String[] filters = request.getParameterValues("filters");
		List<WmaFilterItem> items = parseFilters(filters);
		SieveScriptUtils.writeScript(session.getUserIdentity(), items);
		return "saved";
	}
	
	private List<WmaFilterItem> parseFilters(String[] filters)
			throws IOException {
		List<WmaFilterItem> items = new ArrayList<WmaFilterItem>();
		ObjectMapper mapper = new ObjectMapper();
		for (String filter : filters) {
			if (StringUtils.isNotBlank(filter)) { // skip template value
				items.add(mapper.readValue(filter, WmaFilterItem.class));
			}
		}
		return items;
	}
	
}
