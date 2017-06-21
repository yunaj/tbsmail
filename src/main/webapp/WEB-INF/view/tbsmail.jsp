
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Script-Type" content="text/javascript">
<meta http-equiv="Content-Style-Type" content="text/css">
<meta http-equiv="X-UA-Compatible" content="IE=edge">

<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=2, user-scalable=yes" />
<title>BridgeOffice</title>
<link rel="stylesheet" type="text/css" href="../css/layout.css">
<link rel="stylesheet" type="text/css" href="../css/print.css" media="print">
 
</head>
<body>

	<!-- Gnb -->
	<div class="Gnb"> 
    <!-- 메뉴별 gnb 컬러 변경 / gnb_00 -->
		<div class="gnb_em">
			<div class="top_logo"><span class="tx_b">BridgeOffice</span></div>
			<!-- 메뉴 시작 / 선택메뉴 활성화는 on 추가 -->
			<div class="menu">
				<ul>
					<li class=""><a href="javascript:gotoApproval()" title="HOME">HOME</a></li>
					<li class=""><a href="javascript:gotoApproval()" title="APPROVAL">APPROVAL</a></li>
                    <li class="on"><a href="javascript:gotoApproval()" title="Email">Email</a></li>
                    <li class=""><a href="javascript:gotoApproval()" title="BBS">BBS</a></li>
                    <li class=""><a href="javascript:gotoApproval()" title="Schedule">Schedule</a></li>
                    <li class=""><a href="javascript:gotoApproval()" title="Reservation">Reservation</a></li>
                    <li class=""><a href="javascript:gotoApproval()" title="Admin">Admin</a></li>
					<!--li ><a href="#" title="BBS">BBS</a></li-->
					
				</ul>
				<input type="hidden" id="selectedMenu" value="">
			</div>
			<!-- 회원정보 시작 -->
			<div class="user">
				<ul style="cursor: pointer;" id="login_view">
					<li><img src="../image/man.png" alt="user"></li>
					<li><div class="name profile"><span>jacob</span></div></li>
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

	<!--  Top 메뉴영역 끝 --> 
	<div class="clear"></div>
	<div class="Container"> 
	<!-- Lnb -->
		<div class="Side"> 
		<!-- 메뉴별 lnb 타이틀 컬러 변경 / lnb_00 -->
			<div class="lnb_em">Email</div>
			<div class="h36"></div>
			<!-- lnb별 공통 버튼 -->
			<div class="lnb_butbox" id="lnb_butbox">
				<input type="button" value="Compose" class="but_big"/>
			</div>

			<!-- 트리 삽입 영역 -->
			<div class="lnb_tree" style="background:#FFF;">
				

			</div>
            	
		</div>
		<!-- Content -->
		<div class="Content"> 
		    <!-- Content box 시작 -->
			<div class="content_box"> 
				<!-- 탑 써치 -->
				 <div class="top_search">
					<!--  <input name="textfield" type="text" id="textfield" title="search" class="input260" onClick="this.style.backgroundImage='none'">
					<input type="button" value="Search" class="but_navy" onClick="location.href='http://www.naver.com'";/> -->
				</div>
				<div class="clear"></div>
				<!-- 공통 타이틀 라인 -->
				<div class="title_box">
					<div class="title_line"><h1>Inbox</h1>						
					</div>
                   <div class="page_move_rapper float_right">
						<span class="pageNumber_view"><strong>1-10</strong> of <strong>18</strong></span>
						<span class="btn_pn_rapper">
								<a href="#" class="move_page_button" act="prev_page"><img src="../image/but_prev.png" alt="next"></a>
								<a href="#" class="move_page_button" act="next_page"><img src="../image/but_next.png" alt="next"></a>
						</span>
					</div>
 					<div class="clear"></div>
				</div>
              <!-- 테이블 삽입 영역 start-->
                        <div class="con">
                            <div class="rapper_table mb40">
                                <table summary="" class="board_type_height">
                                    <caption class="blind"></caption>
                                    <colgroup>
                                        <col width="32px"/>
                                        <col width="18%"/>
                                        <col width="18%"/>
                                        <col width="*"/>
                                        <col width="18%"/>
                                    </colgroup>
                                    <thead>
                                        <tr>
                                            <th scope="col"><span></span></th>
                                            <th scope="col"><span>Type</span></th>
                                            <th scope="col"><span>Sender Name</span></th>			
                                            <th scope="col"><span>Title</span></th>
                                            <th scope="col"><span>Sent Date</span></th>
                                        </tr>
                                    </thead>					
                                    <tbody>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
										<tr>
											<td><input type="checkbox" value="" name="attachId"/></td>
											<td class="">*>@</td>
											<td>John Smith</td>
											<td>Title #01</td>
											<td>2016.01.26 22:10</td>
										</tr>
                                         
                                    </tbody>
                                </table>
                            </div>
                            
                    
                        <div class="clear"></div>
                            
            
                        </div>
                        <!-- 테이블 삽입 영역 end-->

         </div>
		<!-- Content box 끝 -->
            
		 
	  </div>
	</div>
	<div class="clear"></div>
	<!-- Container 끝 -->
</div>


</body>

</html>

