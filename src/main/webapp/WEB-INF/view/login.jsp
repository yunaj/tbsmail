<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>Hedwig Web Mail</title>
<link rel="stylesheet" href="css/bootstrap.min.css"/>
<link rel="stylesheet" href="css/default.css"/>
<style>
body.blank {
	background-color: #f1f3f6;
}
.middle-box .panel .panel-body {
	padding: 20px;
}
.middle-box label {
	display: inline-block;
	max-width: 100%;
	margin-bottom: 5px;
	font-weight: bold;
}
</style>
<script src="js/jquery/jquery.js"></script>
<script src="js/bootstrap.min.js"></script>
<script>
$(function() {
});
</script>
</head>
<body class="blank">
	<div class="middle-box">
		<div class="text-center mb-20">
			<h3>WELCOME TO HEDWIG</h3>
		</div>
		<div class="panel panel-info">
			<div class="panel-body">
<c:if test="${not empty error}">
				<div class="alert alert-danger" role="alert">
						<fmt:message key="${error.message}"/>
				</div>
</c:if>
				<form action="login" method="post" enctype="application/x-www-form-urlencoded">
					<input type="hidden" name="facility" value="PropertiesLogin"/>
					<div class="form-group">
						<label class="control-label" for="username">Username</label>
						<input type="text" name="username" id="username" class="form-control" placeholder="Username" required/>
					</div>
					<div class="form-group">
						<label class="control-label" for="password">Password</label>
						<input type="password" name="password" id="password" class="form-control" placeholder="Password" required/>
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" id="remember"> Remember login
						</label>
						<p class="help-block small">(if this is a private computer)</p>
					</div>
					<div class="form-group">
						<select name="language" class="form-control">
							<option value="en">English</option>
		                	<option value="ko">Korean</option>
						</select>
					</div>
					<button type="submit" class="btn btn-primary btn-block">Login</button>
				</form>
			</div>
		</div>
	</div>
</body>
</html>
