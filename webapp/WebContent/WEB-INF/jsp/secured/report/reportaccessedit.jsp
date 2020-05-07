<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/report/access" />
<spring:url var="saveUrl" value="/secured/report/access/edit" />
<spring:url var="refreshReportUrl" value="/secured/report/common/refresh/reportdefinition"/>

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {	    

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

</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="report.access.editReportAccess" /></div>
				<form:form commandName="reportAccessForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="category">
  							<spring:message code="report.category" /><span class="mandatory">*</span>
  						</label>
  						<span class="col-sm-4">
  							<form:hidden path="accessControl.id"/>
  							<form:select path="accessControl.definition.category.code" class="form-control input-sm" id="categoryCode" onchange="searchViaAjax();">
								<form:option label="Select" value="" />
								<form:options items="${reportAccessForm.categoryList}" itemLabel="name" itemValue="code" />
							</form:select>
   						</span>
   					</div>
					<div class="form-group row">
  						<label class="control-label col-sm-2" for="report">
  							<spring:message code="report.reportName" /><span class="mandatory">*</span>
  						</label>   						
  						<span class="col-sm-6">
  							<form:select path="accessControl.definition.id" class="form-control input-sm" id="report">
								<form:option label="Select" value="" />
								<form:options items="${reportAccessForm.definitionList}" itemLabel="name" itemValue="id" />
							</form:select>
   						</span>
					</div>
					<div class="form-group row">
						<label class="control-label col-sm-2" for="role">
							<spring:message code="report.role" /><span class="mandatory">*</span>
						</label>
						<span class="col-sm-6">
  							<form:select path="accessControl.role.id" class="form-control input-sm" id="role">
								<form:option label="Select" value="" />
								<form:options items="${reportAccessForm.roleList}" itemLabel="name" itemValue="id" />
							</form:select>
						</span>
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

