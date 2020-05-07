<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/usermgmt/rolefunction" />
<spring:url var="saveUrl" value="/secured/usermgmt/rolefunction/edit" />


<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    <div class="panel-heading"><spring:message code="usermgmt.rolefunction.tab.role_func" /></div>
				<form:form commandName="roleFunctionForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">			
						<span class="col-sm-1">&nbsp;</span>
  						<label class="control-label col-sm-1" for="role">
  							<spring:message code="label.name" />
  						</label>
  						<span class="col-sm-2">
							<form:select path="roleFunction.role.id" id="role">
								<form:options items="${roleFunctionForm.roles}" itemLabel="name" itemValue="id"/>
							</form:select>
						</span>
  						<label class="control-label col-sm-2" for="function">												
							<spring:message code="usermgmt.rolefunction.function" />
						</label>
  						<span class="col-sm-6">						
							<form:select path="roleFunction.function.code">
								<form:options items="${roleFunctionForm.functions}" itemLabel="name" itemValue="code" />
							</form:select>
						</span>						
					</div>
					<%-- Comment on 18/09/2017 
					<div class="form-group row">
						<span class="col-sm-1">&nbsp;</span>	
  						<span class="col-sm-2">						
							<form:checkbox path="roleFunction.readable" id="readable"/>
							<spring:message code="usermgmt.rolefunction.readable" />
						</span>
  						<span class="col-sm-2">												
							<form:checkbox path="roleFunction.createable" id="creatable"/>
							<spring:message code="usermgmt.rolefunction.creatable" />
						</span>
  						<span class="col-sm-2">												
							<form:checkbox path="roleFunction.updateable" id="updateable"/>
							<spring:message code="usermgmt.rolefunction.updatable" />
						</span>
  						<span class="col-sm-2">												
							<form:checkbox path="roleFunction.deleteable"/>
							<spring:message code="usermgmt.rolefunction.deletable" />
						</span>					
					</div> --%>
							
				</div>				
				<!-- button -->	
				<div class="panel-footer" align="right">
					<input type="submit" class="btn btn-primary" value="<spring:message code="button.submit" />" />
					<a href="${backUrl}" class="btn btn-primary"><spring:message code="button.back" /></a>
				</div>
				</form:form>
		    </div>
		</div>    
	</div>
</div>