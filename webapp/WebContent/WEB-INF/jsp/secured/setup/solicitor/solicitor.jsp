<%@ include file="../../../layouts/commontags.jsp"%>

<spring:url var="postUrl" value="/secured/setup/solicitor" />
<spring:url var="gridUrl" value="/secured/setup/solicitor/grid" />

<script type="text/javascript">

	$(document).ready(function() {
		$('#solicitorForm').on('keyup keypress', function(e) {
			  var keyCode = e.keyCode || e.which;
			  if (keyCode === 13) { 
			    e.preventDefault();			   
			    return false;
			  }
		});
	});
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/setup/solicitor/edit/"/>${url_param_prefix}/' + rowObject.id + '\"><spring:message code="label.edit"/></a>';
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

</script>

<div class="class-container">
	<form:form commandName="solicitorForm" action="${postUrl}">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.solicitor.listing" /></div>
				<form:errors element="div" cssClass="errorblock" path="*" />
				
				<div class="panel-body">
					<datatables:table id="solicitorListTableId" cssClass="table table-striped" url="${gridUrl}" 
						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column renderFunction="checkbox" property="id" searchable="false" sortable="false" cssStyle="padding:10px 10px; width=5%">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#solicitorListTableId').find('input:checkbox').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column titleKey="setup.solicitor.firmName" property="firmName" cssStyle="padding:10px 10px; width=45%" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column titleKey="setup.contact.telNo" property="contactDTO.telNo" cssStyle="padding:10px 10px; width=10%" sortInitOrder="1"/>
						<datatables:column titleKey="setup.contact.faxNo" property="contactDTO.faxNo" cssStyle="padding:10px 10px; width=10%" sortInitOrder="2"/>
						<datatables:column titleKey="setup.contact.email" property="contactDTO.email" cssStyle="padding:10px 10px; width=15%" sortInitOrder="3"/>
						<datatables:column titleKey="label.activeFlag" property="activeFlag" renderFunction="activeFlag" cssStyle="padding:10px 10px; width=10%" sortInitOrder="4" searchable="false"/>
						<datatables:column titleKey="label.edit" renderFunction="edit" searchable="false" sortable="false" cssStyle="padding:10px 10px; width=10%"/>
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
</div>