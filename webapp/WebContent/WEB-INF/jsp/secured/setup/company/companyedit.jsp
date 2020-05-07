<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/setup/company" />
<spring:url var="saveUrl" value="/secured/setup/company/edit" />
<spring:url var="departmentGridUrl" value="/secured/setup/company/departmentgrid" />
<spring:url var="departmentEditUrl" value="/secured/setup/company/departmentedit" />
<spring:url var="departmentRemoveUrl" value="/secured/setup/company/departmentremove" />

<script type="text/javascript">
function activeFlag(data) {
	if (data) {
		return '<input type="checkbox" disabled="true" checked="checked">';
	} else {
		return '<input type="checkbox" disabled="true">';
	}
}

function checkbox(cellvalue, options, rowObject) {
	var selectedid = $('#selectedUser').val();
	var selectedIdArray = selectedid.split(',');
	for(count = 0; count < selectedIdArray.length; count++) {
	   var selId = selectedIdArray[count].replace(/^\s*/, "").replace(/\s*$/, "");
	   if (selId == rowObject.userId) {
		   return '<input type="checkbox" name="newUser" value="' + rowObject.userId + '" checked="checked"/>';
	   }
	}
	return '<input type="checkbox" name="newUser" value="' + rowObject.userId + '" />'; 
}

function showDepartmentAddDialog() {
	$('#dStatus').css('display', 'none');
	$('#dStatus').html('');

	$('#dDepartmentCode').prop('readonly', false);
	$('#dDepartmentCode').val('');
	$('#dDepartmentName').val('');

	return false;
}

function showDepartmentEditDialog(code) {
	var form = {}
	form["code"] = code;

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");

	$('#dDepartmentCode').prop('readonly', true);
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "${departmentEditUrl}",
		data : JSON.stringify(form),
		dataType : 'json',
		timeout : 100000,
		beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
		success : function(data) {
			console.log("SUCCESS: ", data);
			$('#dDepartmentCode').val(data.code);
			$('#dDepartmentName').val(data.name);

			if(data.status != null) {
				$('#dStatus').css('display', 'block');
				$('#dStatus').html(data.status);
			}	
			else {
				$('#dStatus').css('display', 'none');
				$('#dStatus').html('');
			}
			
			//jQuery.noConflict();
			$('#departmentDialog').modal('show');		
		},
		error : function(e) {
			console.log("ERROR: ", e);
		},
		done : function(e) {
			console.log("DONE");
		}
	});
}

function editRow(cellvalue, options, rowObject) {
	return "<a href='#' onclick='showDepartmentEditDialog(\"" + rowObject.code + "\")'\><spring:message code="label.edit"/></a>";
}

function deleteDepartment(code) {
	var form = {}
	form["code"] = code;

	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "${departmentRemoveUrl}",
		data : JSON.stringify(form),
		dataType : 'json',
		timeout : 100000,
		beforeSend: function(xhr) {
            xhr.setRequestHeader(header, token);
        },
		success : function(data) {
			console.log("SUCCESS: ", data);
						
			//jQuery.noConflict();
			oTable_departmentgrid.ajax.reload();		
		},
		error : function(e) {
			console.log("ERROR: ", e);
		},
		done : function(e) {
			console.log("DONE");
		}
	});
}

function deleteRow(cellvalue, options, rowObject) {
	return "<a href='#' onclick='deleteDepartment(\"" + rowObject.code + "\")'\><spring:message code="label.delete"/></a>";	
}

$(document).ready(function() {
	$('#companyForm').on('keyup keypress', function(e) {
		  var keyCode = e.keyCode || e.which;
		  if (keyCode === 13) { 
		    e.preventDefault();			   
		    return false;
		  }
	});
	
	$('.userClassHide').hide();
});

</script>

<div class="container-fluid">
	<div class="row">
		<div class="col-sm-1"></div>
			<div class="panel-group col-sm-10">
			<div class="panel panel-primary">
				<div class="panel-heading">
					<spring:message code="setup.company.editCompany" />
				</div>
				<form:form commandName="companyForm" method="post" action="${saveUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
					<div class="panel-body">
						<ul class="nav nav-tabs">
							<li class="active">
								<a data-toggle="tab" href="#tab-CompanyProfile"><spring:message code="setup.company.company" /></a>
							</li>
							<li>
								<a data-toggle="tab" href="#tab-CompanyDepartment"><spring:message code="setup.company.department" /></a>
							</li>							
							<c:if test="${companyForm.permissionFlag}">
								<li>
									<a data-toggle="tab" href="#tab-UserCompany"><spring:message code="setup.company.user" /></a>
								</li>
							</c:if>
						</ul>
						
						<div class="tab-content">
							<%-- Start of company profile tab --%>
							<div id="tab-CompanyProfile" class="tab-pane fade in active panel-body">
								<div class="form-group row">
									<label class="control-label col-sm-2" for="code">
										<spring:message code="setup.company.code" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-4">
										<form:input path="company.code" class="form-control input-sm" id="code" 
											readOnly="true" style="width:100%; text-transform: uppercase"/>
									</span>
									<label class="control-label col-sm-2" for="bizRegNo">
										<spring:message code="setup.company.bizRegNo" />
									</label>
									<span class="col-sm-3">
										<form:input path="company.bizRegNo" class="form-control input-sm" id="bizRegNo"/>
									</span>
								</div>
								<div class="form-group row">
									<label class="control-label col-sm-2" for="name">
										<spring:message code="setup.company.name" /><span class="mandatory">*</span>
									</label>
									<span class="col-sm-9">
										<form:input path="company.name" class="form-control input-sm" id="name"/>
									</span>
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="contactPerson">
			  							<spring:message code="setup.company.contactPerson" />
			  						</label>
			  						<span class="col-sm-4">
			   							<form:input path="company.contact.contactPerson" class="form-control input-sm" id="contactPerson"/>
			   						</span>
			  						<label class="control-label col-sm-2" for="telNo">
			  							<spring:message code="setup.company.telNo" />
			  						</label>
			  						<span class="col-sm-3">
			   							<form:input path="company.contact.telNo" class="form-control input-sm" id="telNo"/>
			   						</span>	
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="email">
			  							<spring:message code="setup.company.email" />
			  						</label>
			  						<span class="col-sm-4">
			   							<form:input path="company.contact.email" class="form-control input-sm" id="email"/>
			   						</span>	   						    							   									
			  						<label class="control-label col-sm-2" for="faxNo">
			  							<spring:message code="setup.company.faxNo" />
			  						</label>
			  						<span class="col-sm-3">
			   							<form:input path="company.contact.faxNo" class="form-control input-sm" id="faxNo"/>
			   						</span>					
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="address1">
			  							<spring:message code="setup.company.address1" />
			  						</label>
			  						<span class="col-sm-5">
			   							<form:input path="company.contact.address1" class="form-control input-sm" id="address1"/>
			   						</span>	   						    							   									
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="address2">
			  							<spring:message code="setup.company.address2" />
			  						</label>
			  						<span class="col-sm-5">
			   							<form:input path="company.contact.address2" class="form-control input-sm" id="address2"/>
			   						</span>	   						    							   									
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="address3">
			  							<spring:message code="setup.company.address3" />
			  						</label>
			  						<span class="col-sm-5">
			   							<form:input path="company.contact.address3" class="form-control input-sm" id="address3"/>
			   						</span>	   						    							   									
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="city">
			  							<spring:message code="setup.company.city" />
			  						</label>
			  						<span class="col-sm-4">
			   							<form:input path="company.contact.city" class="form-control input-sm" id="city"/>
			   						</span>	   				
			   						<label class="control-label col-sm-2" for="postcode">
			  							<spring:message code="setup.company.postcode" />
			  						</label>
			  						<span class="col-sm-2">
			   							<form:input path="company.contact.postcode" class="form-control input-sm" id="postcode"/>
			   						</span>					
								</div>
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="state">
			  							<spring:message code="setup.company.state" />
			  						</label>
			  						<span class="col-sm-4">
			  							<form:select path="company.contact.state.code" class="form-control input-sm" id="state">
											<form:option label="Select" value="" />
											<form:options items="${companyForm.states}" itemLabel="name" itemValue="code" />
										</form:select>
			   						</span>
			   						<label class="control-label col-sm-2" for="activeFlag">
			   							<spring:message code="setup.company.active_flag" />
			   						</label>
			   						<span class="col-sm-2">
										<form:checkbox path="company.active" />
									</span>
			   					</div>	   				
								<div class="form-group row">
			  						<label class="control-label col-sm-2" for="remark">
			  							<spring:message code="setup.company.remark" />
			  						</label>
			  						<span class="col-sm-9">
			   							<form:textarea path="company.remark" class="form-control input-sm" rows="6" id="remark"/>
			   						</span>					
								</div>
							</div>
							<%-- End of company profile tab --%>
														
							<%-- Start of company department tab --%>
							<div id="tab-CompanyDepartment" class="tab-pane fade panel-body">
								<datatables:table id="departmentgrid" cssClass="table table-striped" url="${departmentGridUrl}" pageable="false" filterable="false"
									serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
									<datatables:column property="code" titleKey="setup.company.departmentCode" cssStyle="padding:10px 10px; width:20%" sortInitOrder="0" sortInitDirection="asc"/>
									<datatables:column property="name" titleKey="setup.company.departmentName" cssStyle="padding:10px 10px" sortInitOrder="1"/>
									<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="editRow" cssStyle="padding:10px 10px"/>									
									<datatables:column searchable="false" titleKey="label.delete" sortable="false" renderFunction="deleteRow" cssStyle="padding:10px 10px"/>									
								</datatables:table>
								
								<a href="#" class="btn btn-default" onclick="showDepartmentAddDialog();" data-toggle="modal" data-target="#departmentDialog"><spring:message code="button.new" /></a>
								
							</div>
							<%-- End of company department tab --%>
							
							<%-- Start of user list tab --%>
							<div id="tab-UserCompany" class="tab-pane fade panel-body">
							
								<datatables:table id="usergrid" data="${companyForm.userDTO}" rowIdBase="userId" 
									cssClass="table table-striped" row="row" ext="responsive" autoWidth="false" filterable="false" pageable="false">	
									<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="checkbox">
									  <datatables:columnHead>
									    <input type="checkbox" onclick="$('#usergrid').find('input:checkbox').prop('checked', this.checked);" />
									  </datatables:columnHead>
									</datatables:column>
									<datatables:column property="username" titleKey="usermgmt.user.username" cssStyle="padding:10px 10px" sortInitOrder="0" sortInitDirection="asc"/>
									<datatables:column property="firstName" titleKey="usermgmt.user.firstname" cssStyle="padding:10px 10px" sortInitOrder="1"/>
									<datatables:column property="lastName" titleKey="usermgmt.user.lastname" cssStyle="padding:10px 10px" sortInitOrder="2"/>
									<datatables:column property="userId" titleKey="" cssStyle="padding:10px 10px" sortInitOrder="3" cssCellClass="userClassHide" cssClass="userClassHide"/>
								</datatables:table>
								<form:hidden path="selectedUser" id="selectedUser"/>
							
							</div>
							<%-- End of user list tab --%>
							
						</div>
					</div>
					<div class="panel-footer" align="right">
						<input type="submit" class="btn btn-primary" value="<spring:message code="button.submit" />" /> 
						<a href="${backUrl}" class="btn btn-primary"><spring:message code="button.back" /></a>
					</div>
				</form:form>
			</div>
		</div>
	</div>
</div>

