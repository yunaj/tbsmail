package com.hs.mail.webmail.controller;

import java.util.List;

import javax.mail.Folder;
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
import com.hs.mail.webmail.model.FancytreeNode;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaFolderList;
import com.hs.mail.webmail.model.WmaMessageInfoList;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.search.Query;
import com.hs.mail.webmail.util.Pager;
import com.hs.mail.webmail.util.RequestUtils;

@Controller
public class FolderController {
	
	@RequestMapping(value = "/folder/messages", method = RequestMethod.GET)
	public String folder(
			@RequestParam(value = "page", defaultValue = "0") int page,
			Model model, HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		WmaPreferences prefs = session.getPreferences();
		String path = RequestUtils.getRequiredParameter(request, "path");
		String term = RequestUtils.getParameter(request, "term");
		int pageSize = RequestUtils.getParameterInt(request, "pageSize", prefs.getPageSize());
		WmaStore store = session.getWmaStore();
		Query query = session.getQuery(request);
		Pager pager = new Pager(page, pageSize, query.isAscending());
		Folder folder = store.getFolder(path);
		WmaMessageInfoList msglist = WmaMessageInfoList
				.createWmaMessageInfoList(folder, query.getSearchTerm(),
						query.getSortTerm(), pager);
		if ("INBOX".equals(path)) {
			model.addAttribute("unread", folder.getUnreadMessageCount());
		}
		model.addAttribute("path", path);
		model.addAttribute("term", term);
		model.addAttribute("messages", msglist.getMessageInfos());
		model.addAttribute("pager", pager);
		model.addAttribute("prefs", prefs);
		return "messagelist";
	}
	
	@RequestMapping(value = "/folder/tree", method = RequestMethod.GET)
	@ResponseBody
	public FancytreeNode[] foldertree(HttpSession httpsession,
			HttpServletRequest request) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		boolean recursive = RequestUtils.getParameterBool(request, "recursive", true);
		WmaStore store = session.getWmaStore();
		return WmaFolderList.createSubfolderList(store.getFolder(path),
				recursive, store.getFolderSeparator());
	}
	
	@RequestMapping(value = "/folder/create")
	@ResponseBody
	public FancytreeNode create(HttpSession httpsession, HttpServletRequest request) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		String name = RequestUtils.getRequiredParameter(request, "name");
		WmaStore store = session.getWmaStore();
		String fullname = new StringBuilder()
				.append((path == null) ? store.getPersonalArchive().getPath() : path)
				.append(store.getFolderSeparator())
				.append(name)
				.toString();
		Folder newfolder = store.createFolder(fullname, WmaFolder.TYPE_MIXED);
		return new FancytreeNode(newfolder);
	}
	
	@RequestMapping(value = "/folder/delete")
	@ResponseBody
	public boolean delete(HttpSession httpsession, HttpServletRequest request) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		// TODO - delete sub folders and move messages to thrash
		store.deleteFolder(path);
		return true;
	}
	
	@RequestMapping(value = "/folder/empty")
	@ResponseBody
	public boolean empty(HttpSession httpsession, HttpServletRequest request) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		WmaStore store = session.getWmaStore();
		store.emptyFolder(path);
		return true;
	}
	
	@RequestMapping(value = "/folder/rename")
	@ResponseBody
	public FancytreeNode rename(HttpSession httpsession, HttpServletRequest request) throws Exception {
		WmaSession session = new WmaSession(httpsession);
		String path = RequestUtils.getRequiredParameter(request, "path");
		String destfolder = RequestUtils.getRequiredParameter(request, "destfolder");
		WmaStore store = session.getWmaStore();
		Folder newfolder = store.renameFolder(path, destfolder);
		return new FancytreeNode(newfolder);
	}
	
	@RequestMapping(value = "/folder/manage", method = RequestMethod.GET)
	public String manage(Model model, HttpSession httpsession,
			HttpServletRequest request) throws WmaException {
		WmaSession session = new WmaSession(httpsession);
		WmaStore store = session.getWmaStore();
		WmaFolder personal = store.getPersonalArchive();
		List<WmaFolder> folders = WmaFolderList.createSubfolderList(personal
				.getFolder());
		model.addAttribute("store", store);
		model.addAttribute("folders", folders);
		return "mngfolders";
	}

}
