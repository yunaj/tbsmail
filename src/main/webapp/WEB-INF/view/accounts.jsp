<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<c:if test="${not empty accounts}">
	<table class="table table-hover table-condensed">
		<tbody>
			<c:forEach var="account" items="${accounts}">
				<tr>
					<td>
						<i class="fa fa-envelope"></i><input type="hidden" name="uid" value="${account.UID}"/>
					</td>
					<td>
						<a id="update-account">
							<strong><c:out value="${account.serverName}"/></strong>
							<c:out value="${account.userName}"/>
						</a>
					</td>
					<td>
						<a id="delete-account" class="btn btn-default btn-xs"><fmt:message key="menu.delete"/></a>
						<a id="fetch-account" class="btn btn-default btn-xs"><fmt:message key="menu.fetch"/></a>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</c:if>
<script>
$(function() {
});
</script>