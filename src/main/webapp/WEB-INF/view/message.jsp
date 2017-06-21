<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/wma.tld" prefix="wma" %>

<form id="msg-form">
	<input type="hidden" name="path" value="${message.folderFullName}"/>
	<input type="hidden" name="uid" value="${message.UID}"/>
	<input type="hidden" name="number" value="${message.number}"/>
	<input type="hidden" name="part"/>

	<div class="mail-header">
		<div class="btn-group pull-right">
			<a id="prev" class="btn btn-sm"><i class="fa fa-arrow-left"></i></a>
			<a id="next" class="btn btn-sm"><i class="fa fa-arrow-right"></i></a> 
			<a id="print" class="btn btn-sm"><i class="fa fa-print"></i></a> 
		</div>
		<a id="reply" class="btn btn-default btn-sm">
			<i class="fa fa-reply"></i> <fmt:message key="menu.reply"/>
		</a>
		<a id="replyall" class="btn btn-default btn-sm">
			<i class="fa fa-reply-all"></i> <fmt:message key="menu.replyall"/>
		</a>
		<a id="forward" class="btn btn-default btn-sm">
			<i class="fa fa-share"></i> <fmt:message key="menu.forward"/>
		</a>
		<a id="delete-msg" class="btn btn-default btn-sm">
			<i class="fa fa-trash-o"></i> <fmt:message key="menu.delete"/>
		</a>
		<a id="raw-msg" class="btn btn-default btn-sm">
			<i class="fa fa-file-code-o"></i> <fmt:message key="menu.rawmsg"/>
		</a>
	</div>
	<div class="mail-content">
		<div class="mail-body">
			<h4 class="mail-title"><c:out value="${message.subject}"/></h4>
			<h5>
				<span class="pull-right">
					<i class="fa fa-clock-o"></i> <fmt:formatDate value="${message.receivedDate}" pattern="yyyy.MM.dd HH:mm"/>
				</span>
				<span><wma:address value="${message.from}"/></span>
			</h5>
			<address class="showcc">
				<div><strong><fmt:message key="label.to"/>: </strong><wma:address value="${message.to}"/></div>
<c:if test="${not empty message.CC}">
				<div class="cc"><strong><fmt:message key="label.cc"/>: </strong><wma:address value="${message.CC}"/></div>
</c:if>
			</address>
		</div>
		<div class="mail-body">
			<c:out value="${message.body}" escapeXml="false"/>
			<div class="clearfix"></div>
		</div>
<c:if test="${not empty message.attachParts}">
		<div class="mail-attachment">
			<p>
				<span><i class="fa fa-paperclip"></i> <fmt:message key="label.attach"/> (${fn:length(message.attachParts)})</span>
			</p>
			<ul class="attachment list-inline">
	<c:forEach var="attach" items="${message.attachParts}" varStatus="status">
				<li>
					<a id="att-${attach.partNumber}"><c:out value="${attach.name}"/></a>
					<span>(<wma:size value="${attach.size}"/>)</span>
				</li>
	</c:forEach>
			</ul>
		</div>
</c:if>
	</div>
</form>
