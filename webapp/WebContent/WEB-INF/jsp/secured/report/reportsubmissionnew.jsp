<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="newUrl" value="/secured/report/submission/new" />

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
	});	    

	function submitOnCategoryChange() {
		$('#report').val('');
		if($("input[name='submission.outputFormat.id.format']").length) {
			$("input[name='submission.outputFormat.id.format']").val('');
		}
		$('#reportSubmissionForm').submit();
	}

	function submitOnDefinitionChange() {
		if($("input[name='submission.outputFormat.id.format']").length) {
			$("input[name='submission.outputFormat.id.format']").val('');
		}
		$('#reportSubmissionForm').submit();
	}	
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="report.submission.newSubmission" /></div>
		    	<form:form commandName="reportSubmissionForm" method="post" action="${newUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="category">
		  					<spring:message code="report.category" />
		  				</label>
	  					<span class="col-sm-3">
  							<form:select path="submission.outputFormat.definition.category.code" class="form-control input-sm" id="category" onchange="submitOnCategoryChange();">
								<form:option label="Select" value="" />
								<form:options items="${reportSubmissionForm.categoryList}" itemLabel="name" itemValue="code" />
							</form:select>
	  					</span>	
		   			</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="report">
  							<spring:message code="report.reportName" /><span class="mandatory">*</span>
  						</label>   						
  						<span class="col-sm-9 ">
  							<form:select path="submission.outputFormat.id.definitionId" class="form-control input-sm" id="report" onchange="submitOnDefinitionChange();">
								<form:option label="Select" value="" />
								<form:options items="${reportSubmissionForm.definitionList}" itemLabel="name" itemValue="id" />
							</form:select>
   						</span>
					</div>
					<c:if test="${reportSubmissionForm.submission.outputFormat.id.definitionId != null && empty errors}">
						<div class="form-group row">
	  						<label class="control-label col-sm-2" for="outputFormat">
	  							<spring:message code="report.submission.outputFormat" /><span class="mandatory">*</span>
	  						</label>   						
	  						<span class="col-sm-9 btn-group">
	  							<form:radiobuttons path="submission.outputFormat.id.format" items="${reportSubmissionForm.formats}" id="outputFormat" cssClass="btn" element="label class='radio-inline'"/>
	   						</span>
						</div>
						<jsp:include page="${reportSubmissionForm.submission.outputFormat.definition.jspPath}"></jsp:include>
					</c:if>
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
