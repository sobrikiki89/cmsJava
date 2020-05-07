<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/usermgmt/user" />
<spring:url var="saveUrl" value="/secured/usermgmt/user/edit" />

<script type="text/javascript">

	$(document).ready(function() {
		$('#userForm').on('keyup keypress', function(e) {
			  var keyCode = e.keyCode || e.which;
			  if (keyCode === 13) { 
			    e.preventDefault();			   
			    return false;
			  }
		});
	});

</script>

<div class="container-fluid">
	<div class="row">
		<div class="col-sm-1"></div>
		<div class="panel-group col-sm-10">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="usermgmt.user.user_listing" />
				</div>
				<form:form commandName="userForm" method="post" action="${saveUrl}"
					role="form" class="form-horizontal">
					<form:errors element="div" cssClass="errorblock" path="*" />
					<div class="panel-body">
						<ul class="nav nav-tabs">
							<li class="active">
								<a data-toggle="tab" href="#tab-UserProfile"><spring:message code="usermgmt.user.tab.user_profile" /></a>
							</li>
							<c:if test="${userForm.allowedChange}">
								<li>
									<a data-toggle="tab" href="#tab-userRole"><spring:message code="usermgmt.user.tab.user_role" /></a>
								</li>
							</c:if>
						</ul>
						<div class="tab-content">
							<div id="tab-UserProfile"
								class="tab-pane fade in active panel-body">
								<div class="form-group row">
									<label class="control-label col-sm-3" for="jobTitle">
										<spring:message code="usermgmt.user.job_title" />
									</label> 
									<span class="col-sm-3">
										<form:input path="user.jobTitle" class="form-control input-sm" id="jobTitle" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="firstname">
										<spring:message code="usermgmt.user.firstname" /><span class="mandatory">*</span>
									</label> 
									<span class="col-sm-8">
										<form:input path="user.firstName" class="form-control input-sm" id="firstname" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="lastname">
										<spring:message code="usermgmt.user.lastname" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-8">
										<form:input path="user.lastName" class="form-control input-sm" id="lastname" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="email">
										<spring:message code="usermgmt.user.email" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-4">
										<form:input path="user.email" class="form-control input-sm" id="email" />
									</span>
								</div>
								<%--
								<div class="form-group row">
	    							<label class="control-label col-sm-3" for="company">
	    								<spring:message code="usermgmt.user.company" /><span class="mandatory">*</span>
	    							</label>
	    							<span class="col-sm-8" >
	    								<form:hidden path="allowedChange" id="allowedChange"/>
	    								<form:select path="company" id="company" style="width:100%" class="form-control input-sm" >
				    						<form:option value="" label="Select..."></form:option>
											<form:options items="${userForm.userCompany}" itemLabel="dropDown" itemValue="code"/>
										</form:select>
	    							</span>
								</div> --%>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="username">
										<spring:message code="usermgmt.user.username" />
									</label> 
									<span class="col-sm-3">
										<form:input path="user.username" class="form-control input-sm" id="username" readonly="true"/>
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="password">
										<spring:message code="usermgmt.user.password" />
									</label>
									<span class="col-sm-3">
										<form:password path="user.password" class="form-control input-sm" showPassword="true" id="password" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="confirm_password">
										<spring:message code="usermgmt.user.confirm_password" />
									</label>
									<span class="col-sm-3">
										<form:password path="user.confirmPassword" class="form-control input-sm" showPassword="true" id="confirm_password" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="password_expire">
										<spring:message code="usermgmt.user.password_expire" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-2">
										<form:input path="user.pwdExpiryDays" class="form-control input-sm" id="password_expire" />
									</span>
									<span class="control-label col-sm-1" style="text-align: left">
										<spring:message code="usermgmt.user.days" />
									</span>
									<label class="control-label col-sm-4" for="force_change_password_flag">
										<spring:message code="usermgmt.user.force_change_password_flag" />
									</label>
									<span class="col-sm-1"> 
										<form:checkbox path="user.forceChangePwdFlag" id="force_change_password_flag" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="session_timeout">
										<spring:message code="usermgmt.user.session_timeout" /><span class="mandatory">*</span>
									</label> 
									<span class="col-sm-2">
										<form:input path="user.sessionTimeoutMinutes" class="form-control input-sm" id="session_timeout" />
									</span>
									<span class="control-label col-sm-1" style="text-align: left">
										<spring:message code="usermgmt.user.minutes" />
									</span>
									<label class="control-label col-sm-4" for="active_flag">
										<spring:message code="usermgmt.user.active_flag" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-1">
										<form:checkbox path="user.activeFlag" id="active_flag" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="max_invalid_login">
										<spring:message code="usermgmt.user.max_invalid_login" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-2"> 
										<form:input path="user.maxInvalidLoginCount" class="form-control input-sm" id="max_invalid_login" />
									</span>
									<label class="control-label col-sm-5" for="connected_flag">
										<spring:message code="usermgmt.user.connected_flag" />
									</label>
									<span class="col-sm-1">
										<form:checkbox path="user.connectedFlag" id="connected_flag" />
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-3" for="invalid_login">
										<spring:message code="usermgmt.user.invalid_login" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-2"> 
										<form:input path="user.invalidLoginCount" class="form-control input-sm" id="invalid_login" />
									</span>
								</div>
							</div>
							<div id="tab-userRole" class="tab-pane fade panel-body">
								<form:checkboxes items="${userForm.roles}" itemValue="id" itemLabel="name" path="roleIds" cssClass="control-label" delimiter="<br/>" />
							</div>
						</div>
					</div>
					<div class="panel-footer" align="right">
						<input type="submit" class="btn btn-primary" value="<spring:message code="button.submit" />" /> 
						<a href="${backUrl}" class="btn btn-primary"><spring:message code="button.back" /></a>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>
