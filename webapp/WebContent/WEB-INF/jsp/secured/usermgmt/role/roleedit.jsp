<%@ include file="../../../layouts/commontags.jsp"%>
<spring:url var="backUrl" value="/secured/usermgmt/role" />
<spring:url var="saveUrl" value="/secured/usermgmt/role/edit" />

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    <div class="panel-heading"><spring:message code="usermgmt.user.role" /></div>
				<form:form commandName="roleForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
	    				<label class="control-label col-sm-3" for="name">
	    					<spring:message code="label.name" />
	    					<span class="mandatory">*</span>
	    				</label>
	    				<span class="col-sm-8">
		    				<form:input path="role.name" disabled="true" class="form-control input-sm" id="roleName"/>
		    			</span>
		    		</div>
  					<div class="form-group row">
	    				<label class="control-label col-sm-3" for="description">
	    					<spring:message code="label.description" />
	    					<span class="mandatory">*</span>
	    				</label>
	    				<span class="col-sm-5">
		    				<form:input path="role.description" class="form-control input-sm" id="description"/>
		    			</span>
		    		</div>
  					<div class="form-group row">
	    				<label class="control-label col-sm-3" for="sortOrder">
	    					<spring:message code="label.sortOrder" />
	    					<span class="mandatory">*</span>
	    				</label>
	    				<span class="col-sm-2">
		    				<form:input path="role.sortOrder" class="form-control input-sm" id="sortOrder"/>
		    			</span>
  					</div>  						
					<!-- button -->	
					<div class="panel-footer" align="right">
						<input type="submit" class="btn btn-primary" value="<spring:message code="button.submit" />" />
						<a href="${backUrl}" class="btn btn-primary"><spring:message code="button.back" /></a>
					</div>
				</div>
				</form:form>
		    </div>
		</div>    
	</div>
</div>