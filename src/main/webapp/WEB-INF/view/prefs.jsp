<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div id="settings-tab" class="container-fluid">
	<!-- Nav tabs -->
	<ul class="nav nav-tabs">
		<li class="active">
			<a href="#general" data-toggle="tab"><fmt:message key="prefs.general"/></a>
		</li>
		<li>
			<a href="#filter" data-toggle="tab"><fmt:message key="prefs.filter"/></a>
		</li>
		<li>
			<a href="#account" data-toggle="tab"><fmt:message key="fetch.account"/></a>
		</li>
	</ul>
	<!-- Tab panes -->
	<div class="tab-content">
	    <div class="tab-pane active" id="general">
			<form:form id="prefs-general" class="form-horizontal" modelAttribute="prefs">
				<div class="alert alert-success fade" style="display:none;"></div>
				<form:hidden path="userIdentity"/>
				<div class="form-group">
					<label class="col-sm-2 control-label"><fmt:message key="prefs.inboxtype"/></label>
					<div class="col-sm-10">
						<label class="radio-inline">
							<form:radiobutton path="inboxType" value="all"/> <fmt:message key="prefs.inbox" />
						</label>
						<label class="radio-inline">
							<form:radiobutton path="inboxType" value="unread"/> <fmt:message key="menu.filter.unread"/>
						</label>
						<label class="radio-inline">
							<form:radiobutton path="inboxType" value="recent"/> <fmt:message key="menu.filter.recent"/>
						</label>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label"><fmt:message key="prefs.pagesize"/></label>
					<div class="col-sm-10">
						<label class="radio-inline"><form:radiobutton path="pageSize" value="10"/> 10</label>
						<label class="radio-inline"><form:radiobutton path="pageSize" value="15"/> 15</label>
						<label class="radio-inline"><form:radiobutton path="pageSize" value="20"/> 20</label>
						<label class="radio-inline"><form:radiobutton path="pageSize" value="25"/> 25</label>
						<label class="radio-inline"><form:radiobutton path="pageSize" value="50"/> 50</label>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label"><fmt:message key="prefs.sentmail"/></label>
					<div class="col-sm-10">
						<div class="checkbox">
							<label>
							    <form:checkbox path="autoArchiveSent" value="true"/>
							    <fmt:message key="prefs.autoarchive"/>
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label"><fmt:message key="prefs.sendas"/></label>
					<div class="col-sm-10">
						<div class="row">
						  <div class="col-xs-5">
						    <form:input path="username" class="form-control"/>
						  </div>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label"><fmt:message key="menu.reply"/></label>
					<div class="col-sm-10">
						<div class="checkbox">
							<label>
								<form:checkbox path="autoQuote" value="true"/> <fmt:message key="prefs.autoquote"/>
							</label>
						</div>
						<div class="checkbox">
							<label>
								<form:checkbox path="autoAttach" value="true"/> <fmt:message key="prefs.autoattach"/>
							</label>
						</div>					
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label">
						<fmt:message key="prefs.signature"/>
					</label>
					<div class="col-sm-10">
						<div class="radio">
							<label>
								<form:radiobutton path="autoSign" value="false"/> <fmt:message key="prefs.nosignature"/>
							</label>
						</div>
						<div class="radio">
							<label>
								<form:radiobutton path="autoSign" value="true"/>
								<form:textarea path="signature" class="form-control"/>
							</label>
						</div>
					</div>
				</div>
				<div class="form-group">
					<label class="col-sm-2 control-label"></label>
					<div class="col-sm-10">
						<button class="btn btn-primary"><fmt:message key="menu.apply"/></button>
					</div>
				</div>
			</form:form>
	    </div><!-- /.tab-pane#general -->
	    <div class="tab-pane" id="filter">
	    	<div class="mb-20">
	    		<a id="save-filter" class="btn btn-default btn-sm disabled hidden"><fmt:message key="menu.save"/></a>
	    		<a id="create-filter" class="btn btn-default btn-sm"><fmt:message key="menu.add"/></a>
	    	</div>
	    	<form method="post" action="prefs/filters">
	    		<input type="hidden" id="filter-dirty" value="na"/>
		    	<div class="alert alert-success fade" style="display:none;"></div>
		    	<table class="table table-hover table-condensed">
		    		<tbody>
		    			<tr id="filter-command" class="hidden">
		    				<td><input type="hidden" name="filters"></td>
		    				<td></td>
		    				<td></td>
		    				<td>
		    					<a id="update-filter" class="btn btn-default btn-xs"><fmt:message key="menu.edit"/></a>
		    					<a id="delete-filter" class="btn btn-default btn-xs"><fmt:message key="menu.delete"/></a>
		    				</td>
		    			</tr>
		    		</tbody>
		    	</table>
	    	</form>
	    </div><!-- /.tab-pane#filter -->
	    <div class="tab-pane" id="account">
	    	<div class="mb-20">
	    		<a id="create-account" class="btn btn-default btn-sm hidden"><fmt:message key="menu.add"/></a>
	    	</div>
	    	<div id="accounts">
	    	</div>
	    </div><!-- /.tab-pane#accounts -->
	</div>
</div>
<script>
$(function() {
	console.log('prefs');

	// GENERAL

	$('#prefs-general').on('submit', function(e) {
		e.preventDefault(); e.stopPropagation();
		var $form = $(this);
		$.post('prefs', $form.serializeArray())
			.done(function(data) {
				$form.find('.alert').html(data).fadeTo(1000, 500).slideUp(500);
			});
		return false;
	}).find('textarea[name=signature]').summernote({ height: 110 });

	// FILTER

	$('#settings-tab').on('shown.bs.tab', 'a[href=#filter]', function() {
		if ($('#save-filter').is(':hidden')) { // lazy loading - this is the 1st time filter list is displayed
			$('#save-filter').removeClass('hidden');
			$.getJSON('prefs/filters', function(data) {
				if (data) {
					$.each(data, function(i, v) {
						var row = $('#filter-command').clone().removeClass('hidden');
						if (setFilter(row, v)) {
							$('#filter-command').parent().append(row);
						}
					});
				}
			});
		}
	});

	$('#filter').on('click', '#save-filter', function(e) {
		var $form = $(e.delegateTarget).find('form');
		$.post($form.attr('action'), $form.serializeArray())
			.done(function(data) {
				$('#save-filter').addClass('disabled');
				$form.find('.alert').html(data).fadeTo(1000, 500).slideUp(500);
			});
	}).on('click', '#create-filter', function() {
		eModal.ajax({
			url: 'prefs/filters/create',
			title: 'Add Filter',
			size: 'lg',
			buttons: [
				{ text: 'Close', style: 'default', close: true },
				{ text: 'OK', style: 'primary', close: true, click: function() {
						var row = $('#filter-command').clone().removeClass('hidden'),
							data = serializeObject($('#filter-form'));
						if (setFilter(row, data)) {
							$('#filter-command').parent().append(row);
							$('#save-filter').removeClass('disabled');
						}
					}
				}
			]
		});
	}).on('click', '#update-filter', function() {
		var row = $(this).closest('tr'),
			data = JSON.parse(row.find('input[name=filters]').val());
		eModal.ajax({
			url: 'prefs/filters/create',
			title: 'Update Filter',
			size: 'lg',
			buttons: [
				{ text: 'Close', style: 'default', close: true },
				{ text: 'OK', style: 'primary', close: true, click: function() { 
						var data = serializeObject($('#filter-form'));
						setFilter(row, data);
						$('#save-filter').removeClass('disabled');
					}
				}
			]
		}).then(function() {
			$('#filter-form').deserialize(data);
		});
	}).on('click', '#delete-filter', function() {
		$(this).closest('tr').remove();
		$('#save-filter').removeClass('disabled');
	});

	function setFilter(row, data) {
		var cond = stringifyIf(data);
		if (!cond) return false;
//		row.find('td:eq(1)').html(cond);
		row.find('td:eq(1)').append( $('<a></a>').attr('id', 'update-filter').html(cond) );
		row.find('td:eq(2)').text(stringifyThen(data));
		row.find('input[name=filters]').val(JSON.stringify(data));
		return true;
	}

	function stringifyIf(data) {
		var strings = [], msgs = {
				'anyof'   : '<fmt:message key="prefs.filter.message.anyof"/>',
				'allof'   : '<fmt:message key="prefs.filter.message.allof"/>',
				'sender'  : '<fmt:message key="prefs.filter.message.sender"/>',
				'subject' : '<fmt:message key="prefs.filter.message.subject"/>'
			};
		if (data.sender) 
			strings.push(msgs.sender.replace('{0}', escapeHtml(data.sender)));
		if (data.subject)
			strings.push(msgs.subject.replace('{0}', escapeHtml(data.subject)));
		if (data.length == 0)
			return null;
		else 
			return (data.length == 1) 
					? strings[0] 
					: strings.join(msgs[data.match]);
	}

	function stringifyThen(data) {
		var msgs = {
				'discard' : '<fmt:message key="prefs.filter.message.discard"/>',
				'fileinto': '<fmt:message key="prefs.filter.message.fileinto"/>'
			};
		if (data.action == 'fileinto')
			return msgs.fileinto.replace('{0}', data.actionparam);
		// remove actionparam
		if (data.actionparam) delete data.actionparam;
		return msgs[data.action];
	}

	// ACCOUNTS

	$('#settings-tab').on('shown.bs.tab', 'a[href=#account]', function() {
		if ($('#create-account').is(':hidden')) { // lazy loading - this is the 1st time filter list is displayed
			$('#create-account').removeClass('hidden');
			$('#accounts').load('prefs/accounts', function() {
				$.getJSON('prefs/accounts/status', function(status) {
					if (status.uid && status.status >= 0) {
						$('#accounts')
							.find('#fetch-account').addClass('disabled').end()
							.find('input[name=uid][value=' + status.uid + ']')
								.prev().removeClass('fa-envelope').addClass('fa-spinner fa-spin');
						setTimeout(poll, 2000);
					}
				});
			});
		}
	});

	$('#account').on('click', '#create-account', function() {
		saveAccount('create');
	}).on('click', '#update-account', function() {
		saveAccount( getAccountUID(this) );
	}).on('click', '#delete-account', function() {
		$('#accounts').load('prefs/accounts/delete', { uid: getAccountUID(this) } );
	}).on('click', '#fetch-account', function() {
		var row = $(this).closest('tr');
		$.post('prefs/accounts/fetch', { uid: getAccountUID(this) }, function() {
			$('#accounts').find('#fetch-account').addClass('disabled');
			row.find('.fa-envelope').removeClass('fa-envelope').addClass('fa-spinner fa-spin');
			setTimeout(poll, 2000);
		});
	});

	function poll() {
		setTimeout(function() {
			$.getJSON('prefs/accounts/status', function(status) {
				if (status.uid && status.status >= 0) { // running
					poll();
				} else {	// completed
					$('#accounts')
						.find('.fa-spinner').removeClass('fa-spinner fa-spin').addClass('fa-envelope').end()
						.find('#fetch-account').removeClass('disabled');
				}
			});
		}, 2000);
	}

	function getAccountUID(elem) {
		return $(elem).closest('tr').find('input[name=uid]').val();
	}

	function saveAccount(uid) {
		eModal.ajax({
			url: 'prefs/accounts/' + uid,
			title: 'Account',
			size: 'lg',
			buttons: [
				{ text: 'Close', style: 'default', close: true },
				{ text: 'OK', style: 'primary', close: false, click: function() {
						var $form = $('form[name=fetchForm]');
						$.post($form.attr('action'), $form.serializeArray())
							.done(function(data) {
								if ($(data).is('form')) {
									$form.closest('.modal-body').html(data);
								} else {
									$('#accounts').html(data);
									eModal.close();
								}
							});
					}
				}
			]
		});
	}

});
</script>