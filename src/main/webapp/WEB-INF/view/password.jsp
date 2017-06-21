<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<form id="authcheck" action="authcheck" method="post">
	<div class="form-group">
		<input type="password" name="password" class="form-control" placeholder="Password" required/>
		<span class="error control-label hidden"><fmt:message key="wma.session.authentication"/></span>
	</div>
</form>