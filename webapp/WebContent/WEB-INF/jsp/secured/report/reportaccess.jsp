<%@ include file="../../layouts/commontags.jsp"%>

<script type="text/javascript">
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/report/access/edit/"/>${url_param_prefix}/' + rowObject.id + '\"><spring:message code="label.edit"/></a>';
	}
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" name="selected" value="' + rowObject.id + '" />'; 
		 
	}

</script>
<spring:url var="backUrl" value="/secured" />
<spring:url var="gridUrl" value="/secured/report/access/accesscontrolgrid" />
<spring:url var="postUrl" value="/secured/report/access" />

<div class="class-container">
	<form:form commandName="reportAccessForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="report.access.reportaccess_listing" /></div>
				<div class="panel-body">
					<datatables:table id="myTableId" cssClass="table table-striped" url="${gridUrl}" 
						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="checkbox">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#myTableId').find('input:checkbox[name=selected]').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column property="reportName" titleKey="report.reportName" cssStyle="padding:10px 10px" sortInitOrder="1"/>
						<datatables:column property="reportCategory" titleKey="report.category" cssStyle="padding:10px 10px" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column property="role" titleKey="report.role" cssStyle="padding:10px 10px" sortInitOrder="2"/>
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
</div>
