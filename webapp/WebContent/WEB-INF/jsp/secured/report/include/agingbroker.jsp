<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	// When the document is ready
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
	});	   
</script>
<c:if test="${reportSubmissionForm.allowedFlag}">	
<div class="form-group row">
	<label class="control-label col-sm-2" for="companies">
		<spring:message code="report.submission.claimbordereaux.companies" />
	</label>
	<span class="col-sm-9">
		<form:select path="handler.paramMap['companies']" class="form-control input-sm" id="companies" multiple="multiple">
			<form:options items="${reportSubmissionForm.handler.lookupMap['companies']}" itemLabel="name" itemValue="id" />
		</form:select>
	</span>
</div>
</c:if>
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
