<%@ include file="../../../layouts/commontags.jsp"%>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
	  	    <div class="panel panel-primary">	  
				<div class="panel-heading"><spring:message code="menu.switch.role" /></div>
				<spring:url var="saveUrl" value="/secured/usermgmt/changerole" />
				<form:form action="${saveUrl}" commandName="changeRoleEditForm" method="post">
				<div class="panel-body">
					<spring:message code="usermgmt.changerole.selectrole" />
					<form:select path="roleId">
						<form:options items="${roles}" itemLabel="name" itemValue="id" />
					</form:select>
				</div>
				<div class="panel-footer">					
					<input type="submit" class="btn btn-primary" value="<spring:message code="button.submit" />" />
				</div>				
				</form:form>
			</div>
		</div>
	</div>
</div>
