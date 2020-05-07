<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/setup/insurer" />
<spring:url var="saveUrl" value="/secured/setup/insurer/edit" />

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.insurer.editInsurer" /></div>
				<form:form commandName="insurerForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="code">
  							<spring:message code="setup.insurer.code" />
  						</label>
  						<span class="col-sm-2">
   							<form:input path="insurer.code" class="form-control input-sm" id="code" maxlength="5" readonly="true"/>
   						</span>
						<span class="control-label col-sm-1"></span>    							
  						<label class="control-label col-sm-2" for="companyNo">
  							<spring:message code="setup.insurer.companyNo" />
  						</label>
  						<span class="col-sm-4">
   							<form:input path="insurer.companyNo" class="form-control input-sm" id="companyNo"/>
   						</span>
						<span class="control-label col-sm-1"></span>    							
					</div>
					<div class="form-group row">
						<label class="control-label col-sm-2" for="name">
							<spring:message code="setup.insurer.name" /><span class="mandatory">*</span>
						</label>
						<span class="col-sm-9">
							<form:input path="insurer.name" class="form-control input-sm" id="name"/>
						</span>
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="contactPerson">
  							<spring:message code="setup.insurer.contactPerson" />
  						</label>
  						<span class="col-sm-4">
   							<form:input path="insurer.contact.contactPerson" class="form-control input-sm" id="contactPerson"/>
   						</span>
  						<label class="control-label col-sm-2" for="telNo">
  							<spring:message code="setup.insurer.telNo" />
  						</label>
  						<span class="col-sm-3">
   							<form:input path="insurer.contact.telNo" class="form-control input-sm" id="telNo"/>
   						</span>	
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="email">
  							<spring:message code="setup.insurer.email" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-4">
   							<form:input path="insurer.contact.email" class="form-control input-sm" id="email"/>
   						</span>	   						    							   									
  						<label class="control-label col-sm-2" for="faxNo">
  							<spring:message code="setup.insurer.faxNo" />
  						</label>
  						<span class="col-sm-3">
   							<form:input path="insurer.contact.faxNo" class="form-control input-sm" id="faxNo"/>
   						</span>					
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="address1">
  							<spring:message code="setup.insurer.address1" />
  						</label>
  						<span class="col-sm-5">
   							<form:input path="insurer.contact.address1" class="form-control input-sm" id="address1"/>
   						</span>	   						    							   									
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="address2">
  							<spring:message code="setup.insurer.address2" />
  						</label>
  						<span class="col-sm-5">
   							<form:input path="insurer.contact.address2" class="form-control input-sm" id="address2"/>
   						</span>	   						    							   									
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="address3">
  							<spring:message code="setup.insurer.address3" />
  						</label>
  						<span class="col-sm-5">
   							<form:input path="insurer.contact.address3" class="form-control input-sm" id="address3"/>
   						</span>	   						    							   									
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="city">
  							<spring:message code="setup.insurer.city" />
  						</label>
  						<span class="col-sm-4">
   							<form:input path="insurer.contact.city" class="form-control input-sm" id="city"/>
   						</span>	   				
   						<label class="control-label col-sm-2" for="postcode">
  							<spring:message code="setup.insurer.postcode" />
  						</label>
  						<span class="col-sm-2">
   							<form:input path="insurer.contact.postcode" class="form-control input-sm" id="postcode"/>
   						</span>					
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="state">
  							<spring:message code="setup.insurer.state" />
  						</label>
  						<span class="col-sm-4">
  							<form:select path="insurer.contact.state.code" class="form-control input-sm" id="state">
								<form:option label="Select" value="" />
								<form:options items="${insurerForm.states}" itemLabel="name" itemValue="code" />
							</form:select>
   						</span>
   						<label class="control-label col-sm-2" for="activeFlag">
   							<spring:message code="setup.insurer.active_flag" />
   						</label>
   						<span class="col-sm-2">
							<form:checkbox path="insurer.active" />
						</span>
   					</div>	   				
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="remark">
  							<spring:message code="setup.insurer.remark" />
  						</label>
  						<span class="col-sm-9">
   							<form:textarea path="insurer.remark" class="form-control input-sm" rows="6" id="remark"/>
   						</span>					
					</div>
				</div>
				<div class="panel-footer" align="right">
					<input type="submit" class="btn btn-primary" 
						value="<spring:message code="button.submit" />" />
					 <a href="${backUrl}" class="btn btn-primary"><spring:message code="button.back" /></a>
				</div>										
				</form:form>
			</div>
		</div>
	</div>				
</div>

