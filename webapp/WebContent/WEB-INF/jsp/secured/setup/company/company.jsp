<%@ include file="../../../layouts/commontags.jsp"%>
<spring:url var="backUrl" value="/secured" />
<spring:url var="gridUrl" value="/secured/setup/company/companygrid" />
<spring:url var="postUrl" value="/secured/setup/company" />
<spring:url var="userviewUrl" value="/secured/setup/company/userview" />

<script type="text/javascript">
	
	$(document).ready(function() {
		$('#companyForm').on('keyup keypress', function(e) {
			  var keyCode = e.keyCode || e.which;
			  if (keyCode === 13) { 
			    e.preventDefault();			   
			    return false;
			  }
		});
	});

	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/setup/company/edit/"/>${url_param_prefix}/' + rowObject.id + '\"><spring:message code="label.edit"/></a>';
	}
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" name="selected" value="' + rowObject.id + '" />'; 
	}

	function activeFlag(data) {
		if (data) {
			return '<input type="checkbox" disabled="true" checked="checked">';
		} else {
			return '<input type="checkbox" disabled="true">';
		}
	}
	
	function userList(cellvalue, options, rowObject) {
		return '<a href="#" onclick="showUserList(' + rowObject.id + ');return false;" ><spring:message code="label.view"/></a>';
	}
	
	function showUserList(id) {
		$.ajax({
			type: "GET",
			url: "${userviewUrl}" + "/${url_param_prefix}/" + id,
			data: $("#companyForm").serialize(),
			success: function(data) {	
				$('#userCompany_tbody').empty();
				if(data != null) {
					var dataLen = data.length;
					for (i = 0; i < dataLen; i++) {
						str = 	'<tr><td>'
		    				+ data[i].firstName +
			    			'</td><td>'
			    			+ data[i].userName +
			    			'</td></tr>';
			    			$('#userCompany_tbody').append(str);
					}
				}
				//jQuery.noConflict();
				$('#userModal').modal('show');
			},
		    error: function (textStatus, errorThrown) {
		    	$('#userModal').text("Error getting data");
		    }
		})
	}
</script>

<div class="class-container">
	<form:form commandName="companyForm" action="${postUrl}">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.company.company_listing" /></div>
				<form:errors element="div" cssClass="errorblock" path="*" />
				<div class="panel-body">
					<datatables:table id="myTableId" cssClass="table table-striped" url="${gridUrl}" 
						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="checkbox">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#myTableId').find('input:checkbox').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column property="name" titleKey="setup.company.name" cssStyle="padding:10px 10px" sortInitOrder="1"/>
						<datatables:column property="bizRegNo" titleKey="setup.company.bizRegNo" cssStyle="padding:10px 10px" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column property="contactPerson" titleKey="setup.company.contactPerson" cssStyle="padding:10px 10px" sortInitOrder="2"/>
						<datatables:column property="telNo" titleKey="setup.company.telNo" cssStyle="padding:10px 10px" sortInitOrder="3"/>
						<datatables:column property="faxNo" titleKey="setup.company.faxNo" cssStyle="padding:10px 10px" sortInitOrder="4"/>
						<datatables:column property="email" titleKey="setup.company.email" cssStyle="padding:10px 10px" sortInitOrder="5"/>
						<datatables:column property="active" titleKey="setup.company.active" cssStyle="padding:10px 10px" searchable="false" sortable="false" renderFunction="activeFlag"/>
						<datatables:column titleKey="setup.company.user" cssStyle="padding:10px 10px; width:10%" searchable="false" sortable="false" renderFunction="userList"/>
						<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="edit" cssStyle="padding:10px 10px"/>
					</datatables:table>
				</div>
				<div class="panel-footer" align="right">
					<form:button name="action" value="delete" class="btn btn-primary"><spring:message code="button.delete" /></form:button>
					<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>
				</div>										
			</div>
		</div>
	</div>
	</form:form>
	
		<div id="userModal" class="modal bootstrap-dialog fade" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
			  	<div class="modal-header">
			  		<button type="button" class="close" data-dismiss="modal" aria-hidden="true"><span aria-hidden="true">&times;</span></button>
					<h4><spring:message code="setup.company.userList" /></h4>
				</div>
				<div class="modal-body">            				            				                	 
	               <div class="panel-body">                				                						    					
			    		<div class="form-group row">
			    			 <table id="userCompany" class="table">
		   						<thead>
		       						<th><spring:message code="label.name" /></th>
		       						<th><spring:message code="setup.company.username" /></th>   						      
		   						</thead>
		   						<tbody id="userCompany_tbody">
		   						</tbody>
		 						</table>    								    						
			    		</div>		    							    							    							    							    					
	               </div>                			
            	</div>
				<div class="modal-footer"></div>
			</div>
		</div>
	</div>	
</div>
