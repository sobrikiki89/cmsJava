<%@ include file="../../../layouts/commontags.jsp"%>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
	  	    <div class="panel panel-primary">	  
				<div class="panel-heading"><spring:message code="usermgmt.user.change_passwd" /></div>
				<spring:url var="saveUrl" value="/secured/usermgmt/changepasswd" />
				<form:form action="${saveUrl}" commandName="changePasswordEditForm" method="post">
					<form:errors element="div" cssClass="errorblock" path="*" />
					<form:hidden path="userId"/>
					<div class="panel-body">
						<div class="form-group row">
							<label class="control-label col-sm-3" for="currentPassword">
								<spring:message code="usermgmt.user.current_password" /><span class="mandatory">*</span>
							</label>
							<span class="col-sm-3">
								<form:password path="currentPassword" class="form-control input-sm" showPassword="true" id="currentPassword" />
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-3" for="password">
								<spring:message code="usermgmt.user.new_password" /><span class="mandatory">*</span>
							</label>
							<span class="col-sm-3">
								<form:password path="password" class="form-control input-sm" showPassword="true" id="password" />
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-3" for="confirm_password">
								<spring:message code="usermgmt.user.confirm_password" />
							</label>
							<span class="col-sm-3">
								<form:password path="confirmPassword" class="form-control input-sm" showPassword="true" id="confirm_password" />
							</span>
						</div>
					</div>
					<div class="panel-footer">					
						<input type="submit" class="btn btn-primary" value="<spring:message code="button.submit" />" />
					</div>				
				</form:form>
			</div>
		</div>
	</div>
</div>
