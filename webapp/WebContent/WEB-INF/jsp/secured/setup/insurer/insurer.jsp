<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/setup/insurer/edit/"/>${url_param_prefix}/' + rowObject.code + '\"><spring:message code="label.edit"/></a>';
	}
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" value="' + rowObject.code + '" />'; 
	}

	function activeFlag(data) {
		if (data) {
			return '<input type="checkbox" disabled="true" checked="checked">';
		} else {
			return '<input type="checkbox" disabled="true">';
		}
	}
	
</script>
<spring:url var="backUrl" value="/secured" />
<spring:url var="gridUrl" value="/secured/setup/insurer/insurergrid" />
<spring:url var="postUrl" value="/secured/setup/insurer" />

<div class="class-container">
	<form:form commandName="insurerForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.insurer.insurer_listing" /></div>
				<div class="panel-body">
					<datatables:table id="myTableId" cssClass="table table-striped" url="${gridUrl}" 
						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="checkbox">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#myTableId').find('input:checkbox').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column property="code" titleKey="setup.insurer.code" cssStyle="padding:10px 10px" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column property="name" titleKey="setup.insurer.name" cssStyle="padding:10px 10px" sortInitOrder="1"/>
						<datatables:column property="contactPerson" titleKey="setup.insurer.contactPerson" cssStyle="padding:10px 10px" sortInitOrder="2"/>
						<datatables:column property="telNo" titleKey="setup.insurer.telNo" cssStyle="padding:10px 10px" sortInitOrder="3"/>
						<datatables:column property="faxNo" titleKey="setup.insurer.faxNo" cssStyle="padding:10px 10px" sortInitOrder="4"/>
						<datatables:column property="email" titleKey="setup.insurer.email" cssStyle="padding:10px 10px" sortInitOrder="5"/>
						<datatables:column property="active" titleKey="setup.insurer.active" cssStyle="padding:10px 10px" sortInitOrder="6" searchable="false" renderFunction="activeFlag"/>
						<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="edit" cssStyle="padding:10px 10px"/>
					</datatables:table>
				</div>
				<div class="panel-footer" align="right">
<%-- 					<form:button name="action" value="delete" class="btn btn-primary"><spring:message code="button.delete" /></form:button> --%>
					<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>
				</div>										
			</div>
		</div>
	</div>
	</form:form>	
</div>
