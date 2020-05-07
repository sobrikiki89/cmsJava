<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="searchUrl" value="/secured/claim" />
<spring:url var="editUrl" value="/secured/claim/edit" />
<spring:url var="reqDeleteUrl" value="/secured/claim/reqDelete" />
<spring:url var="reqRevertUrl" value="/secured/claim/reqRevert" />
<spring:url var="deleteUrl" value="/secured/claim/delete" />
<spring:url var="revertUrl" value="/secured/claim/revert" />

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		$('#startDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#endDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
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

	<c:if test="${claimSearchForm.hasDeletePermission}">	
		function deleteConfirm(claimId) {
			$.ajax({
				type: "GET",
				url: "${reqDeleteUrl}" + "/" + "${url_param_prefix}" + "/" + claimId,
				data: $("#claimDeleteForm").serialize(),
				success: function(data) {	
					if(data != null) {
						$('#confirmationNote').text(data.note);
						//jQuery.noConflict();
						$('#claimDeleteForm.errors').remove();				
						$('#confirmDelete').show();
						$('#confirmRevert').hide();
						$('#confirmationDialog').modal('show');
					} else {
						alert("Error in getting data");
					}
				},
			    error: function (textStatus, errorThrown) {
			    	$('#notificationNote').text('<spring:message code="claim.delete.reqError" />');
			    }
			});
		}

		function doDelete() {
		    $.ajax({
		    	url: "${deleteUrl}",
		        type: 'POST',
		        data: $("#claimDeleteForm").serialize(),
		        success: function (response) {
		        var status = response;
		            $('#confirmDelete').hide();
					$('#confirmationNote').text(status);
					$("#claimSearchForm").submit();
		        },
		         error: function (response) {
		            console.log('fail');
		        },
		    });
		}
	</c:if>

	<c:if test="${claimSearchForm.hasRevertPermission}">	
		function revertConfirm(claimId) {
			$.ajax({
				type: "GET",
				url: "${reqRevertUrl}" + "/" + "${url_param_prefix}" + "/" + claimId,
				data: $("#claimDeleteForm").serialize(),
				success: function(data) {	
					if(data != null) {
						$('#confirmationNote').text(data.note);
						$('#claimDeleteForm.errors').remove();
						$('#confirmDelete').hide();
						$('#confirmRevert').show();
						$('#confirmationDialog').modal('show');
					} else {
						alert("Error in getting data");
					}
				},
			    error: function (textStatus, errorThrown) {
			    	$('#notificationNote').text('<spring:message code="claim.revert.reqError" />');
			    }
			});
		}
		
	
		function doRevert() {
		    $.ajax({
		    	url: "${revertUrl}",
		        type: 'POST',
		        data: $("#claimDeleteForm").serialize(),
		        success: function (response) {
		        var status = response;
		        	$('#confirmRevert').hide();
					$('#confirmationNote').text(status);
					$("#claimSearchForm").submit();        
		        },
		         error: function (response) {
		            console.log('fail');
		        },
		    });
		}
	</c:if>
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-12">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="claim.claim_listing" /></div>
				<form:form commandName="claimSearchForm" method="post" action="${searchUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
						<c:if test="${!claimSearchForm.searched}">
							<div class="alert alert-info">
								<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
								<strong><spring:message code="claim.tip" /></strong> 
								<spring:message code="claim.contractorTip" />
								<spring:message code="claim.userCompanyTip" />
	  						</div>
  						</c:if>
		  				<label class="control-label col-sm-2" for="company">
		  					<spring:message code="claim.company" />
		  				</label>
	  					<span class="col-sm-9">
	  						<form:select path="criteria.companyId" class="form-control input-sm" id="company">
								<form:option label="Select" value="" />
								<form:options items="${claimSearchForm.companies}" itemLabel="name" itemValue="id"/>
							</form:select>
	  					</span>	  					
		   			</div>
		   			<div class="form-group row">
		  				<label class="control-label col-sm-2" for="insuranceClass">
		  					<spring:message code="claim.insuranceClass" />
		  				</label>		   			
		  				<span class="col-sm-5">
  							<form:select path="criteria.insuranceClassCode" class="form-control input-sm" id="insuranceClass">
								<form:option label="Select" value="" />
								<form:options items="${claimSearchForm.insuranceClasses}" itemLabel="dropdownLabel" itemValue="code" />
							</form:select>		  				
	  					</span>	
		  				<label class="control-label col-sm-1" for="claimStatus">
		  					<spring:message code="claim.status" />
		  				</label>
	  					<span class="col-sm-3">
  							<form:select path="criteria.claimStatus" class="form-control input-sm" id="claimStatus">
								<form:option label="Select" value="" />
								<form:options items="${claimSearchForm.statuses}" itemLabel="label" />
							</form:select>
	  					</span>		  				
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="insurer">
		  					<spring:message code="claim.insurer" />
		  				</label>
		  				<span class="col-sm-5">
  							<form:select path="criteria.insurerCode" class="form-control input-sm" id="insurer">
								<form:option label="Select" value="" />
								<form:options items="${claimSearchForm.insurers}" itemLabel="dropdownLabel" itemValue="code" />
							</form:select>
		   				</span>
		   				<label class="control-label col-sm-1" for="policyNo">
		  					<spring:message code="claim.policyNo" />
		  				</label>
	  					<span class="col-sm-3">
  							<form:input path="criteria.policyNo" class="form-control input-sm" id="policyNo"/>
	  					</span>
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="claimNo">
		  					<spring:message code="claim.claimNo" />
		  				</label>
		  				<span class="col-sm-3">
  							<form:input path="criteria.claimNo" class="form-control input-sm" id="claimNo"/>
	  					</span>			   									
		   				<span class="col-sm-1"></span>
		  				<label class="control-label col-sm-2" for="cmsRefNo">
		  					<spring:message code="claim.cmsRefNo" />
		  				</label>		   				
		  				<span class="col-sm-2">
  							<form:input path="criteria.cmsRefNo" class="form-control input-sm" id="cmsRefNo"/>		  				
		   				</span>		   				
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="fromLossDate">
		  					<spring:message code="claim.lossDate" />
		  				</label>
	  					<span class="col-sm-4">
							<form:input path="criteria.fromLossDate" class="form-inline input-sm col-sm-5" id="fromLossDate"/>
							<label class="form-inline col-sm-2 text-center">-</label>							
							<form:input path="criteria.toLossDate" class="form-inline input-sm col-sm-5" id="toLossDate"/>
	  					</span>			   				
		  				<label class="control-label col-sm-2" for="insurerRef">
		  					<spring:message code="claim.insurerRef" />
		  				</label>		   				
		  				<span class="col-sm-2">
  							<form:input path="criteria.insurerRef" class="form-control input-sm" id="insurerRef"/>		  				
		   				</span>
					</div>
					<div class="form-group row">
		  				<label class="control-label col-sm-2" for="vehicleRegNo">
		  					<spring:message code="claim.vehicleRegNo" />
		  				</label>
		  				<span class="col-sm-3">
  							<form:input path="criteria.vehicleRegNo" class="form-control input-sm" id="vehicleRegNo"/>
	  					</span>			   									
		   				<span class="col-sm-3"></span>
	  					<div class="col-sm-3">
							<div class="checkbox">
  								<label class="control-label">
	  								<strong>
	    								<form:checkbox path="criteria.deletedOnly" id="o.deleted"/>
	    								<spring:message code="claim.deletedOnly" />
	  								</strong>
  								</label>
							</div>	  					
						</div>					
					</div>
					
		   			<div class="form-group row">
		  				<label class="control-label col-sm-2" for="adjuster">
		  					<spring:message code="claim.adjusterFirm" />
		  				</label>		   			
		  				<span class="col-sm-5">
  							<form:select path="criteria.adjusterId" class="form-control input-sm" id="adjuster">
								<form:option label="Select" value="0" />
								<form:options items="${claimSearchForm.adjusters}" itemLabel="firmName" itemValue="id" />
							</form:select>		  				
	  					</span>	
	  				</div>
					
		   			<div class="form-group row">
		  				<label class="control-label col-sm-2" for="solicitor">
		  					<spring:message code="claim.solicitorFirm" />
		  				</label>		   			
		  				<span class="col-sm-5">
  							<form:select path="criteria.solicitorId" class="form-control input-sm" id="solicitor">
								<form:option label="Select" value="0" />
								<form:options items="${claimSearchForm.solicitors}" itemLabel="firmName" itemValue="id" />
							</form:select>		  				
	  					</span>	
	  				</div>
	  				
		   		</div>		
				<div class="panel-footer" align="right">
					<form:button name="action" value="search" class="btn btn-primary"><spring:message code="button.search" /></form:button>
					<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>						
				</div>		   				
				</form:form>
			</div>
		</div>
	</div>
	
	<c:if test="${claimSearchForm.searched}">
		<div class="row">
		    <div class="col-sm-1"></div>
		  	<div class="panel-group col-sm-12">
			    <div class="panel panel-primary">
					<div class="panel-body">
						<datatables:table id="myTableId" data="${claimSearchForm.claims}" rowIdBase="claimId" stateSave="true" cssClass="table table-striped" row="row" ext="responsive" autoWidth="false" filterable="false">						
							<datatables:column titleKey="claim.claimNo" cssStyle="padding:10px 10px" sortInitOrder="1" sortInitDirection="asc">
								<a href="${editUrl}/${url_param_prefix}/${row.claimId}" 
									data-toggle="tooltip" data-placement="top" data-html="true" title="${row.title}"
									style="color: blueviolet; font-weight: bold;"/><c:out value="${row.claimNo}"/></a>
							</datatables:column>
							<datatables:column property="policyNo" titleKey="claim.policyNo" cssStyle="padding:10px 10px" sortInitOrder="1"/>
							<datatables:column property="cmsRefNo" titleKey="claim.cmsRefNo" cssStyle="padding:10px 10px" sortInitOrder="2"/>
							<datatables:column property="status" titleKey="claim.status" cssStyle="padding:10px 10px" sortInitOrder="3"/>
							<datatables:column property="insuranceClassCode" titleKey="claim.insuranceClass" cssStyle="padding:10px 10px" sortInitOrder="4"/>
							<datatables:column property="notificationDate" titleKey="claim.notificationDate" cssStyle="padding:10px 10px" sortInitOrder="5" format="{0,date,dd-MM-yyyy}"/>
							<datatables:column property="lossDate" titleKey="claim.lossDate" cssStyle="padding:10px 10px" sortInitOrder="6" format="{0,date,dd-MM-yyyy}"/>
							<datatables:column property="lossType" titleKey="claim.lossType" cssStyle="padding:10px 10px" sortInitOrder="7"/>
							<datatables:column property="contractor" titleKey="claim.contractor" cssStyle="padding:10px 10px; width:12%" sortInitOrder="8"/>
							<datatables:column property="solicitorFirmName" titleKey="claim.solicitorFirm" cssStyle="padding:10px 10px; width:12%" sortInitOrder="9"/>
							<datatables:column property="adjusterFirmName" titleKey="claim.adjusterFirm" cssStyle="padding:10px 10px; width:12%" sortInitOrder="10"/>
							<c:choose>
								<c:when test="${claimSearchForm.criteria.deletedOnly == null || !claimSearchForm.criteria.deletedOnly}">
									<c:if test="${claimSearchForm.hasDeletePermission}">
										<datatables:column titleKey="claim.delete" cssStyle="padding:10px 10px" searchable="false" sortable="false">
											<a href="#" onclick="deleteConfirm(${row.claimId});"><spring:message code="label.delete" /></a>
										</datatables:column>
									</c:if>
								</c:when>
								<c:otherwise>
									<c:if test="${claimSearchForm.hasRevertPermission}">
										<datatables:column titleKey="claim.revert" cssStyle="padding:10px 10px" searchable="false" sortable="false">
											<a href="#" onclick="revertConfirm(${row.claimId});"><spring:message code="label.revert" /></a>
										</datatables:column>
									</c:if>
								</c:otherwise>
							</c:choose>							
						</datatables:table>
					</div>
				</div>
			</div>
		</div>
	</c:if>
	
	<div id="confirmationDialog" class="modal bootstrap-dialog fade" role="dialog">
		<form:form commandName="claimDeleteForm" method="post" action="${deleteUrl}" role="form" class="form-horizontal" >
			<div class="modal-dialog">
				<div class="modal-content">
				  	<div class="modal-header">
						<h4>
							&nbsp;
						</h4>
					</div>
					
			  		<div class="modal-body"  style="text-align: center">
	    				<div>
	    					<span id="confirmationNote"> </span>
	    				</div>
					<div style="height:5%"></div>
			  		</div>
					
					<div class="modal-footer">	
						<button id="confirmDelete" type="button" class="btn btn-primary" aria-hidden="true" onclick="doDelete();">
							<spring:message code="button.confirm" />
						</button>
						<button id="confirmRevert" type="button" class="btn btn-primary" aria-hidden="true" onclick="doRevert();">
							<spring:message code="button.revert" />
						</button>
						<button type="button" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">
							<spring:message code="button.close" />
						</button>
					</div>
				</div>
			</div>
		</form:form>
	</div>
</div>