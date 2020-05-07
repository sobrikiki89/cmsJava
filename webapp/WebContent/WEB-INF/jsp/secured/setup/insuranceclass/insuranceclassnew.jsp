<%@ include file="../../../layouts/commontags.jsp"%>
<spring:url var="backUrl" value="/secured/setup/insuranceclass" />
<spring:url var="saveUrl" value="/secured/setup/insuranceclass/new" />

<script type="text/javascript">
$(document).ready(function(){
	  $("#sortOrder").keypress(function(e){
	    var keyCode = e.which;
	    /*
	    8 - (backspace)
	    32 - (space)
	    48-57 - (0-9)Numbers
	    */
	    if ( (keyCode != 8 || keyCode ==32 ) && (keyCode < 48 || keyCode > 57)) { 
	      return false;
	    }
	  });


	  $("#code").keypress(function(e){
	    var keyCode = e.which;
	    /* 
	    48-57 - (0-9) Numbers
	    65-90 - (A-Z)
	    97-122 - (a-z)
	    8 - (backspace)
	    32 - (space)
	    */
	    // Not allow special 
	    if ( !( (keyCode >= 48 && keyCode <= 57) 
	      ||(keyCode >= 65 && keyCode <= 90) 
	      || (keyCode >= 97 && keyCode <= 122) ) 
	      && keyCode != 8 && keyCode != 32) {
	      e.preventDefault();
	    }
	  });
	});  
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.insuranceclass.newInsuranceClass" /></div>
				<form:form commandName="insuranceClassForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="code">
  							<spring:message code="setup.insuranceclass.code" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-2">
   							<form:input path="insuranceClass.code" class="form-control input-sm" id="code" 
   								style="width:100%; text-transform: uppercase" maxlength="5"/>
   						</span>
  						<label class="control-label col-sm-2" for="code">
  							<spring:message code="setup.insuranceclass.category" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-4">
  							<form:select path="insuranceClass.category.name" class="form-control input-sm" id="category" >
								<form:option label="Select" value="" />
								<form:options items="${insuranceClassForm.categories}" itemLabel="dropdownLabel" itemValue="code"/>
							</form:select>
   						</span>
					</div>
					<div class="form-group row">
						<label class="control-label col-sm-2" for="name">
							<spring:message code="setup.insuranceclass.name" /><span class="mandatory">*</span>
						</label>
						<span class="col-sm-8">
							<form:input path="insuranceClass.name" class="form-control input-sm" id="name"/>
						</span>
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="sortOrder">
  							<spring:message code="setup.insuranceclass.sortOrder" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-2">
   							<form:input path="insuranceClass.sortOrder" class="form-control input-sm" id="sortOrder"/>
   						</span>
   						<label class="control-label col-sm-2" for="activeFlag">
   							<spring:message code="setup.insuranceclass.active_flag" />
   						</label>
   						<span class="col-sm-2">
							<form:checkbox path="insuranceClass.activeFlag" id="activeFlag" />
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

