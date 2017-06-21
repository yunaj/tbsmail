<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<form>
	<input type="hidden" name="path" value="${path}">
	<input type="hidden" name="uids" value="${param.uid}">
	<table class="table text-left">
		<thead>
			<tr>
				<th><fmt:message key="label.type"/></th>
				<th><fmt:message key="label.address"/></th>
				<th><fmt:message key="label.status"/></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="recipient" items="${recipients}">
			<tr>
				<td>
				<c:choose>
				<c:when test="${recipient.type == 'To'}"><span class="label label-primary">To</span></c:when>
				<c:when test="${recipient.type == 'Cc'}"><span class="label label-success">Cc</span></c:when>
				<c:otherwise><span class="label label-info">Bcc</span></c:otherwise>
				</c:choose>
				</td>
				<td><c:out value="${recipient}"/></td>
				<td><a href="#${recipient.address}">[<fmt:message key="menu.revoke"/>]<a></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</form>