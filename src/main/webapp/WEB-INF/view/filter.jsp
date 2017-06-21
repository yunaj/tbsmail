<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form id="filter-form" class="form-horizontal" method="post">
	<div class="form-group">
		<label class="col-sm-2 control-label"></label>
		<div class="col-sm-10">
			<label class="radio-inline">
				<input type="radio" name="match" value="anyof" checked> <fmt:message key="prefs.filter.anyof"/>
			</label>
			<label class="radio-inline">
				<input type="radio" name="match" value="allof"> <fmt:message key="prefs.filter.allof"/>
			</label>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label"><fmt:message key="prefs.filter.sender"/></label>
		<div class="col-sm-10">
			<input type="text" name="sender" class="form-control" placeholder="From" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label"><fmt:message key="prefs.filter.subject"/></label>
		<div class="col-sm-10">
			<input type="text" name="subject" class="form-control" placeholder="Subject" />
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label"><fmt:message key="prefs.filter.action"/></label>
		<div class="col-sm-10">
			<div class="radio">
				<label class="col-sm-4 control-label">
					<input type="radio" name="action" value="discard"> <fmt:message key="prefs.filter.discard"/>
				</label>
			</div>
			<div class="radio">
				<label class="col-sm-4 control-label">
					<input type="radio" name="action" value="fileinto" checked> <fmt:message key="prefs.filter.fileinto"/>
				</label>
				<select name="actionparam" id="fileinto" class="form-control">
				</select>
			</div>
		</div>
	</div>
</form>
<script>
$(function() {
	console.log('filter');

	var tree = $('#tree').fancytree('getTree'),
		fileinto = $('#fileinto');
	fileinto.append($('<option>', { value: $('#personalArchive > a').data('target'), text: $('#personalArchive > a').text() }));
	$.each(tree.getRootNode().getChildren(), function(index, node) {
		fileinto.append($('<option>', { value: node.key, text: node.title }));
	});
	fileinto.append($('<option>', { value: $('#trashInfo > a').data('target'), text: $('#trashInfo > a').text() }));
});
</script>