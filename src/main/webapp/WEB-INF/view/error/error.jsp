<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="container-fluid error-page">
	<h1><i class="fa fa-times-circle"></i> 500</h1><strong>Internal Server Error</strong>
<c:if test="${not empty error}">
	<p><c:out value="${error.message}"/></p>
</c:if>
</div>