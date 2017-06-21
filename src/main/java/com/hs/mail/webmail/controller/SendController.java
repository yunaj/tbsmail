package com.hs.mail.webmail.controller;

import java.util.Date;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hs.mail.webmail.WmaSession;
import com.hs.mail.webmail.exception.WmaException;
import com.hs.mail.webmail.model.WmaFolder;
import com.hs.mail.webmail.model.WmaPreferences;
import com.hs.mail.webmail.model.WmaStore;
import com.hs.mail.webmail.model.impl.WmaComposeMessage;
import com.hs.mail.webmail.model.impl.WmaDisplayMessage;
import com.hs.mail.webmail.model.impl.WmaMultipartFileAttach;
import com.hs.mail.webmail.model.impl.WmaSendReservation;
import com.hs.mail.webmail.util.RequestUtils;
import com.hs.mail.webmail.util.WmaUtils;
import com.sun.mail.imap.IMAPFolder;

@Controller
public class SendController {
	
	private static Logger logger = LoggerFactory.getLogger(SendController.class);
	
	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public String send(HttpSession httpsession, HttpServletRequest request)
			throws Exception {
		WmaSession session = new WmaSession(httpsession);
		boolean savedraft = RequestUtils.getParameterBool(request, "savedraft", false);
		boolean archivesent = RequestUtils.getParameterBool(request, "archivesent", false);
		boolean reserve = RequestUtils.getParameterBool(request, "reserve", false);
		WmaComposeMessage msg = createWmaComposeMessage(session, request);
		if (savedraft) {
			doSaveDraft(session, msg);
		} else if (reserve) {
			Date date = getParameterDate(request);
			doSendLater(session, msg, date, archivesent);
		} else {
			doSendMessage(session, msg, archivesent);
		}
		return (savedraft) ? "saved" : "sent";
	}

	private WmaComposeMessage createWmaComposeMessage(WmaSession session,
			HttpServletRequest request) throws Exception {
		WmaPreferences prefs = session.getPreferences();
		String encoding = RequestUtils.getParameter(request, "encoding", "UTF-8");
		String contentType = RequestUtils.getParameter(request, "content-type", "text/html");
		boolean reply = RequestUtils.getParameterBool(request, "reply", false);
		boolean forward = RequestUtils.getParameterBool(request, "forward", false);
		boolean draft = RequestUtils.getParameterBool(request, "draft", false);
		boolean secure = RequestUtils.getParameterBool(request, "secure", false);
		int priority = RequestUtils.getParameterBool(request, "urgent", false) ? 1 : 3;

		String path = RequestUtils.getParameter(request, "path");
		long uid = RequestUtils.getParameterLong(request, "uid", -1L);

		//retrieve all necessary parameters into local strings recipients
		String to = RequestUtils.getParameter(request, "to");
		String cc = RequestUtils.getParameter(request, "cc");
		String bcc = RequestUtils.getParameter(request, "bcc");
		String subject = RequestUtils.getParameter(request, "subject");
		String body = RequestUtils.getParameter(request, "body");
		
		WmaComposeMessage msg = WmaComposeMessage.createMessage(session);
		msg.setEncoding(encoding);
		msg.setDraft(draft);
		if (msg.isDraft()) {
			msg.setUID(uid);
		}
		msg.setSubject(subject);
		msg.setPriority(priority);
		msg.setReply(reply);
		msg.setForward(forward);
		msg.setSecure(secure);
		// body
		msg.setContentType(contentType + "; charset=\"" + encoding + "\"");
		msg.setBody(body);
		// set sender identity
		msg.setFrom(session.getUserIdentity(),
				StringUtils.isNotBlank(prefs.getUsername()) ? prefs.getUsername() : null);
		// set all recipients
		try {
			msg.setTo(to);
			msg.setCC(cc);
			msg.setBCC(bcc);
		} catch (MessagingException e) {
		}

		// Handle original message
		if (StringUtils.isNotEmpty(path) && uid != -1) {
			int[] partnums = RequestUtils.getParameterInts(request, "parts");
			handleOriginalMessage(session, path, uid, msg, partnums);
		}
		
		// Handle attachments
		MultipartHttpServletRequest multi = (MultipartHttpServletRequest) request;
		List<MultipartFile> files = multi.getFiles("attachment[]");
		for (MultipartFile mf : files) {
			if (mf != null) {
				msg.addAttachment(new WmaMultipartFileAttach(mf, encoding));
			}
		}
		return msg;
	}
	
	private void handleOriginalMessage(WmaSession session, String path,
			long uid, WmaComposeMessage msg, int[] partnums)
			throws ServletException {
		try {
			WmaStore store = session.getWmaStore();
			IMAPFolder folder = (IMAPFolder) store.getFolder(path);
			folder.open(Folder.READ_WRITE);
			Message message = folder.getMessageByUID(uid);
			// Handle original message's flags
			if (msg.isReply()) {
				message.setFlag(Flags.Flag.ANSWERED, true);
			}
			if (partnums != null) {
				// Handle original message's attachments
				WmaDisplayMessage actualmsg = WmaDisplayMessage.createWmaDisplayMessage(uid, message);
				msg.addAttachments(actualmsg, partnums);
			}
			folder.close(false);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void doSendMessage(WmaSession session, WmaComposeMessage message,
			boolean autoarchivesent) throws WmaException {
		if (autoarchivesent) {
			message.send(session);
			// Archive message if necessary
			WmaStore store = session.getWmaStore();
			store.archiveMail(store.getSentMailArchive().getFolder(),
					message.getMessage());
		} else {
			message.send(session);
		}
	}
	
	private void doSaveDraft(WmaSession session, WmaComposeMessage message)
			throws WmaException {
		try {
			// set body text
			message.setBodyText();
			// set draft flag
			message.setFlag(Flags.Flag.DRAFT, true);
			// Archive message to draft
			WmaStore store = session.getWmaStore();
			store.archiveMail(store.getDraftInfo().getFolder(),
					message.getMessage(), message.getUID());
		} catch (MessagingException mex) {
			throw new WmaException("wma.composemessage.draft.failed")
					.setException(mex);
		}
	}

	private void doSendLater(WmaSession session, WmaComposeMessage message,
			Date date, boolean autoarchivesent) throws WmaException {
		try {
			// set body text
			message.setBodyText();
			// ensure that to send date is set
			message.setSentDate(date);
			WmaStore store = session.getWmaStore();
			WmaFolder tosend = store.getToSendArchive();
			store.archiveMail(tosend.getFolder(), message.getMessage());
			WmaSendReservation.serialize(session, tosend.getPath(),
					autoarchivesent ? store.getSentMailArchive().getPath()
							: null, message, date);
		} catch (MessagingException mex) {
			throw new WmaException("wma.composemessage.send.failed")
					.setException(mex);
		}
	}
	
	private static Date getParameterDate(HttpServletRequest request)
			throws WmaException {
		String d = RequestUtils.getParameter(request, "date");
		String h = RequestUtils.getParameter(request, "hour");
		String m = RequestUtils.getParameter(request, "minute");
		return WmaUtils.parseDate(new StringBuilder(d).append(' ').append(h)
				.append(':').append(m).toString(), "MM/dd/yyyy HH:mm");
	}

}
