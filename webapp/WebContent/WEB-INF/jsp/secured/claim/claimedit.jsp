<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/claim" />
<spring:url var="saveUrl" value="/secured/claim/edit" />
<spring:url var="uploadUrl" value="/secured/claim/uploadfile" />
<spring:url var="notificationUrl" value="/secured/claim/notification" />
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
		$(".file-drop-zone")
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
			var html = "<tr><td><input id='claim.remarks" + idx + ".order' name='claim.remarks[" + idx + "].order' readonly='readonly' type='text' class='form-control input-sm' value='" + (idx + 1) + "'/>" +
			"<input id='claim.remarks" + idx + ".id' name='claim.remarks[" + idx + "].id' type='hidden'/></td>" +
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

	function notification() {
		$("#claimSetupForm").attr("action", "${notificationUrl}").submit();
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
		var str = obj.id; 
	    var res = str.substr(9, 6);
	    var idx = res.split('.');
		var objID = $("input[id$='filesList" + idx[0] + "\\.rpid']").attr('id');
		var idr = $("input[id='" + objID + "']").val();
		var url = '<spring:url value="/secured/claim/deletefile/${url_param_prefix}/"/>' + idr;
		obj.href = url;
		return true;	
    }
    
	function downloadFile(obj){
		var str = obj.id; 
	    var res = str.substr(9, 6);
	    var idx = res.split('.');
		var objID = $("input[id$='filesList" + idx[0] + "\\.rpid']").attr('id');
		var idr = $("input[id='" + objID + "']").val();
		window.open('${pageContext.request.contextPath}/secured/claim/downloadfile/${url_param_prefix}/'+ idr +'/', '_blank'); 
	};
	
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-12">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="claim.editClaim" /></div>
				<form:form commandName="claimSetupForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:hidden id="p2Flag" path="relatedPolicyRef2Flag"/>
				<form:hidden id="p3Flag" path="relatedPolicyRef3Flag"/>
				<form:hidden id="p4Flag" path="relatedPolicyRef4Flag"/>
				<form:hidden id="companyId" path="claim.policy.company.id"/>
				<div class="panel-body">
					<c:if test="${not empty msg}">
					    <div class="alert alert-success alert-dismissible" role="alert">
						<button type="button" class="close" data-dismiss="alert"  aria-label="Close">
							<span aria-hidden="true">×</span>
						</button>
						<strong>${msg}</strong>
					    </div>
					</c:if>
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
									<form:hidden path="claim.id" id="claimId"/>
		   						</span>					
		   						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="claimStatus">
		  							<spring:message code="claim.status" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		  							<c:choose>
		  								<c:when test="${claimSetupForm.claim.deleted}">
		  									<form:input path="claim.status.label" class="form-control input-sm" id="claimStatus" readonly="true"/>
		  									<form:hidden path="claim.status"/>
		  								</c:when>
		  								<c:otherwise>
				  							<form:select path="claim.status" class="form-control input-sm" id="claimStatus" >
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.statuses}" itemLabel="label" />
											</form:select>
		  								</c:otherwise>
		  							</c:choose>
		   						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="notificationDate">
		  							<spring:message code="claim.notificationDate" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.notifyDate" class="form-control input-sm" id="notificationDate" readonly="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}"/>
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
		  							<form:input path="claim.policy.insuranceClass.code" class="form-control input-sm" id="insuranceClassName" readonly="true"/>
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
		   							<form:input path="claim.cmsRefNo" class="form-control input-sm" id="cmsRefNo" readonly="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}"/>		  						
		  						</span>	
							</div>	
							<div id="relatedPolicyRef1DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef1">
		  							<spring:message code="claim.relatedPolicyRef1" />
		  						</label>
		  						<span class="col-sm-8">
		  							<c:choose>
		  								<c:when test="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}">
		  									<form:input path="ddRelatedPolicyRef1" class="form-control input-sm" id="relatedPolicyRef1" readonly="true"/>
		  									<form:hidden path="claim.relatedPolicy[0].policyNo"/>
		  								</c:when>
		  								<c:otherwise>
			  								<form:select path="claim.relatedPolicy[0].policyNo" class="form-control input-sm" id="relatedPolicyRef1">
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
											</form:select>		  								
		  								</c:otherwise>
		  							</c:choose>
		  						</span>
		  						<span class="col-sm-1">
		  							<c:if test="${!(claimSetupForm.claim.deleted || claimSetupForm.sibUser)}">
		  								<input id="addRelatedPolicy" type="submit" name="action" class="a" value="<spring:message code="label.add" />"/>
		  							</c:if>
		  						</span>
							</div>
							<div id="relatedPolicyRef2DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef2">
		  							<spring:message code="claim.relatedPolicyRef2" />
		  						</label>
		  						<span class="col-sm-8">
		  							<c:choose>
		  								<c:when test="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}">
		  									<form:input path="ddRelatedPolicyRef2" class="form-control input-sm" id="relatedPolicyRef2" readonly="true"/>
		  									<form:hidden path="claim.relatedPolicy[1].policyNo"/>
		  								</c:when>
		  								<c:otherwise>
			  								<form:select path="claim.relatedPolicy[1].policyNo" class="form-control input-sm" id="relatedPolicyRef2">
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
											</form:select>		  								
		  								</c:otherwise>
		  							</c:choose>		  						
		  						</span>
							</div>
							<div id="relatedPolicyRef3DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef3">
		  							<spring:message code="claim.relatedPolicyRef3" />
		  						</label>
		  						<span class="col-sm-8">
									<c:choose>
		  								<c:when test="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}">
		  									<form:input path="ddRelatedPolicyRef3" class="form-control input-sm" id="relatedPolicyRef3" readonly="true"/>
		  									<form:hidden path="claim.relatedPolicy[2].policyNo"/>
		  								</c:when>
		  								<c:otherwise>
			  								<form:select path="claim.relatedPolicy[2].policyNo" class="form-control input-sm" id="relatedPolicyRef3">
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
											</form:select>		  								
		  								</c:otherwise>
		  							</c:choose>		  						
		  						</span>
							</div>	
							<div id="relatedPolicyRef4DivId" class="form-group row">
		  						<label class="control-label col-sm-2" for="relatedPolicyRef4">
		  							<spring:message code="claim.relatedPolicyRef4" />
		  						</label>
		  						<span class="col-sm-8">
									<c:choose>
		  								<c:when test="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}">
		  									<form:input path="ddRelatedPolicyRef4" class="form-control input-sm" id="relatedPolicyRef4" readonly="true"/>
		  									<form:hidden path="claim.relatedPolicy[3].policyNo"/>
		  								</c:when>
		  								<c:otherwise>
			  								<form:select path="claim.relatedPolicy[3].policyNo" class="form-control input-sm" id="relatedPolicyRef4">
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.otherPolicy}" itemLabel="dropDownLabel" itemValue="policyNo"/>
											</form:select>		  								
		  								</c:otherwise>
		  							</c:choose>		  						
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
									<form:input path="claim.contractor" class="form-control input-sm" id="contractor" readonly="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}"/>
								</span>		  								
								<label class="control-label col-sm-2" for="department">
		  							<spring:message code="claim.department.company" />
<%-- 		  							<spring:message code="label.new.name" arguments="Group"/> --%>
		  						</label>
		  						<span class="col-sm-3">
									<c:choose>
		  								<c:when test="${claimSetupForm.claim.deleted || claimSetupForm.sibUser}">
		  									<form:input path="ddDepartment" class="form-control input-sm" id="department" readonly="true"/>
		  									<form:hidden path="claim.department.id"/>
		  								</c:when>
		  								<c:otherwise>
				  							<form:select path="claim.department.id" class="form-control input-sm" id="department">
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.departments}" itemLabel="name" itemValue="id" />
											</form:select>
		  								</c:otherwise>
		  							</c:choose>		  						
		  						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="lossDate">
		  							<spring:message code="claim.lossDate" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.lossDate" class="form-control input-sm" id="lossDate" readonly="${claimSetupForm.claim.deleted}"/>		  						
		  						</span>
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="lossType">
		  							<spring:message code="claim.lossType" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		  							<c:choose>
			  							<c:when test="${claimSetupForm.claim.deleted}">		  						
		  									<form:input path="claim.lossType.name" class="form-control input-sm" id="lossType" readonly="true"/>
		  									<form:hidden path="claim.lossType.code"/>
										</c:when>
										<c:otherwise>
				  							<form:select path="claim.lossType.code" class="form-control input-sm" id="lossType">
												<form:option label="Select" value="" />
												<form:options items="${claimSetupForm.lossTypes}" itemLabel="name" itemValue="code"/>
											</form:select>
										</c:otherwise>
									</c:choose>
		  						</span>
		  					</div>
		  					<div class="form-group row">
		  						<label class="control-label col-sm-2" for="estLoss">
		  							<spring:message code="claim.estLoss" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.estLostAmount" class="form-control input-sm" id="estLoss" readonly="${claimSetupForm.claim.deleted}"/>
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
									<form:input path="claim.vehicleRegNo" class="form-control input-sm" id="vehicleRegNo" readonly="${claimSetupForm.claim.deleted}"/>
								</span>		  					 
		  					</div>		  					
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="lossLoc">
		  							<spring:message code="claim.lossLoc" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:input path="claim.lossLocation" class="form-control input-sm" id="lossLoc" readonly="${claimSetupForm.claim.deleted}"/>		  						
		  						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="lossDescription">
		  							<spring:message code="claim.lossDescription" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="claim.lossDescription" class="form-control input-sm" rows="6" id="lossDescription" readonly="${claimSetupForm.claim.deleted}"/>
		   						</span>					
							</div>	
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="insuredContactPerson">
		  							<spring:message code="claim.insuredName" />
		  						</label>
								<span class="col-sm-4">
									<form:input path="claim.insuredContact.contactPerson" class="form-control input-sm" id="insuredContactPerson" readonly="${claimSetupForm.claim.deleted}"/>
								</span>
								<label class="control-label col-sm-2" for="insuredContactNo">
									<spring:message code="claim.insuredContactNo" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="claim.insuredContact.telNo" class="form-control input-sm" id="insuredContactNo" readonly="${claimSetupForm.claim.deleted}"/>
		   						</span>
							</div>				
							<hr/>
		   					<div class="form-group row">
		   						<label class="control-label col-sm-2" for="insurerRef">
		  							<spring:message code="claim.insurerRef" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.insurerRef" class="form-control input-sm" id="insurerRef" readonly="${claimSetupForm.claim.deleted}"/>
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
		   							<form:input path="claim.docCompletionDate" class="form-control input-sm" id="docCompletionDate" readonly="${claimSetupForm.claim.deleted}"/>		
		  						</span>
		  						<label class="control-label col-sm-3" for="adjFinalReportDate">
		  							<spring:message code="claim.adjFinalReportDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.adjFinalReportDate" class="form-control input-sm" id="adjFinalReportDate" readonly="${claimSetupForm.claim.deleted}"/>		
		  						</span>
		  					</div>							
							<div class="form-group row">
								<label class="control-label col-sm-2" for="offerDate">
		  							<spring:message code="claim.approvedDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.approvalDate" class="form-control input-sm" id="offerDate" readonly="${claimSetupForm.claim.deleted}"/>		  						
		  						</span>							
								<span class="col-sm-1"></span>
								<label class="control-label col-sm-2" for="offeredAmt">
		  							<spring:message code="claim.offeredAmt" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.offerAmount" class="form-control input-sm" id="offeredAmt" readonly="${claimSetupForm.claim.deleted}"/>
								</span>
								
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="paidDate">
		  							<spring:message code="claim.paidDate" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="claim.paidDate" class="form-control input-sm" id="paidDate" readonly="${claimSetupForm.claim.deleted}"/>		  						
		  						</span>
		  						<span class="col-sm-1"></span>
								<label class="control-label col-sm-2" for="paidAmt">
		  							<spring:message code="claim.approvedAmt" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.approvalAmount" class="form-control input-sm" id="paidAmt" readonly="${claimSetupForm.claim.deleted}"/>
								</span>
							</div>		
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="excessAmt">
		  							<spring:message code="claim.excessAmt" />
		  						</label>
								<span class="col-sm-3">
									<form:input path="claim.excessAmount" class="form-control input-sm" id="excessAmt" readonly="${claimSetupForm.claim.deleted}"/>
								</span>							
							</div>				
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="remark">
		  							<spring:message code="claim.remark" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="claim.remark" class="form-control input-sm" rows="6" id="remark" readonly="${claimSetupForm.claim.deleted}"/>
		   						</span>					
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="outstandingDoc">
		  							<spring:message code="claim.outstandingDocuments" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="claim.outstandingDoc" class="form-control input-sm" rows="6" id="outstandingDoc" readonly="${claimSetupForm.claim.deleted}"/>
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
												<c:if test="${claimSetupForm.claim.deleted == null || !claimSetupForm.claim.deleted}">
													<th class="text-center col-sm-1"></th>
												</c:if>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="claimRemark" varStatus="i" begin="0" items="${claimSetupForm.claim.remarks}">
												<tr>
													<td>
														<form:input path="claim.remarks[${i.index}].order" class="form-control input-sm" id="claim.remarks${i.index}.order" readonly="true"/>
														<form:hidden path="claim.remarks[${i.index}].id" id="claim.remarks${i.index}.id" />
													</td>
													<td><form:input path="claim.remarks[${i.index}].updateUser" class="form-control input-sm" id="claim.remarks${i.index}.updateUser" readonly="true"/></td>
													<td><form:input path="claim.remarks[${i.index}].updateDate" class="form-control input-sm" id="claim.remarks${i.index}.updateDate" readonly="true"/></td>
													<td><form:textarea rows="3" path="claim.remarks[${i.index}].remark" class="form-control input-sm" id="claim.remarks${i.index}.remark"></form:textarea></td>
													<c:if test="${claimSetupForm.claim.deleted == null || !claimSetupForm.claim.deleted}">
														<td>
															<a id='dClaimRemark${i.index}' href='#' onclick='deleteClaimRemarkRow(this)'><spring:message code="claim.delete"/></a>
														</td>
													</c:if>
												</tr>
											</c:forEach>
											<c:if test="${claimSetupForm.claim.deleted == null || !claimSetupForm.claim.deleted}">
												<tr>
							                    	<td colspan="5"	align="center"><input id="claimRemarkAddRow" type="button" class="form-control input-sm" value="<spring:message code="claim.addNewRow" />"/></td>
							                    </tr>
						                    </c:if>
										</tbody>										
									</table>
								</div>
							</div>
						</div>
						<div id="tab-attachment" class="tab-pane fade in panel-body">
							<div class="form-group row">
								<div class="col-sm-12">
									<div class="alert alert-info">
										<a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
										<strong><spring:message code="claim.tip" /></strong> <spring:message code="claim.attachmentTip" />
			  						</div>
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
											<tr style="max-height:30px">
												<div id="kv-avatar-errors-1" class="center-block col-sm-12"></div>
											</tr>
											<c:forEach var="claimFile" varStatus="i" begin="0" items="${claimSetupForm.filesList}">
												<tr>
													<td>
														<label id="filesList${i.index}.order" style="">${i.index+1}</label>
														<form:hidden id="filesList${i.index}.rpid" path="filesList[${i.index}].id"/>
													</td>	
													<td>	
														<form:input id="filesList${i.index}.rpname" path="filesList[${i.index}].name" name="attachment" class="form-control input-sm" readonly="true"/>
													</td>
													<td>	
														<a id='filesList${i.index}.dwnload' href='#' onclick='downloadFile(this)'><spring:message code="claim.download"/></a>
													</td>
													<td>
														<a id='filesList${i.index}.del' href='#' onclick='deleteFile(this)'><spring:message code="claim.delete"/></a>
													</td>
												</tr>
											</c:forEach>
											<c:if test="${claimSetupForm.claim.deleted == null || !claimSetupForm.claim.deleted}">											
												<tr>
													<input id="file1" name="attachment" type="file" class="file-loading" multiple style="max-height: 300px;"/>
												</tr>
											</c:if>
										</tbody>
									</table>			
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="panel-footer" align="right">
					<c:if test="${!claimSetupForm.claim.deleted}">
						<input type="button" class="btn btn-primary" value="<spring:message code="button.email" />" onclick="notification()"/>		
						<form:button name="action" value="submit" class="btn btn-primary"><spring:message code="button.submit" /></form:button>
					</c:if>			
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
