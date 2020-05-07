<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/setup/insuranceclass/edit/"/>${url_param_prefix}/' + rowObject.code + '\"><spring:message code="label.edit"/></a>';
	}
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" name="selected" value="' + rowObject.code + '" />'; 
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
<spring:url var="gridUrl" value="/secured/setup/insuranceclass/insuranceclassgrid" />
<spring:url var="postUrl" value="/secured/setup/insuranceclass" />

<div class="class-container">
	<form:form commandName="insuranceClassForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="setup.insuranceclass.insuranceclass_listing" /></div>
				<div class="panel-body">
					<datatables:table id="myTableId" cssClass="table table-striped" url="${gridUrl}" 
						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px; width=5%" renderFunction="checkbox">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#myTableId').find('input:checkbox').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column property="code" titleKey="setup.insuranceclass.code" cssStyle="padding:10px 10px; width=15%" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column property="name" titleKey="setup.insuranceclass.name" cssStyle="padding:10px 10px; width=50%" sortInitOrder="1"/>
						<datatables:column property="category" titleKey="setup.insuranceclass.category" cssStyle="padding:10px 10px; width=50%" sortInitOrder="2"/>
						<datatables:column property="sortOrder" titleKey="setup.insuranceclass.sortOrder" cssStyle="padding:10px 10px; width=10%" sortInitOrder="3"/>
						<datatables:column property="activeFlag" titleKey="setup.insuranceclass.active" cssStyle="padding:10px 10px; width=10%" sortInitOrder="4" searchable="false" renderFunction="activeFlag"/>
						<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="edit" cssStyle="padding:10px 10px; width=10%"/>
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
