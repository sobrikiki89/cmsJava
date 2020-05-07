<%@ include file="../../../layouts/commontags.jsp"%>
<spring:url var="insuranceClassUrl" value="/secured/report/submission/lookup/insuranceClass" />

<script type="text/javascript">
	$(document).ready(function () {
		$('#fromDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#toDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$("#groupOfInsurance").on("change", function(){
			var vData = $(this).val();		
			if(vData.length > 1) {
				getAndSetClassOfInsurance(vData);
			} else {
				getAndSetClassOfInsurance("all");
			}	
		});
	});	   
	
	function getAndSetClassOfInsurance(code){
		$.ajax({
			type: "GET",
		    url: "${insuranceClassUrl}" + "/" + "${url_param_prefix}" + "/" + code,
		    success: function(data) {
		    	$('#insuranceClass').empty();
		    	var dataLen = data.length;
		    	for (i = 0; i < dataLen; i++) {
	            	$('#insuranceClass').append('<option value="' + data[i].code + '">' + data[i].dropdownLabel + '</option>');
	            }
		    }
		});
	}
</script>

<c:if test="${reportSubmissionForm.allowedFlag}">	
<div class="form-group row">
	<label class="control-label col-sm-2" for="companyId">
		<spring:message code="report.submission.policylisting.company" />
	</label>
	<span class="col-sm-9">
		<form:select path="handler.paramMap['companyId']" class="form-control input-sm" id="companyId">
			<form:option label="Select" value="" />
			<form:options items="${reportSubmissionForm.handler.lookupMap['companies']}" itemLabel="name" itemValue="id" />
		</form:select>
	</span>
</div>
</c:if>
<div class="form-group row">
	<label class="control-label col-sm-2" for="insurer">
		<spring:message code="report.submission.policylisting.insurer" />
	</label>
	<span class="col-sm-9">
		<form:select path="handler.paramMap['insurerCode']" class="form-control input-sm" id="insurer">
			<form:option label="Select" value="" />
			<form:options items="${reportSubmissionForm.handler.lookupMap['insurers']}" itemLabel="dropdownLabel" itemValue="code" />
		</form:select>
	</span>
</div>

<div class="form-group row">
	<label class="control-label col-sm-2" for="groupOfInsurance">
		<spring:message code="report.submission.policylisting.groupOfInsurance" />
	</label>
	<span class="col-sm-9">
		<form:select path="handler.paramMap['groupOfInsuranceCode']" class="form-control input-sm" id="groupOfInsurance">
			<form:option label="Select" value="" />
			<form:options items="${reportSubmissionForm.handler.lookupMap['groupOfInsurance']}" itemLabel="dropdownLabel" itemValue="code" />
		</form:select>
	</span>
</div>
<div class="form-group row">
	<label class="control-label col-sm-2" for="insuranceClass">
		<spring:message code="report.submission.policylisting.insuranceClass" />
	</label>
	<span class="col-sm-9">
		<form:select path="handler.paramMap['insuranceClassCode']" class="form-control input-sm" id="insuranceClass" multiple="multiple">
			<form:options items="${reportSubmissionForm.handler.lookupMap['insuranceClasses']}" itemLabel="dropdownLabel" itemValue="code" />
		</form:select>
	</span>
</div>

<div class="form-group row">
	<label class="control-label col-sm-2" for="fromDate">
		<spring:message code="label.fromDate" arguments="Start"/>
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['fromDate']" class="form-control input-sm" id="fromDate"/>
	</span>
	<span class="col-sm-1"></span>
	<label class="control-label col-sm-2" for="toDate">
		<spring:message code="label.toDate" arguments="Start"/>
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['toDate']" class="form-control input-sm" id="toDate"/>
	</span>
</div>