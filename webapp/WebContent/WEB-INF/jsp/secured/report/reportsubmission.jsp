<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="searchUrl" value="/secured/report/submission" />
<spring:url var="downloadStatusUrl" value="/secured/report/common/download/status" />
<spring:url var="downloadFileUrl" value="/secured/report/common/download/file" />
<spring:url var="editUrl" value="/secured/report/submission/edit" />
<spring:url var="refreshReportUrl" value="/secured/report/common/refresh/reportdefinition"/>

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		$('#requestedDateFrom').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#requestedDateTo').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
	});	    


	function searchViaAjax() {
		var search = {}
		search["categoryCode"] = $("#categoryCode").val();
		enableCategoryDropdown(false);
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "${refreshReportUrl}",
			data : JSON.stringify(search),
			dataType : 'json',
			timeout : 100000,
			success : function(data) {
				console.log("SUCCESS: ", data);
				displayReportList(data);
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
				enableCategoryDropdown(true);
			}
		});
	}

	function displayReportList(data) {
		var options = [];
		$('#report').html('');  // Set the Dropdown as Blank before new Data
		options.push('<option value="">Select</option>');		
		if(data.definitions != null) {
			$.each(data.definitions, function(i, item) {
					options.push($('<option/>', 
					{
						value: item.id, text: item.name 
					}));
				});
		}
		$('#report').append(options);  // Set the Values to Dropdown
	}
	
	function enableCategoryDropdown(flag) {		
		 $("#categoryCode").prop("disabled", flag);
	}

	function showDownloadDialog(submissionId) {
		var search = {}
		search["submissionId"] = submissionId;
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "${downloadStatusUrl}",
			data : JSON.stringify(search),
			dataType : 'json',
			timeout : 100000,
			success : function(data) {
				console.log("SUCCESS: ", data);
				$('#dStatus').empty();
				$('#dLogFile').empty();
				$('#dReportFile').empty();

				if(data.status != null) {
					$('#dStatus').val(data.status);
				}
				if(data.reportFile != null) {
					$('#dReportFile').append("<a target='_blank' href='${downloadFileUrl}/${url_param_prefix}/" + data.submissionId + "/" + data.reportFile + "/'>" + data.reportFile + "</a>");
				}
				if(data.logFile != null) {
					$('#dLogFile').append("<a target='_blank' href='${downloadFileUrl}/${url_param_prefix}/" + data.submissionId + "/" + data.logFile + "/'>" + data.logFile + "</a>");
				}				
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
			}
		});
		return false;
	}
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="report.submission.reportsubmission_listing" /></div>
				<form:errors element="div" cssClass="errorblock" path="*" />
				<form:form commandName="reportSubmissionSearchForm" method="post" action="${searchUrl}" role="form" class="form-horizontal">
				<div class="panel-body">
					<div class="form-group row">
	  					<label class="control-label col-sm-2" for="status">
		  					<spring:message code="report.status" />
		  				</label>
	  					<span class="col-sm-3">
  							<form:select path="criteria.status" class="form-control input-sm" id="status">
								<form:option label="Select" value="" />
								<form:options items="${reportSubmissionSearchForm.statuses}" itemLabel="label" />
							</form:select>
	  					</span>
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="requestedDateFrom">
		  					<spring:message code="report.requestedDate" />
		  				</label>
		  				<span class="col-sm-3">
							<form:input path="criteria.requestedDateFrom" class="form-control input-sm" id="requestedDateFrom"/>
		   				</span>
		  				<label class="control-label col-sm-1" for="requestedDateTo">
		  					<spring:message code="report.submission.to" />
		  				</label>
		  				<span class="col-sm-3">
							<form:input path="criteria.requestedDateTo" class="form-control input-sm" id="requestedDateTo"/>
		   				</span>
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="categoryCode">
		  					<spring:message code="report.category" />
		  				</label>
	  					<span class="col-sm-3">
  							<form:select path="criteria.categoryCode" class="form-control input-sm" id="categoryCode" onchange="searchViaAjax();">
								<form:option label="Select" value="" />
								<form:options items="${reportSubmissionSearchForm.categoryList}" itemLabel="name" itemValue="code" />
							</form:select>
	  					</span>	
		   			</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="report">
  							<spring:message code="report.reportName" />
  						</label>   						
  						<span class="col-sm-9">
  							<form:select path="criteria.definitionId" class="form-control input-sm" id="report">
								<form:option label="Select" value="" />
								<form:options items="${reportSubmissionSearchForm.definitionList}" itemLabel="name" itemValue="id" />
							</form:select>
   						</span>
					</div>		   			
		   		</div>		
				<div class="panel-footer" align="right">
					<form:button name="action" value="" class="btn btn-primary"><spring:message code="button.search" /></form:button>
					<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>						
				</div>		   				
				</form:form>
			</div>
		</div>
	</div>	
	<c:if test="${reportSubmissionSearchForm.searched}">
		<div class="row">
		    <div class="col-sm-1"></div>
		  	<div class="panel-group col-sm-10">
			    <div class="panel panel-primary">
					<div class="panel-body">
						<datatables:table id="myTableId" data="${reportSubmissionSearchForm.submissions}" rowIdBase="id" cssClass="table table-striped" row="row" ext="responsive" autoWidth="false" filterable="false">						
							<datatables:column titleKey="report.submission.id" cssStyle="padding:10px 10px; width:12%" sortInitOrder="2">
								<a href="#" onclick="showDownloadDialog(${row.id});" data-toggle="modal" data-target="#downloadDialog" style="color: blueviolet; font-weight: bold;"/><c:out value="${row.id}" /></a>
							</datatables:column>
							<datatables:column property="category" titleKey="report.category" cssStyle="padding:10px 10px; width:10%" sortInitOrder="3"/>
							<datatables:column property="reportName" titleKey="report.reportName" cssStyle="padding:10px 10px" sortInitOrder="4"/>
							<datatables:column property="status" titleKey="report.status" cssStyle="padding:10px 10px; width:8%" sortInitOrder="5"/>
							<datatables:column property="format" titleKey="report.format" cssStyle="padding:10px 10px; width:8%" sortInitOrder="6"/>
							<datatables:column property="requestedBy" titleKey="report.requestedBy" cssStyle="padding:10px 10px; width:12%" sortInitOrder="7"/>
							<datatables:column property="requestedDate" titleKey="report.requestedDate" cssStyle="padding:10px 10px; width:15%" sortInitOrder="1" format="{0,date,yyyy.MM.dd HH:mm}" sortInitDirection="desc"/>				
							<datatables:column property="endDate" titleKey="report.endDate" cssStyle="padding:10px 10px; width:15%" sortInitOrder="8" format="{0,date,yyyy.MM.dd HH:mm}"/>
						</datatables:table>
					</div>
				</div>
			</div>
		</div>
	</c:if>
</div>