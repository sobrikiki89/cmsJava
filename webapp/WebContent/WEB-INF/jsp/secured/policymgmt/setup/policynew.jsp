<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/policymgmt/setup" />
<spring:url var="saveUrl" value="/secured/policymgmt/setup/new" />
<spring:url var="uploadUrl" value="/secured/policymgmt/setup/uploadfile" />
<spring:url var="refreshCompanyUrl" value="/secured/policymgmt/setup/refresh/company"/>
<spring:message var="maintPeriodYear" code="policymgmt.setup.maintPeriod.year"/>
<spring:message var="maintPeriodMonth" code="policymgmt.setup.maintPeriod.month"/>
<spring:message var="maintPeriodDay" code="policymgmt.setup.maintPeriod.day"/>
<spring:message var="delete" code="policymgmt.setup.delete" />

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

.table-width-endorsement-no {
    max-width: 5%;
    width: 3%;
}

.table-width-endorsement-desc {
    max-width: 30%;
    width: 20%;
}

.table-width-endorsement-number {
 	max-width: 12%;
    width: 10%;
}
</style>
<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		$('#premiumSumInsured').keyup(function(e) {
	       var rebate = $('#premiumSumInsured').val();
	       var endosermentSumInsured = $('#endorsementTotalSumInsured').val();
	       var result = parseFloat(rebate.replace(",","")) + parseFloat(endosermentSumInsured.replace(",",""));
	       $('#totalSumInsured').val(result);
	       updateNetPremium();
	    })
	    
		$('#premiumGross').keyup(function(e) {
	       var gross = $('#premiumGross').val();
	       var endosermentGross = $('#endorsementTotalGross').val();
	       var result = parseFloat(gross.replace(",","")) + parseFloat(endosermentGross.replace(",",""));
	       $('#totalGross').val(result);
	       updateNetPremium();
	    })
	   
		$('#premiumRebate').keyup(function(e) {
	       var rebate = $('#premiumRebate').val();
	       var endosermentRebate = $('#endorsementTotalRebate').val();
	       var result = parseFloat(rebate.replace(",","")) + parseFloat(endosermentRebate.replace(",",""));
	       $('#totalRebate').val(result);
	       updateNetPremium();
	    })
        	
        $('#premiumTax').keyup(function(e) {
	       var tax = checkNullNumber($('#premiumTax').val());
	       var endosermentTax = checkNullNumber($('#endorsementTotalTax').val());
	       var result = tax + endosermentTax;
	       $('#totalTax').val(result);
	       updateNetPremium();
	    })
        	
        $('#stampDuty').keyup(function(e) {
	       var stampDuty = checkNullNumber($('#stampDuty').val());
	       var endosermentStampDuty = checkNullNumber($('#endorsementTotalStampDuty').val());
	       var result = stampDuty + endosermentStampDuty;
	       $('#totalStampDuty').val(result);
	       updateNetPremium();
	    })
	   
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
	    
	
		$("input#interesInsuredAddRow").click(function() {
			var idx = $("#interestInsuredTable").find("input[id$='\\.order']").length;
			var html = "<tr><td><input id='policy.interestInsuredList" + idx + ".order' name='policy.interestInsuredList[" + idx + "].order' readonly='readonly' type='text' class='form-control input-sm' value='" + (idx + 1) + "'/></td>" +
			"<td><textarea id='policy.interestInsuredList" + idx +".description' name='policy.interestInsuredList[" + idx + "].description' class='form-control input-sm' rows='3'></textarea></td>" +
			"<td><input id='policy.interestInsuredList" + idx + ".sumCovered' name='policy.interestInsuredList[" + idx + "].sumCovered' class='form-control input-sm' type='text'/></td>" +
			"<td><a id='dInterestInsured" + idx + "' href='#' onclick='deleteInterestInsuredRow(this)'><span class='glyphicon glyphicon-trash'/></a></td</tr>";			
			$("#interestInsuredTable tbody>tr:nth-last-child(1)").before(html);
	
			$("#interestInsuredTable").find("input[id$='policy.interestInsuredList" + idx + ".sumCovered']").number( true, 2 );
		});		      	

		$("input#excessDeductibleAddRow").click(function() {
			var idx = $("#excessDeductibleTable").find("input[id$='\\.order']").length;
			var html = "<tr><td><input id='policy.excessDeductibleList" + idx + ".order' name='policy.excessDeductibleList[" + idx + "].order' readonly='readonly' type='text' class='form-control input-sm' value='" + (idx + 1) + "'/></td>" +
			"<td><textarea id='policy.excessDeductibleList" + idx +".description' name='policy.excessDeductibleList[" + idx + "].description' class='form-control input-sm' rows='3'></textarea></td>" +
			"<td><input id='policy.excessDeductibleList" + idx + ".amount' name='policy.excessDeductibleList[" + idx + "].amount' class='form-control input-sm' type='text'/></td>" +
			"<td><a id='dExcessDeductible" + idx + "' href='#' onclick='deleteExcessDeductibleRow(this)'><span class='glyphicon glyphicon-trash'/></a></td</tr>";			
			$("#excessDeductibleTable tbody>tr:nth-last-child(1)").before(html);

			$("#excessDeductibleTable").find("input[id$='policy.excessDeductibleList" + idx + ".amount']").number( true, 2 );
		});		      	
		
		$("input#endorsementAddRow").click(function() {
  			var idx = $("#endorsementTable").find("input[id$='\\.order']").length;
  			var html = "<tr><td><input id='policy.endorsementList" + idx + ".order' name='policy.endorsementList[" + idx + "].order' readonly='readonly' type='text' class='form-control input-sm' value='" + (idx + 1) + "'/>" + 
  			"<input id='policy.endorsementList" + idx + ".id' name='policy.endorsementList[" + idx + "].id' type='hidden'/></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".endorsmentNo' name='policy.endorsementList[" + idx + "].endorsmentNo' class='form-control input-sm' type='text'/></td>" +
  			"<td><textarea id='policy.endorsementList" + idx +".description' name='policy.endorsementList[" + idx + "].description' class='form-control input-sm' rows='4'></textarea></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".sumInsured' name='policy.endorsementList[" + idx + "].sumInsured' class='form-control input-sm' type='text' onkeyup='updateTotalSumInsuredEndorsment();'/></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".grossPremium' name='policy.endorsementList[" + idx + "].grossPremium' class='form-control input-sm' type='text' onkeyup='updateTotalGrossEndorsment();'/></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".rebatePremium' name='policy.endorsementList[" + idx + "].rebatePremium' class='form-control input-sm' type='text' onkeyup='updateTotalRebateEndorsment();'/></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".taxAmount' name='policy.endorsementList[" + idx + "].taxAmount' class='form-control input-sm' type='text' onkeyup='updateTotalTaxEndorsment();'/></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".stampDuty' name='policy.endorsementList[" + idx + "].stampDuty' class='form-control input-sm' type='text' onkeyup='updateTotalStampDutyEndorsment();'/></td>" +
  			"<td><input id='policy.endorsementList" + idx + ".netPremium' name='policy.endorsementList[" + idx + "].netPremium' readonly='readonly' class='form-control input-sm' type='text'/></td>" +
  			"<td><a id='dEndorsement" + idx + "' href='#' onclick='deleteEndorsementRow(this)'><span class='glyphicon glyphicon-trash'></span></a></td</tr>";			
  			$("#endorsementTable tbody>tr:nth-last-child(2)").before(html);
  			
  			$("#endorsementTable").find("input[id$='policy.endorsementList" + idx + ".sumInsured']").number( true, 2 );
  			$("#endorsementTable").find("input[id$='policy.endorsementList" + idx + ".grossPremium']").number( true, 2 );
  			$("#endorsementTable").find("input[id$='policy.endorsementList" + idx + ".rebatePremium']").number( true, 2 );
  			$("#endorsementTable").find("input[id$='policy.endorsementList" + idx + ".taxAmount']").number( true, 2 );
  			$("#endorsementTable").find("input[id$='policy.endorsementList" + idx + ".stampDuty']").number( true, 2 );
  			$("#endorsementTable").find("input[id$='policy.endorsementList" + idx + ".netPremium']").number( true, 2 );
		});
		
		$("#interestInsuredTable").find("input[id$='\\.sumCovered']").each(function() {
			$(this).number( true, 2 );
		});

		$("#excessDeductibleTable").find("input[id$='\\.amount']").each(function() {
			$(this).number( true, 2 );
		});

		$("#endorsementTable").find("input[id$='\\.sumInsured']").each(function() {
			$(this).number( true, 2 );
		});

		$("#endorsementTable").find("input[id$='\\.grossPremium']").each(function() {
			$(this).number( true, 2 );
		});

		$("#endorsementTable").find("input[id$='\\.rebatePremium']").each(function() {
			$(this).number( true, 2 );
		});

		$("#endorsementTable").find("input[id$='\\.taxAmount']").each(function() {
			$(this).number( true, 2 );
		});

		$("#endorsementTable").find("input[id$='\\.stampDuty']").each(function() {
			$(this).number( true, 2 );
		});

		$("#endorsementTable").find("input[id$='\\.netPremium']").each(function() {
			$(this).number( true, 2 );
		});
		
		$("#sumInsured").number( true, 2 );
		$("#premiumGross").number( true, 2 );
		$("#premiumRebate").number( true, 2 );
		$("#premiumTax").number( true, 2 );
		$("#premiumNet").number( true, 2 );
		$("#stampDuty").number( true, 2 );
		
		$("#endorsementTotalSumInsured").number( true, 2 );
		$("#endorsementTotalGross").number( true, 2 );
		$("#endorsementTotalRebate").number( true, 2 );
		$("#endorsementTotalTax").number( true, 2 );
		$("#endorsementTotalNet").number( true, 2 );
		$("#endorsementTotalStampDuty").number( true, 2 );

		$("#totalSumInsured").number( true, 2 );
		$("#totalGross").number( true, 2 );
		$("#totalRebate").number( true, 2 );
		$("#totalTax").number( true, 2 );
		$("#totalNet").number( true, 2 );
		$("#totalStampDuty").number( true, 2 );

		$('#endorsementTabTotalSumInsured').number( true, 2 );
        $('#endorsementTabTotalGross').number( true, 2 );
        $('#endorsementTabTotalRebate').number( true, 2 );
        $('#endorsementTabTotalTax').number( true, 2 );
        $('#endorsementTabTotalNet').number( true, 2 );
        $('#endorsementTabTotalStampDuty').number( true, 2 );
	});

	function searchViaAjax() {
		var search = {}
		search["companyId"] = $("#companyId").val();
		enableCompanyDropdown(false);
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "${refreshCompanyUrl}",
			data : JSON.stringify(search),
			dataType : 'json',
			timeout : 100000,
			success : function(data) {
				console.log("SUCCESS: ", data);
				displayCompanyInfo(data);
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
				enableCompanyDropdown(true);
			}
		});
	}

	function displayCompanyInfo(data) {
		$('#contactPerson').val(data.contactPerson);		
		$('#telNo').val(data.telNo);
		$('#faxNo').val(data.faxNo);
		$('#address1').val(data.address1);
		$('#address2').val(data.address2);
		$('#address3').val(data.address3);
		$('#city').val(data.city);
		$('#postcode').val(data.postcode);
		$('#state').val(data.state);
	}
	
	function enableCompanyDropdown(flag) {		
		 $("#companyId").prop("disabled", flag);
	}

	function deleteInterestInsuredRow(source) {		
		$(source).parent().parent().remove();
		var idx = 1;
		$("#interestInsuredTable").find("input[id$='\\.order']").each(function() {
			$(this).val(idx++);
		});

		idx = 0;
		$("#interestInsuredTable").find("input[id$='\\.id']").each(function() {			
			$(this).attr("id", "policy.interestInsuredList" + idx + ".id");
			$(this).attr("name", "policy.interestInsuredList[" + idx + "].id");
			idx++;
		});
		
		idx = 0;
		$("#interestInsuredTable").find("input[id$='\\.order']").each(function() {			
			$(this).attr("id", "policy.interestInsuredList" + idx + ".order");
			$(this).attr("name", "policy.interestInsuredList[" + idx + "].order");
			idx++;
		});
		
		idx = 0;
		$("#interestInsuredTable").find("textarea[id$='\\.description']").each(function() {			
			$(this).attr("id", "policy.interestInsuredList" + idx + ".description");
			$(this).attr("name", "policy.interestInsuredList[" + idx + "].description");
			idx++;
		});
		
		idx = 0;
		$("#interestInsuredTable").find("input[id$='\\.sumCovered']").each(function() {			
			$(this).attr("id", "policy.interestInsuredList" + idx + ".sumCovered");
			$(this).attr("name", "policy.interestInsuredList[" + idx + "].sumCovered");
			idx++;
		});
		
		idx = 0;
		$("#interestInsuredTable").find("a[id^='dInterestInsured']").each(function() {			
			$(this).attr("id", "dInterestInsured" + idx);
			idx++;
		});
	}

	function deleteExcessDeductibleRow(source) {		
		$(source).parent().parent().remove();
		var idx = 1;
		$("#excessDeductibleTable").find("input[id$='\\.order']").each(function() {
			$(this).val(idx++);
		});

		idx = 0;
		$("#excessDeductibleTable").find("input[id$='\\.id']").each(function() {			
			$(this).attr("id", "policy.excessDeductibleList" + idx + ".id");
			$(this).attr("name", "policy.excessDeductibleList[" + idx + "].id");
			idx++;
		});
		
		idx = 0;
		$("#excessDeductibleTable").find("input[id$='\\.order']").each(function() {			
			$(this).attr("id", "policy.excessDeductibleList" + idx + ".order");
			$(this).attr("name", "policy.excessDeductibleList[" + idx + "].order");
			idx++;
		});
		
		idx = 0;
		$("#excessDeductibleTable").find("textarea[id$='\\.description']").each(function() {			
			$(this).attr("id", "policy.excessDeductibleList" + idx + ".description");
			$(this).attr("name", "policy.excessDeductibleList[" + idx + "].description");
			idx++;
		});
		
		idx = 0;
		$("#excessDeductibleTable").find("input[id$='\\.amount']").each(function() {			
			$(this).attr("id", "policy.excessDeductibleList" + idx + ".amount");
			$(this).attr("name", "policy.excessDeductibleList[" + idx + "].amount");
			idx++;
		});
		
		idx = 0;
		$("#excessDeductibleTable").find("a[id^='dExcessDeductible']").each(function() {			
			$(this).attr("id", "dExcessDeductible" + idx);
			idx++;
		});
	}
	
	function deleteFile(obj){
		var str = obj.id; 
	    var res = str.substr(9, 6);
	    var idx = res.split('.');
		var objID = $("input[id$='filesList" + idx[0] + "\\.rpid']").attr('id');
		var idr = $("input[id='" + objID + "']").val();
		var url = '<spring:url value="/secured/policymgmt/setup/deletefile"/>/${url_param_prefix}/' + idr;
		obj.href = url;
		return true;	
    }
    
	function downloadFile(obj){
		var str = obj.id; 
	    var res = str.substr(9, 6);
	    var idx = res.split('.');
		var objID = $("input[id$='filesList" + idx[0] + "\\.rpid']").attr('id');
		var idr = $("input[id='" + objID + "']").val();
		window.open('${pageContext.request.contextPath}/secured/policymgmt/setup/downloadfile/${url_param_prefix}/'+ idr +'/', '_blank'); 
	};
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-12">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="policymgmt.setup.newPolicy" /></div>
				<form:form commandName="policySetupForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<ul class="nav nav-tabs">
				 		<li class="active">
				 			<a data-toggle="tab" href="#tab-policy"><spring:message code="policymgmt.setup.policy" /></a>
				 		</li>
				 		<li>
				 			<a data-toggle="tab" href="#tab-interestInsured"><spring:message code="policymgmt.setup.interestInsured" /></a>
				 		</li>
				 		<li>
				 			<a data-toggle="tab" href="#tab-excessList"><spring:message code="policymgmt.setup.excessList" /></a>
				 		</li>
				 		<li>
				 			<a data-toggle="tab" href="#tab-endorsement"><spring:message code="policymgmt.setup.endorsments" /></a>
				 		</li>
				 		<li>
				 			<a data-toggle="tab" href="#tab-attachment"><spring:message code="label.attachment" /></a>
				 		</li>
					</ul>
					<div class="tab-content">
						<div id="tab-policy" class="tab-pane fade in active panel-body">
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="policyNo">
		  							<spring:message code="policymgmt.setup.policyNo" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="policy.policyNo" class="form-control input-sm" id="policyNo"/>
		   						</span>
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="insuranceClass">
		  							<spring:message code="policymgmt.setup.insuranceClassCode" /><span class="mandatory">*</span>
		  						</label>   						
		  						<span class="col-sm-3">
		  							<form:select path="policy.insuranceClass.code" class="form-control input-sm" id="insuranceClass">
										<form:option label="Select" value="" />
										<form:options items="${policySetupForm.insuranceClasses}" itemLabel="dropdownLabel" itemValue="code" />
									</form:select>
		   						</span>
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="insurerName">
									<spring:message code="policymgmt.setup.insurerName" /><span class="mandatory">*</span>
								</label>
								<span class="col-sm-9">
		  							<form:select path="policy.insurer.code" class="form-control input-sm" id="insurerName">
										<form:option label="Select" value="" />
										<form:options items="${policySetupForm.insurers}" itemLabel="dropdownLabel" itemValue="code" />
									</form:select>
								</span>
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="companyId">
									<spring:message code="policymgmt.setup.companyName" /><span class="mandatory">*</span>
								</label>
								<span class="col-sm-9">
		  							<form:select path="policy.company.id" class="form-control input-sm" id="companyId" onchange="searchViaAjax();">
										<form:option label="Select" value="" />
										<form:options items="${policySetupForm.companies}" itemLabel="name" itemValue="id" />
									</form:select>
								</span>
							</div>					
							<div class="form-group row">
								<label class="control-label col-sm-2" for="contactPerson">
									<spring:message code="policymgmt.setup.contactPerson" />
								</label>
								<span class="col-sm-9">
		   							<form:input path="policy.company.contact.contactPerson" class="form-control input-sm" id="contactPerson" readonly="true"/>
		   						</span>						
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="telNo">
									<spring:message code="policymgmt.setup.telNo" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="policy.company.contact.telNo" class="form-control input-sm" id="telNo" readonly="true"/>
		   						</span>						
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="faxNo">
		  							<spring:message code="policymgmt.setup.faxNo"/>
		  						</label>   						
		  						<span class="col-sm-3">
		   							<form:input path="policy.company.contact.faxNo" class="form-control input-sm" id="faxNo" readonly="true"/>
		   						</span>   						
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="address1">
									<spring:message code="policymgmt.setup.address1" />
								</label>
								<span class="col-sm-9">
		   							<form:input path="policy.company.contact.address1" class="form-control input-sm" id="address1" readonly="true"/>
		   						</span>						
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="address2">
									<spring:message code="policymgmt.setup.address2" />
								</label>
								<span class="col-sm-9">
		   							<form:input path="policy.company.contact.address2" class="form-control input-sm" id="address2" readonly="true"/>
		   						</span>						
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="address3">
									<spring:message code="policymgmt.setup.address3" />
								</label>
								<span class="col-sm-9">
		   							<form:input path="policy.company.contact.address3" class="form-control input-sm" id="address3" readonly="true"/>
		   						</span>						
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="city">
									<spring:message code="policymgmt.setup.city" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="policy.company.contact.city" class="form-control input-sm" id="city" readonly="true"/>
		   						</span>
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="postcode">
		  							<spring:message code="policymgmt.setup.postcode"/>
		  						</label>   						
		  						<span class="col-sm-3">
		   							<form:input path="policy.company.contact.postcode" class="form-control input-sm" id="postcode" readonly="true"/>
		   						</span>   												
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="state">
		  							<spring:message code="policymgmt.setup.state" />
		  						</label>
		  						<span class="col-sm-3">
		   							<form:input path="policy.company.contact.state.name" class="form-control input-sm" id="state" readonly="true"/>
		   						</span>
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="insuredContent">
		  							<spring:message code="policymgmt.setup.insuredContent" /><span class="mandatory">*</span>
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="policy.insuredContent" class="form-control input-sm" rows="6" id="insuredContent"/>
		   						</span>					
							</div>
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="riskLocation">
		  							<spring:message code="policymgmt.setup.riskLocation" />
		  						</label>
		  						<span class="col-sm-9">
		   							<form:textarea path="policy.riskLocation" class="form-control input-sm" rows="6" id="riskLocation"/>
		   						</span>					
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="startDate">
									<spring:message code="policymgmt.setup.startDate" /><span class="mandatory">*</span>
								</label>
								<span class="col-sm-3">
		   							<form:input path="policy.startDate" class="form-control input-sm" id="startDate"/>
		   						</span>
		  						<span class="col-sm-1"></span>
		  						<label class="control-label col-sm-2" for="endDate">
		  							<spring:message code="policymgmt.setup.endDate"/>
		  						</label>   						
		  						<div class="col-sm-3">
									<form:input path="policy.endDate" class="form-control input-sm" id="endDate"/>
		   						</div>   												
							</div>
							<div class="form-group row">
								<label class="control-label col-sm-2" for="maintPeriod">
									<spring:message code="policymgmt.setup.maintPeriod" />
								</label>
								<span class="col-sm-2">
		   							<form:input path="policy.maintPeriod" class="form-control input-sm" id="maintPeriod"/>
		   						</span>
								<span class="col-sm-2">
		   							<form:select path="policy.maintPeriodUnit" class="form-control input-sm" id="maintPeriodUnit">
										<form:option label="Select" value="" />
										<form:option label="${maintPeriodYear}" value="YEAR" />
										<form:option label="${maintPeriodMonth}" value="MONTH" />
										<form:option label="${maintPeriodDay}" value="DAY" />
									</form:select>   							
			   					</span>
			   				</div>
							<div class="form-group row">
			   					<span class="col-sm-2"></span>
			   					<label class="control-label col-sm-3" style="text-align: center">
									<spring:message code="policymgmt.setup.policy" />
								</label>
								<label class="control-label col-sm-3" style="text-align: center">
									<spring:message code="policymgmt.setup.endorsments" />
								</label>
								<label class="control-label col-sm-3" style="text-align: center">
									<spring:message code="policymgmt.setup.total" />
								</label>
								<span class="col-sm-2"></span>
			   				</div>
							
							<div class="form-group row">
								<label class="control-label col-sm-2" for="sumInsured">
									<spring:message code="policymgmt.setup.sumInsured" />
								</label>
								<span class="col-sm-3">
		   							<form:input path="policy.sumInsured" class="form-control input-sm" id="sumInsured" readonly="true"/>
		   						</span>
								<span class="col-sm-3">
		   							<form:input path="endorsementTotalSumInsured" class="form-control input-sm" id="endorsementTotalSumInsured" readonly="true"/>
		   						</span>	
								<span class="col-sm-3">
		   							<form:input path="totalSumInsured" class="form-control input-sm" id="totalSumInsured" readonly="true"/>
		   						</span>										
			   				</div>
			   				
		  					<div class="row" style="margin-top: 30px"></div>
			   				<div class="row">
			   					<label class="col-sm-1">
									<spring:message code="policymgmt.setup.subTitle.premium" />
								</label>											
			   				</div>
			   				<hr style="margin-top: auto;">
			   				
			   				<div class="form-group row">
			   					<span class="col-sm-2"></span>
			   					<label class="control-label col-sm-3" style="text-align: center">
									<spring:message code="policymgmt.setup.policy" />
								</label>
								<label class="control-label col-sm-3" style="text-align: center">
									<spring:message code="policymgmt.setup.endorsments" />
								</label>
								<label class="control-label col-sm-3" style="text-align: center">
									<spring:message code="policymgmt.setup.total" />
								</label>
								<span class="col-sm-2"></span>
			   				</div>
			   				
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="premiumGross">
									<spring:message code="policymgmt.setup.grossPremium" />
		  						</label>   						
		  						<div class="col-sm-3">
									<form:input path="policy.premiumGross" class="form-control input-sm" id="premiumGross"/>
		   						</div>   
		  						<span class="col-sm-3">
		   							<form:input path="endorsementTotalGross" class="form-control input-sm" id="endorsementTotalGross" readonly="true"/>
		   						</span>						
		  						<div class="col-sm-3">
									<form:input path="totalGross" class="form-control input-sm" id="totalGross" readonly="true"/>
		   						</div>   												
			   				</div>
			   				
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="premiumRebate">
									<spring:message code="policymgmt.setup.rebatePremium" />
		  						</label>   						
		  						<div class="col-sm-3">
									<form:input path="policy.premiumRebate" class="form-control input-sm" id="premiumRebate"/>
		   						</div>
		  						<span class="col-sm-3">
		   							<form:input path="endorsementTotalRebate" class="form-control input-sm" id="endorsementTotalRebate" readonly="true"/>
		   						</span>						
		  						<div class="col-sm-3">
									<form:input path="totalRebate" class="form-control input-sm" id="totalRebate" readonly="true"/>
		   						</div>   		  												
			   				</div>
			   				
			   				<div class="form-group row">
		  						<label class="control-label col-sm-2" for="premiumTax">
									<spring:message code="policymgmt.setup.taxAmount" />
		  						</label>   						
		  						<div class="col-sm-3">
									<form:input path="policy.premiumTax" class="form-control input-sm" id="premiumTax"/>
		   						</div>
		  						<span class="col-sm-3">
		   							<form:input path="endorsementTotalTax" class="form-control input-sm" id="endorsementTotalTax" readonly="true"/>
		   						</span>						
		  						<div class="col-sm-3">
									<form:input path="totalTax" class="form-control input-sm" id="totalTax" readonly="true"/>
		   						</div>   		  												
			   				</div>
			   				
			   				<div class="form-group row">
		  						<label class="control-label col-sm-2" for="stampDuty">
									<spring:message code="policymgmt.setup.stampDuty" />
		  						</label>   						
		  						<div class="col-sm-3">
									<form:input path="policy.stampDuty" class="form-control input-sm" id="stampDuty"/>
		   						</div>
		  						<span class="col-sm-3">
		   							<form:input path="endorsementTotalStampDuty" class="form-control input-sm" id="endorsementTotalStampDuty" readonly="true"/>
		   						</span>						
		  						<div class="col-sm-3">
									<form:input path="totalStampDuty" class="form-control input-sm" id="totalStampDuty" readonly="true"/>
		   						</div>   		  												
			   				</div>
			   				
							<div class="form-group row">
		  						<label class="control-label col-sm-2" for="premiumNet">
									<spring:message code="policymgmt.setup.netPremium" />
		  						</label>   						
		  						<div class="col-sm-3">
									<form:input path="policy.premiumNet" class="form-control input-sm" id="premiumNet" readonly="true"/>
		   						</div>
		  						<span class="col-sm-3">
		   							<form:input path="endorsementTotalNet" class="form-control input-sm" id="endorsementTotalNet" readonly="true"/>
		   						</span>			
		  						<div class="col-sm-3">
									<form:input path="totalNet" class="form-control input-sm" id="totalNet" readonly="true"/>
		   						</div>   												
			   				</div>
						</div>
						
						<div id="tab-interestInsured" class="tab-pane fade panel-body">
							<div class="form-group row">
		  						<span class="col-sm-1"></span>
		  						<div class="col-sm-10">
									<table id="interestInsuredTable" class="table table-bordered table-hover">
										<thead>
											<tr >
												<th class="text-center col-sm-1"><spring:message code="policymgmt.setup.no" /></th>
												<th class="text-center col-sm-6"><spring:message code="policymgmt.setup.description" /></th>
												<th class="text-center col-sm-2"><spring:message code="policymgmt.setup.sumCovered" /></th>
												<th class="text-center col-sm-1"></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="interestInsuredItem" varStatus="i" begin="0" items="${policySetupForm.policy.interestInsuredList}">
												<tr>
													<td><form:input path="policy.interestInsuredList[${i.index}].order" class="form-control input-sm" id="policy.interestInsuredList${i.index}.order" readonly="true"></form:input></td>
													<td><form:textarea rows="3" path="policy.interestInsuredList[${i.index}].description" class="form-control input-sm" id="policy.interestInsuredList${i.index}.description"></form:textarea></td>
													<td><form:input path="policy.interestInsuredList[${i.index}].sumCovered" class="form-control input-sm" id="policy.interestInsuredList${i.index}.sumCovered"></form:input></td>
													<td><a id='dInterestInsured${i.index}' href='#' onclick='deleteInterestInsuredRow(this)'><spring:message code="policymgmt.setup.delete"/></a></td>
												</tr>
											</c:forEach>
											<tr>
						                    	<td colspan="4"	align="center"><input id="interesInsuredAddRow" type="button" class="form-control input-sm" value="<spring:message code="policymgmt.setup.addNewRow" />"/></td>
						                    </tr>
										</tbody>										
									</table>
								</div>
		  						<span class="col-sm-1"></span>
							</div>												
						</div>
						<div id="tab-excessList" class="tab-pane fade panel-body">
							<div class="form-group row">
		  						<span class="col-sm-1"></span>
		  						<div class="col-sm-10">
									<table id="excessDeductibleTable" class="table table-bordered table-hover">
										<thead>
											<tr >
												<th class="text-center col-sm-1"><spring:message code="policymgmt.setup.no" /></th>
												<th class="text-center col-sm-6"><spring:message code="policymgmt.setup.description" /></th>
												<th class="text-center col-sm-2"><spring:message code="policymgmt.setup.excessAmount" /></th>
												<th class="text-center col-sm-1"></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="excessDeductibleItem" varStatus="i" begin="0" items="${policySetupForm.policy.excessDeductibleList}">
												<tr>
													<td><form:input path="policy.excessDeductibleList[${i.index}].order" class="form-control input-sm" id="policy.excessDeductibleList${i.index}.order" readonly="true"/></td>
													<td><form:textarea rows="3" path="policy.excessDeductibleList[${i.index}].description" class="form-control input-sm" id="policy.excessDeductibleList${i.index}.description"></form:textarea></td>
													<td><form:input path="policy.excessDeductibleList[${i.index}].amount" class="form-control input-sm" id="policy.excessDeductibleList${i.index}.amount"/></td>
													<td><a id='dExcessDeductible${i.index}' href='#' onclick='deleteExcessDeductibleRow(this)'><spring:message code="policymgmt.setup.delete"/></a></td>
												</tr>
											</c:forEach>
											<tr>
						                    	<td colspan="4"	align="center"><input id="excessDeductibleAddRow" type="button" class="form-control input-sm" value="<spring:message code="policymgmt.setup.addNewRow" />"/></td>
						                    </tr>
										</tbody>										
									</table>
								</div>
		  						<span class="col-sm-1"></span>
							</div>
						</div>
						<div id="tab-attachment" class="tab-pane fade in panel-body">
							<div class="form-group row">
								<div class="col-sm-12">
									<div id="kv-avatar-errors-1" class="center-block col-sm-12"></div>
									<table id="attachmentTable" class="table table-bordered table-hover">
										<thead>
											<tr>
												<th class="text-center col-sm-1"><spring:message code="claim.no" /></th>
												<th class="text-center col-sm-3"><spring:message code="label.name" /></th>
												<th class="text-center col-sm-1"></th>
												<th class="text-center col-sm-1"></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="policyFile" varStatus="i" begin="0" items="${policySetupForm.filesList}">
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
											<tr>
												<input id="file" name="attachment" type="file" class="file-loading" multiple />
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						<div id="tab-endorsement" class="tab-pane fade in panel-body">
						    <div class="form-group row">
								<span class="col-sm-1"></span>
									<div class="col-sm-12">
									<table id="endorsementTable" class="table table-bordered table-hover">
										<thead>
											<tr >
												<th class="text-center table-width-endorsement-no"><spring:message code="policymgmt.setup.no" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.endorsementNo" /></th>
												<th class="text-center table-width-endorsement-desc"><spring:message code="policymgmt.setup.description" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.sumInsured" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.grossPremium" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.rebatePremium" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.taxAmount" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.stampDuty" /></th>
												<th class="text-center table-width-endorsement-number"><spring:message code="policymgmt.setup.netPremium" /></th>
												<th class="text-center table-width-endorsement-no"></th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="interestInsuredItem" varStatus="i" begin="0" items="${policySetupForm.policy.endorsementList}">
											<tr>
												<td>
													<form:input path="policy.endorsementList[${i.index}].order" class="form-control input-sm" id="policy.endorsementList${i.index}.order" readonly="true"/>
													<form:hidden path="policy.endorsementList[${i.index}].id" id="policy.endorsementList${i.index}.id" />
												</td>
								                <td><form:input path="policy.endorsementList[${i.index}].endorsmentNo" class="form-control input-sm" id="policy.endorsementList${i.index}.endorsmentNo"/></td>
								                <td><form:textarea rows="4" path="policy.endorsementList[${i.index}].description" class="form-control input-sm" id="policy.endorsementList${i.index}.description"></form:textarea></td>
								                <td><form:input path="policy.endorsementList[${i.index}].sumInsured" class="form-control input-sm" id="policy.endorsementList${i.index}.sumInsured" onkeyup="updateTotalSumInsuredEndorsment();"/></td>
								                <td><form:input path="policy.endorsementList[${i.index}].grossPremium" class="form-control input-sm" id="policy.endorsementList${i.index}.grossPremium" onkeyup="updateTotalGrossEndorsment();"/></td>
								                <td><form:input path="policy.endorsementList[${i.index}].rebatePremium" class="form-control input-sm" id="policy.endorsementList${i.index}.rebatePremium" onkeyup="updateTotalRebateEndorsment();"/></td>
								                <td><form:input path="policy.endorsementList[${i.index}].taxAmount" class="form-control input-sm" id="policy.endorsementList${i.index}.taxAmount" onkeyup="updateTotalTaxEndorsment();"/></td>
								                <td><form:input path="policy.endorsementList[${i.index}].stampDuty" class="form-control input-sm" id="policy.endorsementList${i.index}.stampDuty" onkeyup="updateTotalStampDutyEndorsment();"/></td>
								                <td><form:input path="policy.endorsementList[${i.index}].netPremium" class="form-control input-sm" id="policy.endorsementList${i.index}.netPremium" onkeyup="updateTotalNetEndorsment();" readOnly="true"/></td>
								                <td><a id='dEndorsement${i.index}' href='#' onclick='deleteEndorsementRow(this)'><span class="glyphicon glyphicon-trash"></span></a></td>
											</tr>
											</c:forEach>
											<tr>
								            	<td colspan="3" align="right" style="vertical-align: middle; font-weight: bold;"><spring:message code="policymgmt.setup.total"/></td>
								            	<td colspan="1"	align="right"><form:input path="endorsementTotalSumInsured" class="form-control input-sm" id="endorsementTabTotalSumInsured" readOnly="true"/></td>
								            	<td colspan="1"	align="right"><form:input path="endorsementTotalGross" class="form-control input-sm" id="endorsementTabTotalGross" readOnly="true"/></td>
								            	<td colspan="1"	align="right"><form:input path="endorsementTotalRebate" class="form-control input-sm" id="endorsementTabTotalRebate" readOnly="true"/></td>
								            	<td colspan="1"	align="right"><form:input path="endorsementTotalTax" class="form-control input-sm" id="endorsementTabTotalTax" readOnly="true"/></td>
								            	<td colspan="1"	align="right"><form:input path="endorsementTotalStampDuty" class="form-control input-sm" id="endorsementTabTotalStampDuty" readOnly="true"/></td>
								            	<td colspan="1"	align="right"><form:input path="endorsementTotalNet" class="form-control input-sm" id="endorsementTabTotalNet" readOnly="true"/></td>
								            	<td colspan="1"	align="right"></td>
								            </tr>
											<tr>
								            	<td colspan="10"	align="center"><input id="endorsementAddRow" type="button" class="form-control input-sm" value="<spring:message code="policymgmt.setup.addNewRow" />"/></td>
								            </tr>
										</tbody>										
									</table>
								</div>
							<span class="col-sm-1"></span>
							</div>
						</div>	
					</div>
				</div>
				<div class="panel-footer" align="right">
					<input type="submit" class="btn btn-primary" 
						value="<spring:message code="button.submit" />" />
					 <a href="${backUrl}" class="btn btn-primary"><spring:message code="button.back" /></a>
				</div>										
				</form:form>
			</div>
		</div>
	</div>				
</div>

<script type="text/javascript" src="<spring:url value="/resources/js/policyEndorsementFunction.js"/>"></script>
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