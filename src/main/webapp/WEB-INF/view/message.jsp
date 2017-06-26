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

	<div class="mail-header" style="float:right;">
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
		<a id="prev" class="btn btn-sm"><i class="fa fa-arrow-left"></i></a>
		<a id="next" class="btn btn-sm"><i class="fa fa-arrow-right"></i></a> 
		<a id="print" class="btn btn-sm"><i class="fa fa-print"></i></a> 
	</div>
	<div class="mail-content">
			<table class="board_width_borderNone">
			<caption class="blind"></caption>
			<colgroup>
				<col width="100px"/>
				<col width="*"/>
				<col width="100px"/>
				<col width="250px"/>
			</colgroup>
			<tbody>
				<tr>
					<th><label for="label_1"><fmt:message key="label.subject"/></label></th>
					<td colspan="3"><div class="ui_input_text">
						<input type="text" name="" value='<c:out value="${message.subject}"/>' readonly /></div>
					</td>
				</tr>
				<tr>
					<th><label for="label_1"><fmt:message key="label.from"/></label></th>
					<td><div class="ui_input_text"><input type="text" name="" value='<wma:address value="${message.from}"/>' readonly/></div></td>
					<th><label for="label_1"><fmt:message key="label.receiveddate"/></label></th>
					<td><div class="ui_input_text"><input type="text" name="" value='<fmt:formatDate value="${message.receivedDate}" pattern="yyyy.MM.dd HH:mm"/>' readonly/></div></td>
				</tr>
				<tr>
					<th><label for="label_1"><fmt:message key="label.to"/></label></th>
					<td colspan="3"><div class="ui_input_text"><input type="text" name="" value='<wma:address value="${message.to}"/>' readonly/></div></td>
				</tr>
				<c:if test="${not empty message.CC}">
				<tr>
					<th><label for="label_1"><fmt:message key="label.cc"/></label></th>
					<td colspan="3"><div class="ui_input_text"><input type="text" name="" value='<wma:address value="${message.CC}"/>' readonly/></div></td>
				</tr>
				</c:if>
				<tr>
					<th><label for="label_1"><fmt:message key="label.content"/></label></th>
					<td colspan="3" class="ui_textarea_rapper_04">
						<textarea><c:out value="${message.body}" escapeXml="false"/></textarea>
					</td>  
				</tr>
			</tbody>
			
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
	</table>
	</div>
</form>
