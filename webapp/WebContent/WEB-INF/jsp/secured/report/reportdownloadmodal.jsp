<%@ include file="../../layouts/commontags.jsp"%>
<spring:url var="searchUrl" value="/secured/report/submission" />

<form:form commandName="reportSubmissionDownloadForm" method="post" action="${searchUrl}" role="form" class="form-horizontal">			
	<div id="downloadDialog" class="modal fade">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4>
						<spring:message code="report.downloadReport" />
					</h4>
				</div>

				<div class="modal-body">
					<div class="panel-body">
						<div class="form-group row">
							<label class="control-label col-sm-2" for="dStatus"> <spring:message
									code="report.status" />
							</label> <span class="col-sm-4"> <input
								class="form-control input-sm" id="dStatus" disabled="disabled" />
							</span>
						</div>

						<div class="form-group row">
							<label class="control-label col-sm-2" for="dReportFile">
								<spring:message code="report.reportFile" />
							</label> <span class="col-sm-4" id="dReportFile"></span>
						</div>

						<div class="form-group row">
							<label class="control-label col-sm-2" for="dLogFile"> <spring:message
									code="report.logFile" />
							</label> <span class="col-sm-4" id="dLogFile"></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</form:form>