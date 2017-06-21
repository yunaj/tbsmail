<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<div class="mail-header">
</div>
<table id="fldr-table" class="table table-condensed">
	<thead>
		<tr>
			<th><fmt:message key="menu.folder"/></th>
			<th class="text-right"><fmt:message key="label.count"/></th>
			<th class="text-right"><fmt:message key="label.size"/></th>
			<th class="text-right"></th>
		</tr>
	</thead>
	<tbody class="text-right">
        <tr>
        	<td class="text-left">
        		<input type="hidden" name="path" value="${store.inboxInfo.path}"/>
            	<i class="fa fa-inbox"></i> <fmt:message key="prefs.inbox" />
        	</td>
        	<td><fmt:formatNumber value="${store.inboxInfo.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.sentMailArchive.path}"/>
            	<i class="fa fa-paper-plane-o"></i> ${store.sentMailArchive.name}
        	</td>
        	<td><fmt:formatNumber value="${store.sentMailArchive.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.toSendArchive.path}"/>
            	<i class="fa fa-clock-o"></i> ${store.toSendArchive.name}
        	</td>
        	<td><fmt:formatNumber value="${store.toSendArchive.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.trashInfo.path}"/>
            	<i class="fa fa-trash-o"></i> ${store.trashInfo.name}
	        </td>
        	<td><fmt:formatNumber value="${store.trashInfo.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
	    </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.draftInfo.path}"/>
            	<i class="fa fa-pencil-square-o"></i> ${store.draftInfo.name}
        	</td>
        	<td><fmt:formatNumber value="${store.draftInfo.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
        </tr>
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${store.personalArchive.path}"/>
            	<i class="fa fa-archive"></i> ${store.personalArchive.name}
        	</td>
        	<td><fmt:formatNumber value="${store.personalArchive.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
    	</tr>
<c:forEach var="folder" items="${folders}">
        <tr>
        	<td class="text-left">
          		<input type="hidden" name="path" value="${folder.path}"/>
            	<i class="fa fa-folder"></i> ${folder.name}
        	</td>
        	<td><fmt:formatNumber value="${folder.messageCount}"/></td>
        	<td><i class="fa fa-spinner fa-spin"></i></td>
        	<td><a href="#empty" class="btn btn-default btn-xs"><fmt:message key="menu.folder.empty"/></a></td>
        </tr>
</c:forEach>
	</tbody>
</table>
<script>
$(function() {
	console.log('mngfolders');

	$('#fldr-table').find('input[name=path]').each(function() {
		var tr = $(this).closest('tr');
		$.getJSON('quota', {path:$(this).val()}, function(quotas) {
			if (quotas && quotas.length > 0) {
				if (quotas[0].resources && quotas[0].resources.length > 0) {
					var resource = quotas[0].resources[0];
					tr.find('td').eq(2).text(formatBytes(resource.usage));
				}
			}
		});
	});

	$('#fldr-table').on('click', 'a[href=#empty]', function() {
		var tr = $(this).closest('tr'),
		    path = tr.find('input[name=path]').val();
		$.post('folder/empty', {path:path}, function() {
			tr.find('td').eq(1).text('0');
			tr.find('td').eq(2).text(formatBytes(0));
			if (path == 'INBOX') {
				$('#side-menu').find('#inbox-unread').text('0');
			}
            // refresh quota
            showQuota();
		});
	});
});
</script>