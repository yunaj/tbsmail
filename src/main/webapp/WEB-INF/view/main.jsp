<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>BridgeOffice</title>

<link rel="stylesheet" href="css/bootstrap.css"/>
<link rel="stylesheet" href="css/font-awesome.min.css"/>
<link rel="stylesheet" href="css/jquery-ui.css"/>
<link rel="stylesheet" href="css/skin-lion/ui.fancytree.css"/>
<link rel="stylesheet" href="css/summernote.css"/> 
<link rel="stylesheet" href="css/default.css"/>
<link rel="stylesheet" href="css/egovframework/com/cmm/layout.css"/>
<link rel="stylesheet" href="css/egovframework/com/cmm/form.css"/>
<link rel="stylesheet" href="css/egovframework/com/cmm/print.css" media="print">

<style type="text/css">
h4 {color: #323a45;}
</style>

</head>
<body>
<div class="wrap">
	<!--  Top 메뉴영역 시작 --> 
	<div class="Gnb"> 
   		<!-- 메뉴별 gnb 컬러 변경 / gnb_00 -->
		<div class="gnb_em">
			<div class="top_logo"><span class="tx_b">BridgeOffice</span></div>
			<!-- 메뉴 시작 / 선택메뉴 활성화는 on 추가 -->
			<div class="menu">
				<ul>
					<li class=""><a href="javascript:gotoApproval()" title="Home">Home</a></li>
					<li class=""><a href="javascript:gotoApproval()" title="Approval">Approval</a></li>
	                   <li class="on"><a href="javascript:gotoApproval()" title="Email">Email</a></li>
	                   <li class=""><a href="javascript:gotoApproval()" title="BBS">BBS</a></li>
	                   <li class=""><a href="javascript:gotoApproval()" title="Schedule">Schedule</a></li>
	                   <li class=""><a href="javascript:gotoApproval()" title="Reservation">Reservation</a></li>
					<li class=""><a href="javascript:gotoApproval()" title="Contact">Contact</a></li>
					<li class=""><a href="javascript:gotoApproval()" title="SNS">SNS</a></li>
				</ul>
				<input type="hidden" id="selectedMenu" value="">
			</div>
			<!-- 회원정보 시작 -->
			<div class="user">
				<ul style="cursor: pointer;" id="login_view">
					<li><img src="images/egovframework/com/uss/cmm/man.png" alt="user"></li>
					<li><div class="name profile">David.kim</div></li>
					<li>
						<div class="view">
							<a href="#"  title="view"></a>
						</div>
					</li>
				</ul>
		 	</div>
			<!-- 회원정보 끝 --> 
		</div>
	</div>
	<!-- Gnb 끝 -->
	<div class="clear"></div>
	<!--  Top 메뉴영역 끝 --> 

	
	<div class="Container">
	 
		<!-- Left menu 시작 -->
		<div class="Side"> 
			<!-- 메뉴별 lnb 타이틀 컬러 변경 / lnb_00 -->
			<div class="lnb_em">Email</div>
			<div class="h36"></div>
			<!-- lnb별 공통 버튼 -->
			<div class="lnb_butbox" id="lnb_butbox">
				<input type="button" id="compose" value='<fmt:message key="menu.compose"/>' class="but_big"/>
			</div>
			
			<!-- 트리 삽입 영역 -->
			<div class="lnb_tree" style="background: #FFF;">
				<div id="navigation">
					<ul class="nav nav-stacked" id="side-menu">
						<li role="presentation"><a
							data-target="${store.inboxInfo.path}">
								<fmt:message key="prefs.inbox" /> <span id="inbox-unread"
								class="label label-unreadmail pull-right"></span>
						</a></li>
						<li role="presentation"><a
							data-target="${store.sentMailArchive.path}"> ${store.sentMailArchive.name}
						</a></li>
						<li role="presentation"><a
							data-target="${store.toSendArchive.path}"> ${store.toSendArchive.name}
						</a></li>
						<li id="trashInfo" role="presentation"><a
							data-target="${store.trashInfo.path}"> ${store.trashInfo.name}
						</a></li>
						<li role="presentation"><a
							data-target="${store.draftInfo.path}"> ${store.draftInfo.name}
						</a></li>
						<li id="personalArchive" role="presentation"><a
							data-target="${store.personalArchive.path}"> ${store.personalArchive.name} <span
								class="fa arrow collapsed" data-toggle="collapse"
								data-target="#tree-container"></span>
						</a>
							<div class="sub-nav collapse" id="tree-container">
								<div id="tree"></div>
								<div class="folder-menu clearfix">
									<span class="pull-right"> <a id="create-folder"
										class="btn btn-default btn-xs"
										title="<fmt:message key='menu.folder.create'/>"><i
											class="fa fa-plus"></i></a> <a id="delete-folder"
										class="btn btn-default btn-xs"
										title="<fmt:message key='menu.folder.delete'/>"><i
											class="fa fa-remove"></i></a> <a id="rename-folder"
										class="btn btn-default btn-xs"
										title="<fmt:message key='menu.folder.rename'/>"><i
											class="fa fa-edit"></i></a> <a id="manage-folder"
										class="btn btn-default btn-xs"
										title="<fmt:message key='menu.folder.manage'/>"><i
											class="fa fa-wrench"></i></a>
									</span>
								</div>
							</div>
						</li>
						<li><a id="settings"> <fmt:message key="menu.settings" /></a></li>
					</ul>
					<!-- /.sidebar-quota -->
					<ul class="nav nav-stacked">
						<%-- <li role="presentation"><a> <fmt:message
									key='prefs.publicfolder' /></a>
							<div class="sub-nav" id="namespace-container">
								<div id="namespaces">
									<ul id="namespaces-data">
										<c:forEach var="ns" items="${namespaces}">
											<li id="${ns.path}" class="folder lazy unselectable">${ns.name}</li>
										</c:forEach>
									</ul>
								</div>
							</div>
						</li> --%>
					</ul>
					<!-- /#side-menu.nav -->
					<div class="sidebar-quota">
						<div id="quota" class="text-right text-muted">{0} of {1}
							used</div>
						<div class="progress progress-bar-xs">
							<div class="progress-bar progress-bar-primary" role="progressbar"
								aria-valuenow="60" aria-valuemin="0" aria-valuemax="100"
								style="width: 0%;">
								<span class="sr-only"></span>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- 트리 삽입 영역 -->
		</div>
		<!-- Left menu 끝 -->
	

		<!-- Content -->
		<div class="Content"> 
		<!-- Content box 시작 -->
			<div class="content_box"> 
				<div class="top_search"></div>
				<div class="clear"></div>
				<div class="title_box">
					<div class="title_line"><h1><fmt:message key="prefs.inbox" /></h1></div>
				</div>
				
				<div class="con">
                <div class="rapper_table mb40">
				    <div role="tabpanel" class="tab-pane active" id="main-tab">
				    	<a href="<c:url value='folder/messages'>
				  			<c:param name='path' value='INBOX'/>
				  			<c:param name='criteria' value='${prefs.inboxType}'/>
				  			<c:param name='pageSize' value='${prefs.pageSize}'/>
						</c:url>"></a>
					<!-- Simple splash message -->
					<div class="splash">
						<h1><i class="fa fa-spinner fa-spin fa-lg"></i></h1>
					</div>
				    </div><!-- /#main-tab.tab-pane -->
				    <div role="tabpanel" class="tab-pane" id="sub-tab">
				    </div><!-- /#sub-tab.tab-pane -->
			    </div><!-- rapper_table mb40 끝 -->
			    </div><!-- con 끝 -->

			</div>
			<!-- Content box 끝 -->
		</div>
	  	<!-- Content 끝 -->
	</div>
	<!-- Container 끝 -->
	<div class="clear"></div>
</div>
<!-- wrap 끝  -->

<!-- Modal dialog template 시작 -->
<div id="modal" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
    	<div class="modal-header">
	      	<button type="button" class="close" data-dismiss="modal" aria-label="Close">
	      		<span aria-hidden="true">&times;</span>
	      	</button>
      		<h4 class="modal-title">Modal title</h4>
    	</div>
      	<div class="modal-body text-center"></div>
     	<div class="modal-footer">
      		<button type="button" class="btn btn-default" data-dismiss="modal"><fmt:message key="menu.close"/></button>
  		</div>
    </div>
    <!-- /.modal-content -->
  </div>
  <!-- /.modal-dialog -->
</div>
<!-- Modal dialog template 끝 -->


<!-- EgovCopyright 시작  -->
<!-- <div class="layerPopup_rapper profilePop" style="display: none; position:absolute !important; right:20px; left:inherit !important; top:200px; margin:0 !important;">
    <iframe name="iframePopup" id="iframePopup" src=""></iframe>
</div>
<div id="shadow_profile" class="shadow_profile" style="display: none;"></div> -->
<!-- EgovCopyright 끝 -->

<script src="js/jquery/jquery.js"></script>
<script src="js/jquery/jquery-ui.js"></script>
<script src="js/jquery/jquery.fancytree.js"></script>
<script src="js/jquery/jquery.pagination.js"></script>
<script src="js/jquery/jquery.form.js"></script>
<script src="js/jquery/jquery.deserialize.js"></script>
<script src="js/jquery/jquery.slimscroll.js"></script>
<script src="js/bootstrap.js"></script>
<script src="js/eModal.js"></script>
<script src="js/summernote.js"></script>
<script src="js/hedwig.webmail.js"></script>
<script>
var wma_separator = '${store.folderSeparator}',
	invalid_chars = '~ . \\ / : * ? " < > |';

function addtab(tabid) {
	$('#tab').clone().attr('id','')
		.find('a').attr({'href':'#' + tabid, 'aria-controls': tabid})
		.end().appendTo($('#tablist'));
	return $('<div role="tabpanel" class="tab-pane"/>')
		.attr('id', tabid)
		.appendTo($('#tab-content'));
}
function loadtab(tabid, tabname, url, data) {
	$('#' + tabid).load(url, data, function() { showtab(tabid, tabname); });
}
function showtab(tabid, tabname) {
	if (!$('#' + tabid).length) return false;
	var tab = $('#tablist a[href=#' + tabid + ']').tab('show').closest('li').removeClass('hidden').end();
	console.log();
	if (tabname) {
		if(tab.has('button').length){
			tab.html(tab.html().replace(/.*(<[^\/].*\/.*>).*/, "$1" + tabname));
		}else{
			tab.html(tabname);
			$(".title_line h1").text($.trim(tabname));
		}
		return true;
	}
	
}
function removetab(tabid) {
	$('#tablist a[href=#main-tab]').tab('show');
	if (tabid === 'sub-tab') $('#tablist a[href=#sub-tab]').closest('li').addClass('hidden'), $('#sub-tab').empty();
	else $('#tablist a[href=#' + tabid + ']').closest('li').remove(), $('#' + tabid).remove();
}
function refresh() {
	$('#main-tab').load('folder/messages', $('#msg-list-form').serialize());
}
function gotopage(page) {
	$('#page').val(page);
	$('select[name=criteria]').val($('input[name=_criteria]').val());
	$('input[name=term]').val($('input[name=_term]').val());
	refresh();
}
function showQuota() {
	$.getJSON('quota', function(quotas) {
		if (quotas && quotas.length > 0) {
			if (quotas[0].resources && quotas[0].resources.length > 0) {
				var resource = quotas[0].resources[0];
				if (resource.limit > 0) {
					var percent = Math.min(100, Math.round((resource.usage / resource.limit) * 100));
					$('#quota').text(formatBytes(resource.usage) + ' of ' + formatBytes(resource.limit) + ' used')
						.parent().find('.progress-bar').css('width', percent + '%')
							.find('.sr-only').text(percent + '% used');

				} else {
					$('#quota').text(formatBytes(resource.usage) + ' used');
				}
			}
		}
	});
}
$(function() {
	$('#tree').fancytree({
		source: {
			url: 'folder/tree?' + $.param({path:$('#personalArchive > a').data('target')})
		},
		activate: function(event, data) {
			var node = data.node;
			loadtab('main-tab', node.title, 'folder/messages', $.param({path:node.key}));
		}
	});
	$('#namespaces').fancytree({
		activate: function(event, data) {
			var node = data.node;
			loadtab('main-tab', node.title, 'folder/messages', $.param({path:node.key}));
		},
		lazyLoad: function(event, data) {
			var node = data.node;
			data.result = $.getJSON('folder/tree?' + $.param({path:node.key,recursive:false}));
		}
	});
	$('#tree,#namespaces').bind('fancytreeblurtree', function(event, data) {
		var tree = $(event.delegateTarget).fancytree('getTree'),
		    node = tree.getActiveNode();
		if (node) {
			node.setActive(false), node.setFocus(false);
		}
	}).bind('fancytreefocustree', function(event, data) {
		if (!data.node.unselectable) {
			$('#side-menu').find('.active').removeClass('active');
			$(event.delegateTarget).addClass('active');
		}
	}).bind('fancytreebeforeactivate', function(event, data) {
		return !data.node.unselectable;
	});

	showQuota();

	$('#main-tab').load($('#main-tab > a').attr('href'));

	$('#tablist').on('click', 'li a > .close', function(e) {
		e.preventDefault(), e.stopPropagation();
		removetab($(this).closest('a').attr('href').substring(1));
	});
	$('#side-menu').on('click', 'a[data-target]', function(event) {
		var path = $(this).data('target'),
		name = $(this).html().replace(/<.*[^\/]\/.*>/g, ''); // remove tags
		loadtab('main-tab', name, 'folder/messages', $.param({path:path}));
	});
	$('#compose').click(function() {
		loadtab('main-tab', $(this).val(), 'message/compose');
	});
	$('#create-folder').click(function() {
		eModal.prompt({title:'<fmt:message key="main.folder.promptname"/>'})
			.then(function(name) {
				if (!isValidMboxName(name)) {
					alert('<fmt:message key="main.folder.invalidname"/>\n' + invalid_chars);	
					return;
				}
				var tree = $('#tree').fancytree('getTree'),
					node = tree.getActiveNode();
				if (!node) {
					path = $('#personalArchive > a').data('target');
					node = tree.getRootNode();
				} else {
					path = node.key;
				}
				if (tree.getNodeByKey(path + wma_separator + name)) {
					alert('<fmt:message key="main.folder.alreadyexist"/>');
					return;
				}
				$.post('folder/create', {name:name,path:path}, function(data) {
					node.addNode(data);
				});
			});
	});
	$('#delete-folder').click(function() {
		var tree = $('#tree').fancytree('getTree'),
			node = tree.getActiveNode();
		if (node) {
			if (!node.hasChildren()) {
				eModal.confirm({
					message: '<fmt:message key="main.folder.confirm.delete"/>',
					label: 'Yes'
				}).then(function() {
					$.post('folder/delete', {path:node.key}, function() {
						var parent = node.getParent();
						node.remove();
						if (!parent.isRootNode()) parent.setActive();
						else $('#personalArchive > a').trigger('click');
					});
				});
			} else eModal.alert('<fmt:message key="main.folder.haschildren"/>');
		} else eModal.alert('<fmt:message key="main.folder.select"/>');
	});
	$('#rename-folder').click(function() {
		var tree = $('#tree').fancytree('getTree'),
			node = tree.getActiveNode();
		if (node) {
			eModal.prompt({title:'<fmt:message key="main.folder.promptname"/>'})
				.then(function(name) {
					if (!isValidMboxName(name)) {
						alert('<fmt:message key="main.folder.invalidname"/>\n' + invalid_chars);
						return;
					}
					var destfolder = node.key.substring(0, node.key.lastIndexOf(wma_separator) + 1) + name;
					if (tree.getNodeByKey(destfolder)) {
						alert('<fmt:message key="main.folder.alreadyexist"/>');
						return;
					}
					$.post('folder/rename', {path:node.key,destfolder:destfolder}, function(data) {
						node.key = data.key, node.setTitle(data.title);
					});
				});
		} else eModal.alert('<fmt:message key="main.folder.select"/>');
	});
	$('#manage-folder').click(function() {
		loadtab('sub-tab', $(this).attr('title'), 'folder/manage');
	});
	$('#settings').on('click', function() {
		loadtab('sub-tab', $(this).text(), 'prefs');
	});

	$('#tab-content').on('click', '#refresh', refresh
	).on('click', '#reply, #replyall, #forward', function() {
		var data = $(this).closest('form').serializeArray();
		data.push({name:$(this).attr('id'),value:true});
		loadtab('sub-tab', $('#compose').text(), 'message/compose', $.param(data));
	}).on('click', '#delete-msg', function() {
		var form = $(this).closest('form'),
			path = form.find('input[name=path]').val()
			uid = form.find('input[name=uid]').val();
		eModal.confirm('<fmt:message key="message.confirm.delete"/>')
			.then(function() {
				$.post('message/delete', $.param({path:path,uids:uid}), function() {
					removetab('tab-' + uid);
					if ($('#path').val() == path
							&& $('#msg-list-form').find('input[name=uids][value=' + uid + ']').length > 0) {
						refresh();
					}
				});
			});
	}).on('click', '#raw-msg', function() {
		window.open('message/raw?' + $(this).closest('form').serialize());
	}).on('click', '#print', function() {
		window.open('message?' + $(this).closest('form').serialize() + '&print=true');
	}).on('click', '#prev, #next', function() {
		var form = $(this).closest('form'), 
			msg = serializeObject(form),
		    offset = $(this).attr('id') == 'prev' ? -1 : 1;
		$.getJSON('message/uid', {path:msg.path,number:msg.number,offset:offset}, function(uid) {
			if (!showtab('tab-' + uid)) {
				var pane = form.closest('.tab-pane'),
					tab = $('#tablist').find('a[href=#tab-' + msg.uid + ']'),
					title = tab.html();
				pane.load('message', $.param({path:msg.path,uid:uid}), function() {
					pane.attr('id', 'tab-' + uid);
					tab.attr('href', '#tab-' + uid).attr('aria-controls', 'tab-' + uid)
					   .html(title.replace(/.*(<[^\/].*\/.*>).*/, "$1" + pane.find('.mail-title').text()));
				});
			}
		});
	}).on('click', '.mail-attachment ul.attachment a[id]', function() {
		var form = $(this).closest('form');
		form.find('input[name=part]').val($(this).attr('id').substring(4));
		window.open('message/part?' + form.serialize());
	}).on('click', '.mail-body address', function() {
		$(this).toggleClass('showcc');
	});

	$('#modal').on('hidden.bs.modal', function() {
		$(this).find('.modal-body').empty();
	});
});
</script>
</body>
</html>