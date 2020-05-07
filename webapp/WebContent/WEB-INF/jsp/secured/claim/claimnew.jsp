<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/claim" />
<spring:url var="saveUrl" value="/secured/claim/new" />
<spring:url var="uploadUrl" value="/secured/claim/uploadfile" />
<spring:url var="refreshPolicyUrl" value="/secured/claim/refresh/policy"/>
<spring:message var="delete" code="claim.delete" />
<sec:authentication property="principal.username" var="currentUser"/>
<style>
.kv-avatar .file-preview-frame,.kv-avatar .file-preview-frame:hover {
    margin: 0;
    padding: 0;
    border: none;
    box-shadow: none;
    text-align: center;
}
.kv-avatar .file-input {
    display: table-cell;
    max-width: 150px;
    max-height: 70px
}

.file-input .file-preview,.file-input .file-preview .file-drop-zone{
	height: initial;
}
</style>
<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {	    
		var rp2f=$("#p2Flag").val();
		if(rp2f === "RP2"){
			$('#relatedPolicyRef2DivId').show();	
		} else {
			$('#relatedPolicyRef2DivId').hide();
		}
		
		var rp3f=$("#p3Flag").val();
		if(rp3f === "RP3"){
			$('#relatedPolicyRef3DivId').show();	
		} else {
			$('#relatedPolicyRef3DivId').hide();
		}
		
		var rp4f=$("#p4Flag").val();
		if(rp4f === "RP4"){
			$('#relatedPolicyRef4DivId').show();	
		} else {
			$('#relatedPolicyRef4DivId').hide();
		}
		
		$('#notificationDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#offerDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#paidDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#lossDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false			
		});
		$('#docCompletionDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false
		});
		$('#adjFinalReportDate').datetimepicker({
			format: "d-M-Y",
			timepicker: false,
			scrollMonth : false,
			scrollInput : false
		});

		$("#estLoss").number( true, 2 );
		$("#reserveAmnt").number( true, 2 );
		$("#offeredAmt").number( true, 2 );
		$("#paidAmt").number( true, 2 );
		$("#excessAmt").number( true, 2 );
		
		$("input#claimRemarkAddRow").click(function() {
			var idx = $("#claimRemarkTable").find("input[id$='\\.order']").length;
			var html = "<tr><td><input id='claim.remarks" + idx + ".order' name='claim.remarks[" + idx + "].order' readonly='readonly' type='text' class='form-control input-sm' value='" + (idx + 1) + "'/></td>" +
			"<td><input id='claim.remarks" + idx + ".updateUser' name='claim.remarks[" + idx + "].updateUser' class='form-control input-sm' type='text' readonly='true'/></td>" +
			"<td><input id='claim.remarks" + idx + ".updateDate' name='claim.remarks[" + idx + "].updateDate' class='form-control input-sm' type='text' readonly='true'/></td>" +
			"<td><textarea id='claim.remarks" + idx +".remark' name='claim.remarks[" + idx + "].remark' class='form-control input-sm' rows='3'></textarea></td>" +
			"<td><a id='dClaimRemark" + idx + "' href='#' onclick='deleteClaimRemarkRow(this)'>${delete}</a></td</tr>";			
			$("#claimRemarkTable tbody>tr:nth-last-child(1)").before(html);

			$("#claimRemarkTable").find("input[id$='claim.remarks" + idx + ".updateUser']").val("${currentUser}");
			$("#claimRemarkTable").find("input[id$='claim.remarks" + idx + ".updateDate']").val(moment().format('DD-MMM-YYYY HH:mm:ss'));
		});		     

		var maxLength = 500;
        $("textarea#remark").after("<div><span id='remarkRemainingLength'>"
                  + maxLength + "</span> <spring:message code="claim.remaining" /></div>");
        $("textarea#remark").bind("keyup change", function(){checkMaxLength(this.id,  maxLength); } );
        checkMaxLength($("textarea#remark").attr("id"), maxLength);

		var maxLengthOutstandingDoc = 800;
        $("textarea#outstandingDoc").after("<div><span id='outstandingDocRemainingLength'>"
                  + maxLengthOutstandingDoc + "</span> <spring:message code="claim.remaining" /></div>");
        $("textarea#outstandingDoc").bind("keyup change", function(){checkMaxLength(this.id,  maxLengthOutstandingDoc); } );
        checkMaxLength($("textarea#outstandingDoc").attr("id"), maxLengthOutstandingDoc);
        
        $('#insuredContactNo').keydown(function (e) {
            var k = String.fromCharCode(e.which);
            if (k.match(/[^0-9\x08]/g))
              e.preventDefault();
       	});
        
        $('#adjusterContactNo').keydown(function (e) {
            var k = String.fromCharCode(e.which);
            if (k.match(/[^0-9\x08]/g))
              e.preventDefault();
       	});
        
        $('#solicitorContactNo').keydown(function (e) {
            var k = String.fromCharCode(e.which);
            if (k.match(/[^0-9\x08]/g))
              e.preventDefault();
       	});
	});
	
	function deleteClaimRemarkRow(source) {		
		$(source).parent().parent().remove();
		var idx = 1;
		$("#claimRemarkTable").find("input[id$='\\.order']").each(function() {
			$(this).val(idx++);
		});

		idx = 0;
		$("#claimRemarkTable").find("input[id$='\\.id']").each(function() {			
			$(this).attr("id", "claim.remarks" + idx + ".id");
			$(this).attr("name", "claim.remarks[" + idx + "].id");
			idx++;
		});
		
		idx = 0;
		$("#claimRemarkTable").find("input[id$='\\.order']").each(function() {			
			$(this).attr("id", "claim.remarks" + idx + ".order");
			$(this).attr("name", "claim.remarks[" + idx + "].order");
			idx++;
		});

		idx = 0;
		$("#claimRemarkTable").find("input[id$='\\.updateUser']").each(function() {			
			$(this).attr("id", "claim.remarks" + idx + ".updateUser");
			$(this).attr("name", "claim.remarks[" + idx + "].updateUser");
			idx++;
		});

		idx = 0;
		$("#claimRemarkTable").find("input[id$='\\.updateDate']").each(function() {			
			$(this).attr("id", "claim.remarks" + idx + ".updateDate");
			$(this).attr("name", "claim.remarks[" + idx + "].updateDate");
			idx++;
		});
		
		idx = 0;
		$("#claimRemarkTable").find("textarea[id$='\\.remark']").each(function() {			
			$(this).attr("id", "claim.remarks" + idx + ".remark");
			$(this).attr("name", "claim.remarks[" + idx + "].remark");
			idx++;
		});
				
		idx = 0;
		$("#claimRemarkTable").find("a[id^='dClaimRemark']").each(function() {			
			$(this).attr("id", "dClaimRemark" + idx);
			idx++;
		});
	}

	function back() {
		$("#claimSetupForm").attr("action", "${backUrl}").submit();
	}	

    function checkMaxLength(textareaID, maxLength){

        currentLengthInTextarea = $("#"+textareaID).val().length;
        $("#" + textareaID + "RemainingLength").text(parseInt(maxLength) - parseInt(currentLengthInTextarea));

		if (currentLengthInTextarea > (maxLength)) {

			// Trim the field current length over the maxlength.
			$("textarea#" + textareaID).val($("textarea#" + textareaID).val().slice(0, maxLength));
			$("#" + textareaID + "RemainingLength").text(0);
		}
    }
    
    function deleteFile(obj){
		$(obj).parent().parent().remove();
		var str = obj.id; 
	    var res = str.substr(9, 6);
	    var idx = res.split('.');
		var objID = $("input[id$='filesList" + idx[0] + "\\.rpid']").attr('id');
		var idr = $("input[id='" + objID + "']").val();
		if (idr) {
			
		} else {
			return '<a href=\"<spring:url value="/secured/claim/deletefile/${url_param_prefix}/"/>' + idr + '\"><spring:message code="claim.delete"/></a>';	
		}
    }    
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-12">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="claim.newClaim" /></div>
				<form:form commandName="claimSetupForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:hidden id="p2Flag" path="relatedPolicyRef2Flag"/>
				<form:hidden id="p3Flag" path="relatedPolicyRef3Flag"/>
				<form:hidden id="p4Flag" path="relatedPolicyRef4Flag"/>
				<form:hidden id="companyId" path="claim.policy.company.id"/>
				<div class="panel-body">
					<form:errors element="div" cssClass="errorblock" path="*" />
					<ul class="nav nav-tabs">
				 		<li class="active">
				 			<a data-toggle="tab" href="#tab-claim"><spring:message code="claim.claim" /></a>
				 		</li>
				 		<li>
				 			<a data-toggle="tab" href="#tab-remark"><spring:message code="claim.remark" /></a>
				 		</li>
				 		<li>
				 			<a data-toggle="tab" href="#tab-attachment"><spring:message code="label.attachment" /></a>
				 		</li>
					</ul>
					<div class="tab-content">
						<div id="tab-claim" class="tab-pane fade in active panel-body">
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="claimNo">
		  							<spring:message code="claim.claimNo" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.claimNo" class="form-control input-sm" id="claimNo" readOnly="true"/>
		   						</span>					
		   						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="claimStatus">
		  							<spring:message code="claim.status" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		  							<form:select path="claim.status" class="form-control input-sm" id="claimStatus">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.statuses}" itemLabel="label" />
									</form:select>
		   						</span>					   								
							</div>							
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="notificationDate">
		  							<spring:message code="claim.notificationDate" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.notifyDate" class="form-control input-sm" id="notificationDate"/>		  						
		  						</span>
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="policyId">
		  							<spring:message code="claim.policyNo" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		  							<form:input path="claim.policy.policyNo" class="form-control input-sm" id="policyNo" readonly="true"/>
		  							<form:hidden path="claim.policy.id" id="policyId"/>
		   						</span>					   								
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="insuranceClassName">
		  							<spring:message code="claim.insuranceClass" />
		  						</label>
		  						<span class="col-sm-2">
		  							<form:input path="claim.policy.insuranceClass.name" class="form-control input-sm" id="insuranceClassName" readonly="true"/>
		  							<form:hidden path="claim.policy.insuranceClass.code"/>
		  						</span>
		  						<label class="control-label col-sm-2" for="insurerName">
		  							<spring:message code="claim.insurer" />
		  						</label>
		  						<span class="col-sm-5">
		  							<form:input path="claim.policy.insurer.name" class="form-control input-sm" id="insurerName" readonly="true"/>		  							
		  						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="companyName">
		  							<spring:message code="claim.companyName.group"/>
<%-- 		  							<spring:message code="label.new.name" arguments="Group"/> --%>
		  						</label>
		  						<span class="col-sm-9">
		  							<form:input path="claim.policy.company.name" class="form-control input-sm" id="companyName" readonly="true"/>
		  							<form:hidden path="claim.policy.company.code"/>		  							
		  						</span>
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="cmsRefNo">
		  							<spring:message code="claim.cmsRefNo" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:input path="claim.cmsRefNo" class="form-control input-sm" id="cmsRefNo"/>		  						
		  						</span>	
 							</div>	
							<div id="relatedPolicyRef1DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef1">
		  							<spring:message code="claim.relatedPolicyRef1" />
		  						</label>
		  						<span class="col-sm-8">
	  								<form:select path="claim.relatedPolicy[0].policyNo" class="form-control input-sm" id="relatedPolicyRef1">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
									</form:select>
		  						</span>
		  						<span class="col-sm-1">
		  							<input id="addRelatedPolicy" type="submit" name="action" class="form-control input-sm" value="<spring:message code="label.add" />"/>
		  						</span>
							</div>
							<div id="relatedPolicyRef2DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef2">
		  							<spring:message code="claim.relatedPolicyRef2" />
		  						</label>
		  						<span class="col-sm-8">
	  								<form:select path="claim.relatedPolicy[1].policyNo" class="form-control input-sm" id="relatedPolicyRef2">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
									</form:select>
		  						</span>
							</div>
							<div id="relatedPolicyRef3DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef3">
		  							<spring:message code="claim.relatedPolicyRef3" />
		  						</label>
		  						<span class="col-sm-8">
	  								<form:select path="claim.relatedPolicy[2].policyNo" class="form-control input-sm" id="relatedPolicyRef3">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
									</form:select>
		  						</span>
							</div>	
							<div id="relatedPolicyRef4DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef4">
		  							<spring:message code="claim.relatedPolicyRef4" />
		  						</label>
		  						<span class="col-sm-8">
	  								<form:select path="claim.relatedPolicy[3].policyNo" class="form-control input-sm" id="relatedPolicyRef4">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
									</form:select>
		  						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="insuredContent">
		  							<spring:message code="claim.insuredContent" />
		  						</label>
		  						<span class="col-sm-9">
		  							<form:textarea path="claim.policy.insuredContent" class="form-control input-sm" rows="6" id="insuredContent" readonly="true"/>		  							
		   						</span>
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="contractor">
		  							<spring:message code="claim.contractor" />
		  						</label>
								<span class="col-sm-4">
									<form:input path="claim.contractor" class="form-control input-sm" id="contractor"/>
								</span>		  								
								<label class="control-label col-sm-2" for="department">
		  							<spring:message code="claim.department.company" />
<%-- 		  							<spring:message code="claim.department" /> --%>
		  						</label>
		  						<span class="col-sm-3">
									<form:select path="claim.department.id" class="form-control input-sm" id="department">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.departments}" itemLabel="name" itemValue="id" />
									</form:select>
									
		  						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="lossDate">
		  							<spring:message code="claim.lossDate" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.lossDate" class="form-control input-sm" id="lossDate"/>		  						
		  						</span>
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="lossType">
		  							<spring:message code="claim.lossType" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		  							<form:select path="claim.lossType.code" class="form-control input-sm" id="lossType">
										<form:option label="Select" value="" />
										<form:options items="${claimSetupForm.lossTypes}" itemLabel="name" itemValue="code" />
									</form:select>
		  						</span>
							</div>
		  					<div class="form-group row">
		  						<label class="control-label col-sm-2" for="estLoss">
		  							<spring:message code="claim.estLoss" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.estLostAmount" class="form-control input-sm" id="estLoss"/>
								</span>
		  						<label class="control-label col-sm-3" for="reserveAmnt">
		  							<spring:message code="claim.claimReservedAmount" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.claimReservedAmount" class="form-control input-sm" id="reserveAmnt" readonly="${claimSetupForm.claim.deleted}"/>
								</span>
		  					</div>
		  					<div class="form-group row">
		  						<label class="control-label col-sm-2" for="vehicleRegNo">
		  							<spring:message code="claim.vehicleRegNo" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.vehicleRegNo" class="form-control input-sm" id="vehicleRegNo"/>
								</span>		  					 
		  					</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="lossLoc">
		  							<spring:message code="claim.lossLoc" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:input path="claim.lossLocation" class="form-control input-sm" id="lossLoc"/>		  						
		  						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="lossDescription">
		  							<spring:message code="claim.lossDescription" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="claim.lossDescription" class="form-control input-sm" rows="6" id="lossDescription"/>
		   						</span>					
							</div>	
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="insuredContactPerson">
		  							<spring:message code="claim.insuredName" />
		  						</label>
								<span class="col-sm-4">
									<form:input path="claim.insuredContact.contactPerson" class="form-control input-sm" id="insuredContactPerson"/>
								</span>
								<label class="control-label col-sm-2" for="insuredContactNo">
									<spring:message code="claim.insuredContactNo" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="claim.insuredContact.telNo" class="form-control input-sm" id="insuredContactNo"/>
		   						</span>
							</div>	

							<hr/>
		   					<div class="form-group row">
		   						<label class="control-label col-sm-2" for="insurerRef">
		  							<spring:message code="claim.insurerRef" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.insurerRef" class="form-control input-sm" id="insurerRef"/>
								</span>
		   					</div>		
		   										
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="adjusterFirm">
				  					<spring:message code="claim.adjusterFirm" />
				  				</label>		   			
				  				<span class="col-sm-4">
		  							<form:select path="claim.adjuster.id" class="form-control input-sm" id="adjusterFirm">
										<form:option label="Select" value="0" />
										<form:options items="${claimSearchForm.adjusters}" itemLabel="firmName" itemValue="id" />
									</form:select>		  				
			  					</span>	
		  						
								<label class="control-label col-sm-2" for="adjusterContactPerson">
									<spring:message code="claim.adjusterName" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="claim.adjusterContact.contactPerson" class="form-control input-sm" id="adjusterContactPerson"/>
		   						</span>
							</div>
							
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="adjusterRef">
		  							<spring:message code="claim.adjusterRef" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.adjusterRef" class="form-control input-sm" id="adjusterRef"/>
								</span>
		  						<label class="control-label col-sm-3" for="adjusterContactNo">
		  							<spring:message code="claim.adjusterContactNo" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.adjusterContact.telNo" class="form-control input-sm" id="adjusterContactNo"/>
								</span>
							</div>
							
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="solicitorFirm">
				  					<spring:message code="claim.solicitorFirm" />
				  				</label>		   			
				  				<span class="col-sm-4">
		  							<form:select path="claim.solicitor.id" class="form-control input-sm" id="solicitorFirm">
										<form:option label="Select" value="0" />
										<form:options items="${claimSearchForm.solicitors}" itemLabel="firmName" itemValue="id" />
									</form:select>		  				
			  					</span>	
		  						
								<label class="control-label col-sm-2" for="solicitorContactPerson">
									<spring:message code="claim.solicitorName" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="claim.solicitorContact.contactPerson" class="form-control input-sm" id="solicitorContactPerson"/>
		   						</span>
							</div>

							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="solicitorRef">
		  							<spring:message code="claim.solicitorRef" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.solicitorRef" class="form-control input-sm" id="solicitorRef"/>
								</span>
		  						<label class="control-label col-sm-3" for="solicitorContactNo">
		  							<spring:message code="claim.solicitorContactNo" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.solicitorContact.telNo" class="form-control input-sm" id="solicitorContactNo"/>
								</span>
							</div>
		   					
							<hr/>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="docCompletionDate">
		  							<spring:message code="claim.docCompletionDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.docCompletionDate" class="form-control input-sm" id="docCompletionDate"/>		  						
		  						</span>
		  						<label class="control-label col-sm-3" for="adjFinalReportDate">
		  							<spring:message code="claim.adjFinalReportDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.adjFinalReportDate" class="form-control input-sm" id="adjFinalReportDate"/>		
		  						</span>
		  					</div>							
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="offerDate">
		  							<spring:message code="claim.approvedDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.approvalDate" class="form-control input-sm" id="offerDate"/>		  						
		  						</span>
		  						<span class="col-sm-1"></span>
								<label class="control-label col-sm-2" for="offeredAmt">
		  							<spring:message code="claim.offeredAmt" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.offerAmount" class="form-control input-sm" id="offeredAmt"/>
								</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="paidDate">
		  							<spring:message code="claim.paidDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.paidDate" class="form-control input-sm" id="paidDate"/>		  						
		  						</span>
								<span class="col-sm-1"></span>
								<label class="control-label col-sm-2" for="paidAmt">
		  							<spring:message code="claim.approvedAmt" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.approvalAmount" class="form-control input-sm" id="paidAmt"/>
								</span>
							</div>	
							<div class="form-group row">
								<label class="control-label col-sm-2" for="excessAmt">
		  							<spring:message code="claim.excessAmt" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.excessAmount" class="form-control input-sm" id="excessAmt"/>
								</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="remark">
		  							<spring:message code="claim.remark" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="claim.remark" class="form-control input-sm" rows="6" id="remark"/>
		   						</span>					
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="outstandingDoc">
		  							<spring:message code="claim.outstandingDocuments" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="claim.outstandingDoc" class="form-control input-sm" rows="6" id="outstandingDoc"/>
		   						</span>					
							</div>
						</div>
						<div id="tab-remark" class="tab-pane fade in panel-body">
							<div class="form-group row">
		  						<div class="col-sm-12">
									<table id="claimRemarkTable" class="table table-bordered table-hover">
										<thead>
											<tr >
												<th class="text-center col-sm-1"><spring:message code="claim.no" /></th>
												<th class="text-center col-sm-2"><spring:message code="claim.by" /></th>
												<th class="text-center col-sm-3"><spring:message code="claim.on" /></th>
												<th class="text-center col-sm-5"><spring:message code="claim.remark" /></th>
												<th class="text-center col-sm-1"></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="claimRemark" varStatus="i" begin="0" items="${claimSetupForm.claim.remarks}">
												<tr>
													<td><form:input path="claim.remarks[${i.index}].order" class="form-control input-sm" id="claim.remarks${i.index}.order" readonly="true"/></td>
													<td><form:input path="claim.remarks[${i.index}].updateUser" class="form-control input-sm" id="claim.remarks${i.index}.updateUser" readonly="true"/></td>
													<td><form:input path="claim.remarks[${i.index}].updateDate" class="form-control input-sm" id="claim.remarks${i.index}.updateDate" readonly="true"/></td>
													<td><form:textarea rows="3" path="claim.remarks[${i.index}].remark" class="form-control input-sm" id="claim.remarks${i.index}.remark"></form:textarea></td>
													<td><a id='dClaimRemark${i.index}' href='#' onclick='deleteClaimRemarkRow(this)'><spring:message code="claim.delete"/></a></td>
												</tr>
											</c:forEach>
											<tr>
						                    	<td colspan="5"	align="center"><input id="claimRemarkAddRow" type="button" class="form-control input-sm" value="<spring:message code="claim.addNewRow" />"/></td>
						                    </tr>
										</tbody>										
									</table>
								</div>
							</div>
						</div>
						<div id="tab-attachment" class="tab-pane fade in panel-body">
							<div class="form-group row">
								<div class="col-sm-12">
									<table id="attachmentTable" class="table table-bordered table-hover">
										<thead>
											<tr>
												<th class="text-center col-sm-1"><spring:message code="claim.no" /></th>
												<th class="text-center col-sm-9"><spring:message code="label.name" /></th>
												<th class="text-center col-sm-1"></th>
												<th class="text-center col-sm-1"></th>
											</tr>
										</thead>
										<tbody>
											<tr style="height:inherit">
												<div id="kv-avatar-errors-1" class="center-block col-sm-12"></div>
												<input id="file1" name="attachment" type="file" class="file-loading" multiple/>
											</tr>
											<c:forEach var="claimFile" varStatus="i" begin="0" items="${claimSetupForm.filesList}">
												<tr>
													<td>
														<label id="filesList${i.index}.order" style="">${i.index}</label>
														<form:hidden id="filesList${i.index}.rpid" path="filesList[${i.index}].id"/>
													</td>	
													<td>	
														<form:input id="filesList${i.index}.rpname" path="filesList[${i.index}].name" name="attachment" class="form-control input-sm" readonly="true"/>
													</td>
													<td>
														<a id='filesList${i.index}.del' href='#' onclick='deleteFile(this)'><spring:message code="claim.delete"/></a>
													</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>			
								</div>
							</div>
						</div>
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

<script>
var $input = $(".file-loading");
$input.fileinput({
	uploadUrl: "${uploadUrl}", // server upload action
	uploadAsync: true,
	overwriteInitial: false,
    maxFileSize: 25000,
    browseClass: "btn btn-primary btn-block",
    showClose: false,
    showUpload: false,
    showBrowse: false,
    showPreview: true,
    showCaption: false,
    showRemove: false,
    browseOnZoneClick: true,
    //removeLabel: '',
    removeIcon: '<i class="glyphicon glyphicon-remove"></i>',
    removeTitle: 'Cancel or reset changes',
    elErrorContainer: '#kv-avatar-errors-1',
    msgErrorClass: 'alert alert-block alert-danger',
    allowedFileExtensions: ["jpg", "png", "gif", "docx", "doc", "pdf", "txt", "xls", "xlsx", "tif", "msg"]
}).on("filebatchselected", function(event, files) {
    $input.fileinput("upload");
});	
</script>