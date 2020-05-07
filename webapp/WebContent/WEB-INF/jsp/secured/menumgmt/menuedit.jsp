<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/menumgmt/menu" />
<spring:url var="saveUrl" value="/secured/menumgmt/menu/edit" />

<div class="container-fluid">
	<div class="row">
		<div class="col-sm-1"></div>
		<div class="panel-group col-sm-10">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="menumgmt.menusetup.editMenu" />
				</div>
				<form:form commandName="menuForm" method="post" action="${saveUrl}"
					role="form" class="form-horizontal">
					<form:errors element="div" cssClass="errorblock" path="*" />
					<form:hidden path="menuItem.id" />
					<div class="panel-body">
						<div class="form-group row">
							<label class="control-label col-sm-3" for="name"><spring:message
									code="label.name" /></label> <span class="col-sm-8"> <form:input
									path="menuItem.name" class="form-control input-sm"
									id="menuCode" />
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-3" for="description"><spring:message
									code="label.description" /></label> <span class="col-sm-8"> <form:input
									path="menuItem.description" class="form-control input-sm"
									id="description" />
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-3" for="sortOrder"><spring:message
									code="menumgmt.menusetup.function" /></label> <span class="col-sm-8">
								<form:select path="menuItem.function.code">
									<form:option label="Select" value="" />
									<form:options items="${menuForm.functions}" itemLabel="name"
										itemValue="code" />
								</form:select>
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-3" for="parentFlag"><spring:message
									code="menumgmt.menusetup.parent" /></label> <span class="col-sm-8">
								<form:select path="parentId">
									<form:option label="Select" value="" />
									<form:options items="${menuForm.menus}" itemLabel="name"
										itemValue="id" />
								</form:select>
							</span>
						</div>
						<div class="form-group row">
							<label class="control-label col-sm-3" for="parentFlag"><spring:message
									code="menumgmt.menusetup.parentFlag" /></label> <span class="col-sm-2">
								<form:checkbox path="menuItem.parentFlag" />
							</span> <label class="control-label col-sm-3" for="sortOrder"><spring:message
									code="label.sortOrder" /></label> <span class="col-sm-2"> <form:input
									path="menuItem.sortOrder" class="form-control input-sm"
									id="sortOrder" />
							</span>
						</div>

						<!-- button -->
						<div class="panel-footer" align="right">
							<input type="submit" class="btn btn-primary"
								value="<spring:message code="button.submit" />" /> <a
								href="${backUrl}" class="btn btn-primary"><spring:message
									code="button.back" /></a>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>