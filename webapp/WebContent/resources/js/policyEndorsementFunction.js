	function updateNetPremium(){
		var gross = checkNullNumber($('#premiumGross').val());
	    var rebate = checkNullNumber($('#premiumRebate').val());
	    var tax = checkNullNumber($('#premiumTax').val());
	    var stampDuty = checkNullNumber($('#stampDuty').val());
	    var v = gross - rebate + tax + stampDuty;
	    $('#premiumNet').val(v);
	    var endosermentNet = $('#endorsementTotalNet').val();
	    var totalNet = v + parseFloat(endosermentNet.replace(",",""));
	    $('#totalNet').val(totalNet);
	}
	
	function updateTotalSumInsuredEndorsment(){
		var policySumInsured = checkNullNumber($('#sumInsured').val());
		var totalTabAmount = parseFloat(0);
		var endorsementLength = $("#endorsementTable").find("input[id$='\\.order']").length;
		for (i = 0; i < endorsementLength; i++) {
			var endorsementSumInsured = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".sumInsured']").val());
			totalTabAmount = endorsementSumInsured + totalTabAmount;
		}
		var totalSumInsured = policySumInsured + totalTabAmount;
		$('#endorsementTabTotalSumInsured').val(totalTabAmount);
		$('#endorsementTotalSumInsured').val(totalTabAmount)
		$('#totalSumInsured').val(totalSumInsured);
	}
	
	function updateTotalGrossEndorsment(){
	   var endorsementLength = $("#endorsementTable").find("input[id$='\\.order']").length;
	   var gross = $('#premiumGross').val();
	   var gE = parseFloat(0);
	   for (i = 0; i < endorsementLength; i++) {
	       var grossEndorsementIdx = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".grossPremium']").val());
	       var rebateEndorsementIdx = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".rebatePremium']").val());
	       var taxAmount = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".taxAmount']").val());
	       var endorsementStampDuty = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".stampDuty']").val());
	       gE = grossEndorsementIdx + gE;
	       var amountNetPremium = grossEndorsementIdx - rebateEndorsementIdx + taxAmount + endorsementStampDuty;
	       $("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".netPremium']").val(amountNetPremium);
	   }
	   var totalGross = parseFloat(gross.replace(",","")) + gE;
	   $('#endorsementTabTotalGross').val(gE);
	   $('#endorsementTotalGross').val(gE)
	   $('#totalGross').val(totalGross);
	
	   updateTotalNetEndorsment();
	
	   return true;
	}
	
	function updateTotalRebateEndorsment(){
	   var endorsementLength = $("#endorsementTable").find("input[id$='\\.order']").length;
	   var rebate = $('#premiumRebate').val();
	   var gE = 0;
	   for (i = 0; i < endorsementLength; i++) {
	       var rebateEndorsementIdx = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".rebatePremium']").val());
	       var grossEndorsementIdx = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".grossPremium']").val());
	       var taxAmount = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".taxAmount']").val());
		   var endorsementStampDuty = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".stampDuty']").val());
	       gE = rebateEndorsementIdx + gE;
	       var amountNetPremium = grossEndorsementIdx - rebateEndorsementIdx + taxAmount + endorsementStampDuty;
	       $("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".netPremium']").val(amountNetPremium);
	   }
	   var totalRebate = parseFloat(rebate.replace(",","")) + gE;
	   $('#endorsementTabTotalRebate').val(gE);
	   $('#endorsementTotalRebate').val(gE)
	   $('#totalRebate').val(totalRebate);
	   updateTotalNetEndorsment();
	   return true;
	}
	
	function updateTotalTaxEndorsment(){
		var policyTax = checkNullNumber($('#premiumTax').val());
		var totalTabAmount = parseFloat(0);
		var endorsementLength = $("#endorsementTable").find("input[id$='\\.order']").length;
		for (i = 0; i < endorsementLength; i++) {
			var endorsementGross = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".grossPremium']").val());
		    var endorsementRebate = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".rebatePremium']").val());
		    var endorsementTax = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".taxAmount']").val());
		    var endorsementStampDuty = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".stampDuty']").val());
			totalTabAmount = endorsementTax + totalTabAmount;
			var amountNetPremium = endorsementGross - endorsementRebate + endorsementTax + endorsementStampDuty;
			$("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".netPremium']").val(amountNetPremium);
		}
		var totalTax = policyTax + totalTabAmount;
		$('#endorsementTabTotalTax').val(totalTabAmount);
		$('#endorsementTotalTax').val(totalTabAmount)
		$('#totalTax').val(totalTax);
		updateTotalNetEndorsment();
	}
	
	function updateTotalStampDutyEndorsment(){
		var policyTax = checkNullNumber($('#stampDuty').val());
		var totalTabAmount = parseFloat(0);
		var endorsementLength = $("#endorsementTable").find("input[id$='\\.order']").length;
		for (i = 0; i < endorsementLength; i++) {
			var endorsementGross = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".grossPremium']").val());
		    var endorsementRebate = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".rebatePremium']").val());
		    var endorsementTax = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".taxAmount']").val());
		    var endorsementStampDuty = checkNullNumber($("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".stampDuty']").val());
			totalTabAmount = endorsementStampDuty + totalTabAmount;
			var amountNetPremium = endorsementGross - endorsementRebate + endorsementTax + endorsementStampDuty;
			$("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".netPremium']").val(amountNetPremium);
		}
		var totalTax = policyTax + totalTabAmount;
		$('#endorsementTabTotalStampDuty').val(totalTabAmount);
		$('#endorsementTotalStampDuty').val(totalTabAmount)
		$('#totalStampDuty').val(totalStampDuty);
		updateTotalNetEndorsment();
	}
	
	function updateTotalNetEndorsment(){
	   var endorsementLength = $("#endorsementTable").find("input[id$='\\.order']").length;
	   var net = $('#premiumNet').val();
	   var gE = parseFloat(0);
	   for (i = 0; i < endorsementLength; i++) {
	       var netEndorsementIdx = $("#endorsementTable").find("input[id$='policy.endorsementList" + i + ".netPremium']").val();
	       gE = parseFloat(netEndorsementIdx.replace(",","")) + gE;
	   }
	   var totalNet = parseFloat(net.replace(",","")) + gE;
	   $('#endorsementTabTotalNet').val(gE);
	   $('#endorsementTotalNet').val(gE)
	   $('#totalNet').val(totalNet);
	   
	   return true;
	}
	
	function deleteEndorsementRow(source) {		
		$(source).parent().parent().remove();
		var idx = 1;
		$("#endorsementTable").find("input[id$='\\.order']").each(function() {
			$(this).val(idx++);
		});

		idx = 0;
		$("#endorsementTable").find("input[id$='\\.id']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".id");
			$(this).attr("name", "policy.endorsementList[" + idx + "].id");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.order']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".order");
			$(this).attr("name", "policy.endorsementList[" + idx + "].order");
			idx++;
		});

   		idx = 0;
		$("#endorsementTable").find("input[id$='\\.endorsmentNo']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".endorsmentNo");
			$(this).attr("name", "policy.endorsementList[" + idx + "].endorsmentNo");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("textarea[id$='\\.description']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".description");
			$(this).attr("name", "policy.endorsementList[" + idx + "].description");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.sumInsured']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".sumInsured");
			$(this).attr("name", "policy.endorsementList[" + idx + "].sumInsured");
			idx++;
		});
    
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.grossPremium']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".grossPremium");
			$(this).attr("name", "policy.endorsementList[" + idx + "].grossPremium");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.rebatePremium']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".rebatePremium");
			$(this).attr("name", "policy.endorsementList[" + idx + "].rebatePremium");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.taxAmount']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".taxAmount");
			$(this).attr("name", "policy.endorsementList[" + idx + "].taxAmount");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.stampDuty']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".stampDuty");
			$(this).attr("name", "policy.endorsementList[" + idx + "].stampDuty");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("input[id$='\\.netPremium']").each(function() {			
			$(this).attr("id", "policy.endorsementList" + idx + ".netPremium");
			$(this).attr("name", "policy.endorsementList[" + idx + "].netPremium");
			idx++;
		});
		
		idx = 0;
		$("#endorsementTable").find("a[id^='dEndorsement']").each(function() {			
			$(this).attr("id", "dEndorsement" + idx);
			idx++;
		});

		updateTotalSumInsuredEndorsment();
		updateTotalRebateEndorsment();
		updateTotalGrossEndorsment();
		updateTotalTaxEndorsment();
		updateTotalStampDutyEndorsment();
	}
	
	function checkNullNumber(number){
	   if(number === null || number.length <= 0){
		   number = 0;
       } else{
    	   number = parseFloat(number.replace(",",""));
       }
	   return number;
	}
