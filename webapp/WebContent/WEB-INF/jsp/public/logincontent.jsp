<%@ include file="../layouts/commontags.jsp"%>
<c:if test="${not empty error}">
	<div class="errorblock">${errormsg}</div>
</c:if>

<spring:url var="loginUrl" value="/login" />
<div class="container-fluid">
	<form name="f" method="post" action="${loginUrl}">
	<div class="row">
	    <div class="col-sm-4"></div>
	  	<div class="panel-group col-sm-4">
		    <div class="panel panel-primary">
				<div class="panel-heading"><spring:message code="login.header" /></div>
				<div class="panel-body">
					<h4><span class="label label-default"><spring:message code="login.username" /></span></h4>
					<input type="text" name="username" />				
					<h4><span class="label label-default"><spring:message code="login.password" /></span></h4>
					<input type="password" name="password"/>
				</div>
				<div class="panel-footer">
					<input name="submit" type="submit" class="btn btn-primary" value="<spring:message code="login.submit" />" />
				</div>
				<input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}" />
			</div>
		</div>    	
		<div class="col-sm-4"></div>		
	</div>
	</form>
</div>