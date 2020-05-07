<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/setup/losstype" />
<spring:url var="saveUrl" value="/secured/setup/losstype/new" />

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.losstype.newLossType" /></div>
				<form:form commandName="lossTypeForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="code">
  							<spring:message code="setup.losstype.code" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-2">
   							<form:input path="lossType.code" class="form-control input-sm" id="code" style="width:100%; text-transform: uppercase"
   							 maxlength="5"/>
   						</span>
					</div>
					<div class="form-group row">
						<label class="control-label col-sm-2" for="name">
							<spring:message code="setup.losstype.name" /><span class="mandatory">*</span>
						</label>
						<span class="col-sm-9">
							<form:input path="lossType.name" class="form-control input-sm" id="name"/>
						</span>
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="sortOrder">
  							<spring:message code="setup.losstype.sortOrder" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-2">
   							<form:input path="lossType.sortOrder" class="form-control input-sm" id="sortOrder"/>
   						</span>
   						<label class="control-label col-sm-2" for="activeFlag">
   							<spring:message code="setup.losstype.active_flag" />
   						</label>
   						<span class="col-sm-2">
							<form:checkbox path="lossType.activeFlag" id="activeFlag" />
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

