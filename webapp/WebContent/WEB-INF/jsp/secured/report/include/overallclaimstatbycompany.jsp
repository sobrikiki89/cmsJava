<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		$('#fromLossDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#toLossDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
	});	   
</script>

<div class="form-group row">
	<label class="control-label col-sm-2" for="fromLossDate">
		<spring:message code="report.submission.overallclaimstat.fromLossDate" />
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['fromLossDate']" class="form-control input-sm" id="fromLossDate"/>
	</span>
	<span class="col-sm-1"></span>
	<label class="control-label col-sm-2" for="toLossDate">
		<spring:message code="report.submission.overallclaimstat.toLossDate" />
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['toLossDate']" class="form-control input-sm" id="toLossDate"/>
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
			<form:select path="handler.paramMap['companies']" class="form-control input-sm" id="companies" multiple="multiple">
				<form:options items="${reportSubmissionForm.handler.lookupMap['companies']}" itemLabel="name" itemValue="id" />
			</form:select>
		</span>
	</div>
</c:if>

