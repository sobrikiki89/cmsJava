<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		$('#fromNotifyDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#toNotifyDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
	});	   
</script>

<div class="form-group row">
	<label class="control-label col-sm-2" for="fromNotifyDate">
		<spring:message code="report.submission.overallclaimstat.fromNotifyDate" />
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['fromNotifyDate']" class="form-control input-sm" id="fromNotifyDate"/>
	</span>
	<span class="col-sm-1"></span>
	<label class="control-label col-sm-2" for="toNotifyDate">
		<spring:message code="report.submission.overallclaimstat.toNotifyDate" />
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['toNotifyDate']" class="form-control input-sm" id="toNotifyDate"/>
	</span>
</div>
<div class="form-group row">
	<label class="control-label col-sm-2" for="insuranceClass">
		<spring:message code="report.submission.overallclaimstat.insuranceClass" />
	</label>
	<span class="col-sm-6">
		<form:select path="handler.paramMap['insuranceClass']" class="form-control input-sm" id="insuranceClass">
			<form:option label="Select" value="" />
			<form:options items="${reportSubmissionForm.handler.lookupMap['insuranceClasses']}" itemLabel="dropdownLabel" itemValue="code" />
		</form:select>
	</span>
</div>
<c:if test="${reportSubmissionForm.allowedFlag}">	
	<div class="form-group row">
		<label class="control-label col-sm-2" for="companies">
			<spring:message code="report.submission.overallclaimstat.companies" />
		</label>
		<span class="col-sm-9">
			<form:select path="handler.paramMap['companies']" class="form-control input-sm" id="companies">
				<form:options items="${reportSubmissionForm.handler.lookupMap['companies']}" itemLabel="name" itemValue="id" />
			</form:select>
		</span>
	</div>
</c:if>


