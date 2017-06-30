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
		<input type="button" id="reply" value='<fmt:message key="menu.reply"/>' class="but_gray"/>
		<input type="button" id="replyall" value='<fmt:message key="menu.replyall"/>' class="but_gray"/>
		<input type="button" id="forward" value='<fmt:message key="menu.forward"/>' class="but_gray"/>
		<input type="button" id="delete-msg" value='<fmt:message key="menu.delete"/>' class="but_gray"/>
		<input type="button" id="raw-msg" value='<fmt:message key="menu.rawmsg"/>' class="but_gray"/>
		<span class="btn_pn_rapper">
	        <a id="prev" class="move_page_button"><img src="images/egovframework/com/uss/cmm/but_prev.png" alt="prev"></a>
	        <a id="next" class="move_page_button"><img src="images/egovframework/com/uss/cmm/but_next.png" alt="next"></a>
	        <!-- <a id="print" class="btn btn-sm"><i class="fa fa-print"></i></a> --> 
        </span>
	</div>
	<div class="mail-content">
			<table class="board_width_borderNone">
			<caption class="blind"></caption>
			<colgroup>
				<col width="100px"/>
				<col width="*"/>
				<col width="100px"/>
				<col width="200px"/>
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
				<c:if test="${not empty message.attachParts}">
				<tr>
					<th><label for="label_1"><fmt:message key="label.attach"/></label></th>
					<td colspan="3" style="padding-left:10px;">
						<table summary="" class="board_type_height" id="fileTable">
	                        <caption class="blind"></caption>
	                        <colgroup>
	                            <col width="40px"/>
	                            <col width="*"/>
	                            <col width="30%"/>
	                        </colgroup>
	                        <thead>
	                            <tr>
	                                <th scope="col"><span>No</span></th>
	                                <th scope="col"><span>File Name</span></th>
	                                <th scope="col"><span>File Size</span></th>	
	                            </tr>
	                        </thead>					
	                        <tbody>
	                        	<c:forEach var="attach" items="${message.attachParts}" varStatus="status">
	                            <tr>
	                                <td>${attach.partNumber}</td>
	                                <td><a id="att-${attach.partNumber}"><c:out value="${attach.name}"/></a></td>
	                                <td><wma:size value="${attach.size}"/></td>
	                            </tr>
	                            </c:forEach>
	                        </tbody>
	                    </table>
					</td>  
				</tr>
				</c:if>
				<tr>
					<th><label for="label_1"><fmt:message key="label.content"/></label></th>
					<td colspan="3" class="ui_textarea_rapper_04">
						<div><c:out value="${message.body}" escapeXml="false"/></div>
					</td>  
				</tr>
			</tbody>
		</table>
	</div>
</form>
