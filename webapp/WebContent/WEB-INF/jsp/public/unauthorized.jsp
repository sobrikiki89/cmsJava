<%@ include file="../layouts/commontags.jsp"%>

<spring:message var="title" code="home.title" />

<tiles:insertDefinition name="unsecured-template">
	<tiles:putAttribute name="title" value="${title}" />
	<tiles:putAttribute name="content">	
		<div class="container-fluid">
			<div class="row">
			    <div class="col-sm-1"></div>
			  	<div class="panel-group col-sm-10">
				    <div class="panel panel-primary">
						<div class="panel-heading">
							<p><spring:message code="secured.unauthorized.header" /></p>
						</div>
						<div class="panel-body">
							<p><spring:message code="secured.unauthorized.body" /></p>
						</div>
						<div class="panel-footer">
							<form method="post" id="logoutForm" action="<spring:url value="/logout" />">
								<input type="hidden" name="${_csrf.parameterName}"   value="${_csrf.token}" />
								<input name="submit" type="submit" class="btn btn-primary" value="<spring:message code="button.close" />" />
							</form>
						</div>
					</div>
				</div>
			    <div class="col-sm-4"></div>
			</div>
		</div>
	</tiles:putAttribute>
</tiles:insertDefinition>