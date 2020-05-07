<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="postUrl" value="/secured/setup/solicitor/new" />

<script type="text/javascript">
	$(function() {  
	    $("textarea[maxlength]").bind('input propertychange', function() {  
	        var maxLength = $(this).attr('maxlength');  
	        if ($(this).val().length > maxLength) {  
	            $(this).val($(this).val().substring(0, maxLength));  
	        }  
	    })
	    
	    $('#telNo').keydown(function (e) {
            var k = String.fromCharCode(e.which);
            if (k.match(/[^0-9\x08]/g))
              e.preventDefault();
       	});
        
        $('#faxNo').keydown(function (e) {
            var k = String.fromCharCode(e.which);
            if (k.match(/[^0-9\-\x08]/g))
              e.preventDefault();
       	});
	});
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.solicitor.new" /></div>
				<form:form commandName="solicitorForm" method="post" action="${postUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
						<label class="control-label col-sm-2" for="firmName">
							<spring:message code="setup.solicitor.firmName" /><span class="mandatory">*</span>
						</label>
						<span class="col-sm-9">
							<form:input path="solicitorDTO.firmName" class="form-control input-sm" id="firmName"/>
						</span>
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="email">
  							<spring:message code="setup.contact.email" />
  						</label>
  						<span class="col-sm-9">
   							<form:input path="contactDTO.email" class="form-control input-sm" id="email"/>
   						</span>	   		
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="telNo">
  							<spring:message code="setup.contact.telNo" />
  						</label>
  						<span class="col-sm-4">
   							<form:input path="contactDTO.telNo" class="form-control input-sm" id="telNo"/>
   						</span>					    							   									
  						<label class="control-label col-sm-2" for="faxNo">
  							<spring:message code="setup.contact.faxNo" />
  						</label>
  						<span class="col-sm-3">
   							<form:input path="contactDTO.faxNo" class="form-control input-sm" id="faxNo"/>
   						</span>					
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="address1">
  							<spring:message code="setup.contact.address1" />
  						</label>
  						<span class="col-sm-5">
   							<form:input path="contactDTO.address1" class="form-control input-sm" id="address1"/>
   						</span>	   						    							   									
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="address2">
  							<spring:message code="setup.contact.address2" />
  						</label>
  						<span class="col-sm-5">
   							<form:input path="contactDTO.address2" class="form-control input-sm" id="address2"/>
   						</span>	   						    							   									
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="address3">
  							<spring:message code="setup.contact.address3" />
  						</label>
  						<span class="col-sm-5">
   							<form:input path="contactDTO.address3" class="form-control input-sm" id="address3"/>
   						</span>	   						    							   									
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="city">
  							<spring:message code="setup.contact.city" />
  						</label>
  						<span class="col-sm-4">
   							<form:input path="contactDTO.city" class="form-control input-sm" id="city"/>
   						</span>	   				
   						<label class="control-label col-sm-2" for="postcode">
  							<spring:message code="setup.contact.postcode" />
  						</label>
  						<span class="col-sm-2">
   							<form:input path="contactDTO.postcode" class="form-control input-sm" id="postcode"/>
   						</span>					
					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="state">
  							<spring:message code="setup.contact.state" />
  						</label>
  						<span class="col-sm-4">
  							<form:select path="contactDTO.stateCode" class="form-control input-sm" id="state">
								<form:option label="Select" value="" />
								<form:options items="${solicitorForm.states}" itemLabel="name" itemValue="code" />
							</form:select>
   						</span>
   						<label class="control-label col-sm-2" for="activeFlag">
   							<spring:message code="setup.contact.activeFlag" />
   						</label>
   						<span class="col-sm-2">
							<form:checkbox path="solicitorDTO.activeFlag" />
						</span>
   					</div>	   				
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="remark">
  							<spring:message code="setup.contact.remark" />
  						</label>
  						<span class="col-sm-9">
   							<form:textarea path="solicitorDTO.remark" class="form-control input-sm" rows="6" id="remark" maxlength='254'/>
   						</span>					
					</div>
				</div>
				<div class="panel-footer" align="right">
					<form:button name="action" value="submit" class="btn btn-primary"><spring:message code="button.submit" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>
				</div>										
				</form:form>
			</div>
		</div>
	</div>				
</div>