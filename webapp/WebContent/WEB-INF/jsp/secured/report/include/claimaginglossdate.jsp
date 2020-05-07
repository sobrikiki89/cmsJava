<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		$('#asOfDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false
		});
	});	   
</script>

<div class="form-group row">
	<label class="control-label col-sm-2" for="asOfDate">
		<spring:message code="report.submission.claimaging.asOfDate" />
	</label>
	<span class="col-sm-3">
		<form:input path="handler.paramMap['asOfDate']" class="form-control input-sm" id="asOfDate"/>
	</span>	
</div>
<c:if test="${reportSubmissionForm.allowedFlag}">	
	<div class="form-group row">
		<label class="control-label col-sm-2" for="companies">
			<spring:message code="report.submission.claimaging.companies" />
		</label>
		<span class="col-sm-9">
			<form:select path="handler.paramMap['companies']" class="form-control input-sm" id="companies" multiple="multiple">
				<form:options items="${reportSubmissionForm.handler.lookupMap['companies']}" itemLabel="name" itemValue="id" />
			</form:select>
		</span>
	</div>
</c:if>


