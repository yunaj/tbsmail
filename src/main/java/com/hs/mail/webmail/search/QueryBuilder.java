package com.hs.mail.webmail.search;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.hs.mail.webmail.util.RequestUtils;
import com.sun.mail.imap.SortTerm;

public class QueryBuilder {
	
	public static Query build(HttpServletRequest request)
			throws ServletException {
		SearchTerm term = createSearchTerm(request);
		SortTerm[] sortterm = createSortTerm(request);
		boolean asc = RequestUtils.getParameterBool(request, "asc", false);
		return new Query(term, sortterm, asc);
	}

	private static SearchTerm createSearchTerm(HttpServletRequest request) {
		String criteria = request.getParameter("criteria");
		if (criteria == null || "all".equals(criteria)) {
			return null;
		}
		if ("recent".equals(criteria)) {
			return new FlagTerm(new Flags(Flags.Flag.RECENT), true);
		} else if ("unread".equals(criteria)) {
			return new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		} else if ("flagged".equals(criteria)) {
			return new FlagTerm(new Flags(Flags.Flag.FLAGGED), true);
		} else {
			String term = RequestUtils.getParameter(request, "term");
			if (!StringUtils.isEmpty(term)) {
				if ("subject".equals(criteria)) {
					return new SubjectTerm(term);
				} else if ("from".equals(criteria)) {
					return new FromStringTerm(term);
				} else if ("to".equals(criteria)) {
					return new RecipientStringTerm(Message.RecipientType.TO,
							term);
				}
			}
		}
		return null;
	}
	
	private static SortTerm[] createSortTerm(HttpServletRequest request) {
		String order = request.getParameter("order");
		if ("arrival".equals(order)) {
			return new SortTerm[] { SortTerm.ARRIVAL };
		} else if ("date".equals(order)) {
			return new SortTerm[] { SortTerm.DATE };
		} else if ("from".equals(order)) {
			return new SortTerm[] { SortTerm.FROM };
		} else if ("size".equals(order)) {
			return new SortTerm[] { SortTerm.SIZE };
		} else if ("subject".equals(order)) {
			return new SortTerm[] { SortTerm.SUBJECT };
		} else {
			return null;
		}
	}
	
}
