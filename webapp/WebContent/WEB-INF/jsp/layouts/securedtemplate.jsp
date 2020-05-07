
<%@ include file="commontags.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
<title><spring:message code="secured.home.title" /></title>
<link rel='stylesheet' type="text/css" href="<spring:url value="/webjars/font-awesome/4.7.0/css/font-awesome.min.css"/>" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/<spring:theme code="css"/>" />
<link rel="stylesheet" type="text/css" href="<spring:url value="/webjars/datetimepicker/2.5.4/build/jquery.datetimepicker.min.css"/>" />
<link rel="stylesheet" type="text/css" href="<spring:url value="/resources/css/site.css" />" />
<link rel="stylesheet" type="text/css" href="<spring:url value="/webjars/github-com-kartik-v-bootstrap-fileinput/4.3.5/css/fileinput.min.css"/>" />

<style type="text/css">
/* .backgroundImage::before { 
     background: url('<spring:url value="/resources/image/Sterling.png"/>') no-repeat center center; 
 } */
</style>

<script type="text/javascript" src="<spring:url value="/webjars/jquery/3.2.1/jquery.min.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/webjars/github-com-kartik-v-bootstrap-fileinput/4.3.5/js/fileinput.min.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/webjars/jquery-number/2.1.6/jquery.number.min.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"/>"></script>
<script type="text/javascript" src="<spring:url value="/webjars/momentjs/2.18.1/min/moment.min.js"/>"></script>
<script type="text/javascript">

$(function () {
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});
});
	
$(document).ready(function() {
	$('ul.dropdown-menu [data-toggle=dropdown]').on('click', function(event) {
		event.preventDefault(); 
		event.stopPropagation(); 
		$(this).parent().siblings().removeClass('open');
		$(this).parent().toggleClass('open');
	});
	
	$("#logout").click(function(e) {
		$("#logoutForm").submit();
   	});
});
</script>  
</head>
<body>
	<div class="backgroundImage"></div>
	<nav class="navbar navbar-default">
		<div class="container-fluid">
    		<div class="navbar-header">
      			<a href="#" class="navbar-brand" style="padding-left:0px; padding-top:0px">
      				<span class=navbar-brandimg><img src='<spring:url value="/resources/image/Sterling.png"/>' ></img></span>
      				<spring:message code="home.header"/>      				
      			</a>
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
					<span class="icon-bar"></span>				
					<span class="icon-bar"></span>				
					<span class="icon-bar"></span>	
				</button>				    		
    		</div>				
		    <div class="collapse navbar-collapse" id="myNavbar">
				<ul class="nav navbar-nav">
					<li>
						<a href="<spring:url value='/secured' />">
							<sec:authentication property="principal.username" var="currentUser"/>
							<spring:message code="secured.home.header" arguments="${currentUser}"/>						
						</a>
					</li>
					<sec:authentication property="principal.menuList" var="menuList" />${menuList}
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li>
						<a href="#" data-toggle="modal" data-target="#optionsDlg">
							<span class="glyphicon glyphicon-cog"></span> <spring:message code="options" />
						</a>
					</li>
					<li>
						<a id="logout" href="#">
							<span class="glyphicon glyphicon-log-out"></span> <spring:message code="menu.logout" />
						</a>
						<form method="post" id="logoutForm" action="<spring:url value="/logout" />">
							<input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}" />
						</form>
					</li>					
				</ul>		        
			</div>
    	</div>
	</nav>				
	<div class="panel-body">
		<tiles:insertAttribute name="content" />
	</div>
	<footer class="footer">
    	<div class="container">
			<span class="text-muted"><spring:message code="home.footer" htmlEscape="false"/></span>
		</div>
	</footer>	
	<tiles:insertAttribute name="options-modal" />
	<tiles:insertAttribute name="additional-modal" />
	<tiles:insertAttribute name="timeout-modal" />
</body>
<script type="text/javascript" src="<spring:url value="/webjars/datetimepicker/2.5.4/build/jquery.datetimepicker.full.min.js"/>"></script>
</html>