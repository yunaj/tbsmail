<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/wma.tld" prefix="wma" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title><c:out value="${message.subject}"/></title>
<link rel="stylesheet" href="css/print.css"/>
<script>
window.onload = function() { window.print(); }
</script>
</head>
<body>
<h2><c:out value="${message.subject}"/></h2>
<div><strong><fmt:message key="label.from"/>:</strong> <wma:address value="${message.from}"/></div>
<div><strong><fmt:message key="label.to"/>:</strong> <wma:address value="${message.to}"/></div>
<div><fmt:formatDate value="${message.receivedDate}" pattern="yyyy.MM.dd HH:mm"/></div>
<c:if test="${not empty message.CC}">
<div><strong><fmt:message key="label.cc"/>:</strong> <wma:address value="${message.CC}"/></div>
</c:if>
<c:out value="${message.body}" escapeXml="false"/>
<c:if test="${not empty message.attachParts}">
<h3><fmt:message key="label.attach"/></h3>
<ol>
<c:forEach var="attach" items="${message.attachParts}" varStatus="status">
<li><c:out value="${attach.name}"/> <wma:size value="${attach.size}"/></li>
</c:forEach>
</ol>
</c:if>
</body>
</html>