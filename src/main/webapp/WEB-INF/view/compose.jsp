<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/wma.tld" prefix="wma" %>

<form id="compose-form" class="form-horizontal" method="post" enctype="multipart/form-data" action="send">
	<input type="hidden" name="savedraft" />
	<input type="hidden" name="draft" value="${message.draft}" />
	<input type="hidden" name="reply" value="${message.reply}" />
	<input type="hidden" name="forward" value="${message.forward}" />
<c:if test="${not empty actualmsg}">
	<input type="hidden" name="path" value="${actualmsg.folderFullName}"/>
	<input type="hidden" name="uid" value="${actualmsg.UID}"/>
</c:if>
	<div class="mail-header" style="float: right;">
		<a id="sendmail" class="btn btn-default btn-sm">
			<i class="fa fa-reply"></i> <fmt:message key="menu.send"/>
		</a>
		<a id="savedraft" class="btn btn-default btn-sm">
			<i class="fa fa-pencil"></i> <fmt:message key="menu.savedraft"/>
		</a>
		<div class="btn-group">
			<a id="options" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown">
				<i class="fa fa-gear"></i> <span class="caret"></span>
			</a>
			<ul class="dropdown-menu dropdown-menu-form">
				<li>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="secure" value="true"> <fmt:message key="compose.secure"/>
						</label>
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="urgent" value="true"> <fmt:message key="compose.urgent"/>
						</label>          
					</div>
					<div class="checkbox">
						<label>
							<input type="checkbox" name="archivesent" value="true"
							<c:if test="${prefs.autoArchiveSent}">checked</c:if>
							/> <fmt:message key="prefs.autoarchive"/>
						</label>          
					</div>
					<c:if test="${not empty actualmsg && not empty actualmsg.attachParts}">
					<div class="checkbox">
						<label>
							<input type="checkbox" id="autoattach"
					<c:if test="${prefs.autoAttach}">checked</c:if>
							/> <fmt:message key="prefs.autoattach"/>
						</label>          
					</div>
					</c:if>
				</li>
			</ul>
		</div>
		<a id="attach" class="btn btn-default btn-sm">
			<i class="fa fa-paperclip"></i>
		</a>
	</div>
	<div class="mail-content">
		<table class="board_width_borderNone">
			<caption class="blind"></caption>
			<colgroup>							
				<col width="100px"/>
				<col width="70px"/>
				<col width="*"/>
				<col width="80px"/>
				<col width="63px"/>
			</colgroup>
			<tbody>
				<tr>
					<th scope="row" ><label for="label_1"><fmt:message key="label.subject"/></label></th>
					<td colspan="4">
						<div class="ui_input_text">
							<input type="text" name="subject" id="subject" size="60" maxlength="60"/>
							<br/>
						</div>
					</td>
				</tr>
				<tr>
					<th scope="row" ><label for="label_1"><fmt:message key="label.to"/></label></th>
					<td colspan="4">
						<div class="ui_input_text">
							<input type="text" name="to" id="to" size="60" maxlength="60"/>
							<br/>
						</div>
					</td>
				</tr>
				<tr>
					<th scope="row" ><label for="label_1"><fmt:message key="label.cc"/></label></th>
					<td colspan="4">
						<div class="ui_input_text">
							<input type="text" name="cc" id="cc" size="60" maxlength="60"/>
							<br/>
						</div>
					</td>
				</tr>
				<tr>
					<th scope="row" ><label for="label_1"><fmt:message key="label.bcc"/></label></th>
					<td colspan="4">
						<div class="ui_input_text">
							<input type="text" name="bcc" id="bcc" size="60" maxlength="60"/>
							<br/>
						</div>
					</td>
				</tr>
				
				<tr>
					<th scope="row" ><label for="label_1"><fmt:message key="label.content"/></label></th>
					<td colspan="4">
						<c:choose>
						<c:when test="${not empty actualmsg}">
							<textarea id="summernote" name="body">
							<c:if test="${prefs.autoQuote}">
								<c:out value="${message.body}" escapeXml="false"/>
							</c:if>
								<c:out value="${actualmsg.body}"/>
							</textarea>
							<c:if test="${not empty actualmsg.attachParts}">
							<div id="org-attach" class="mail-attachment">
								<c:forEach var="attach" items="${actualmsg.attachParts}" varStatus="status">
								<label class="checkbox-inline">
									<input type="checkbox" name="parts" value="${attach.partNumber}" 
							<c:if test="${prefs.autoAttach}">checked</c:if>		
									/>
									<c:out value="${attach.name}"/> <span>(<wma:size value="${attach.size}"/>)</span>
								</label>
								</c:forEach>
							</div><!-- /.mail-attachment -->
							</c:if>
						</c:when>
						<c:otherwise>
							<textarea id="summernote" name="body"></textarea>
						</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<th scope="row" ><label for="label_1"><fmt:message key="compose.reserve.tosend"/></label></th>
					<td colspan="4">
						<div class="form-inline">
							<input type="checkbox" name="reserve" value="true">
							<input type="text" id="reserve-d" name="date" class="form-control input-sm"/>
							<select id="reserve-h" name="hour" class="form-control input-sm"></select>
							<strong>:</strong>
							<select id="reserve-m" name="minute" class="form-control input-sm"></select>
						</div>
					</td>
				</tr>
			</tbody>
		</table>	
		
		
		<div id="attachments" class="form-group hidden">
			<label class="col-sm-2 control-label"><fmt:message key="label.attach"/>:</label>
			<div class="attachments col-sm-10">
				<input type="file" name="attachment[]" />
			</div>
		</div>
	
	</div>
</form>
<div class="hidden">
<c:choose>
	<c:when test="${message.draft}">
	<span id="subject"><c:out value="${actualmsg.subject}"/></span>
	</c:when>
	<c:when test="${message.reply}">
	<span id="subject"><fmt:message key="compose.subject.reply"><fmt:param value="${actualmsg.subject}"/></fmt:message></span>
	</c:when>
	<c:when test="${message.forward}">
	<span id="subject"><fmt:message key="compose.subject.forward"><fmt:param value="${actualmsg.subject}"/></fmt:message></span>
	</c:when>
	<c:otherwise>
	<span id="subject"></span>
	</c:otherwise>
</c:choose>
	<span id="to"><c:out value="${message.to}"/></span>
	<span id="cc"><c:out value="${message.CC}"/></span>
</div>
<script>
$(function() {
	console.log('compose');

	function options(elem, str) {
		var o = elem.get(0).options, 
		    a = str.split(',');
		for (var i = 0; i < a.length; i++) o[i] = new Option(a[i]);
	}

	var now = new Date();
	$('#compose-form')
		.find('input[name=subject]').val($('#subject').text()).end()
		.find('input[name=to]').val($('#to').text().replace(/\n\s+/gm,'')).end()
		.find('input[name=cc]').val($('#cc').text().replace(/\n\s+/gm,'')).end()
		.find('input[name=date]').datepicker({autoSize:true,minDate:now}).datepicker("setDate",now);
	
	options($('#reserve-h'), '00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16,17,18,19,20,21,22,23');
	options($('#reserve-m'), '00,10,20,30,40,50');

	$('#attachments > .attachments').bootstrapFileInput();
	$('#summernote').summernote({ height: 300 });

	$('#compose-form').on('change', '#autoattach', function() {
		$('#org-attach').find('input[name=parts]').prop('checked', $(this).is(':checked'));
	}).on('click', '.field-options a', function() {
		var what = $(this).attr('href');
		$(this).hide();
		$(what).closest('.form-group').removeClass('hidden');
		$(what).focus();
	}).on('click', '#attach', function() {
		$('#attachments :file:last').trigger('click');
	}).on('change', ':file', function() {
		if ($('#attachments').is(':hidden')) $('#attachments').removeClass('hidden');
	}).on('click', '#sendmail', function(event) {
		$(event.delegateTarget).submit();
	}).on('click', '#savedraft', function(event) {
		var $form = $(event.delegateTarget);
		$form.find('input[name=savedraft]').val(true);
		$form.submit();
	}).on('submit', function(event) {
		event.preventDefault(); event.stopPropagation();
		var savedraft = parseBool($(this).find('input[name=savedraft]').val()),
		    reply = parseBool($(this).find('input[name=reply]').val()),
		    uid = reply ? $(this).find('input[name=uid]').val() : -1;
		$(this).ajaxSubmit({
			beforeSerialize: function($form, options) {
				$('#summernote').html($('#summernote').code());
			},
			beforeSubmit: function(arr, $form, options) {
				if (!validate(savedraft)) {
					return false;
				}
        		$('#modal')
        			.find('.modal-header,.modal-footer').hide().end()
        			.find('.modal-body').html('<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><h4>' + (savedraft ? 'Saving' : 'Sending') + '</h4>').end().modal('show');
			},
			success: function() {
				$('#modal').modal('hide'), removetab('sub-tab');
				if (savedraft && $('#main-tab').find('#draft').length != 0) refresh();
				else if (reply) {
					var tr = $('#main-tab').find('input[name=uids][value=' + uid + ']').closest('tr');
					if (tr.length != 0) {
						if (tr.find('.fa-reply').length == 0) {
							tr.find('a[href=#open]').before($('<i class="fa fa-reply"></i>'));
						}
					}
				}
			},
			error: function(xhr, status, error) {
				$('#modal').find('.modal-header,.modal-footer').show().end()
					.find('.modal-body').html(xhr.responseText);
			}
		});
		return false;
	});

	function validate(savedraft) {
		var $form = $('#compose-form'),
			$input = $form.find('input[name=subject]'),
			$error = null;
		if ($input.val().trim()) {
			if (!savedraft)
				$.each(['bcc','cc','to'], function(index, name) {
					$error = $form.find('input[name=' + name + ']');
					if ($error.val().trim()) { 
						$error = null; 
						return false; // break the loop
					}
				});
		} else 
			$error = $input;
		
		if ($error) {
			$error.focus().closest('.form-group').addClass('has-error');
			return false;
		}
		return true;
	}

});
</script>	