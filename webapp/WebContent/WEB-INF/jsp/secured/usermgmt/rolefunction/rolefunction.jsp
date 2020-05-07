<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">

	function addHoverToDatatableRows() {
	    var trs = document.getElementById('rolefunctiongrid').getElementsByTagName('tbody')[0]
	        .getElementsByTagName('tr');
	    for (var i = 0; i < trs.length; i++) {
	        trs[i].onmouseover = new Function("this.bgColor='#ff0000'");
	        trs[i].onmouseout = new Function("this.bgColor='#ffffff'");
	     }
	}	
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" name="selected" value="' + rowObject.pk.roleId + ':' + rowObject.pk.functionCode + '" />'; 
	}
	
	function permission(data, rowobject) {
		if (data) {
			return '<input type="checkbox" disabled="true" checked="checked">';
		} else {
			return '<input type="checkbox" disabled="true">';
		}
	}
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/usermgmt/rolefunction/edit/"/>${url_param_prefix}/' + rowObject.pk.roleId+ '/' +rowObject.pk.functionCode + '/' + '\"><spring:message code="label.edit"/></a>';
	}
	
</script>

<div class="class-container">
	<spring:url var="gridUrl" value="/secured/usermgmt/rolefunction/rolefunctiongrid" />
	<spring:url var="postUrl" value="/secured/usermgmt/rolefunction" />
	<form:form commandName="roleFunctionForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />	
	<div class="row">
	<div class="col-sm-1"> </div>
		<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="usermgmt.rolefunction.rolefunction_listing" /></div>
					<div class="panel-body">
						<datatables:table id="rolefunctiongrid" cssClass="table table-striped" url="${gridUrl}" 
							row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px; width:10%" renderFunction="checkbox">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#rolefunctiongrid').find('input:checkbox[name=selected]').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column property="role" titleKey="usermgmt.rolefunction.role" cssStyle="padding:10px 10px; width:30%" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column property="function" titleKey="usermgmt.rolefunction.function" cssStyle="padding:10px 10px; width:60%" sortInitOrder="1" sortInitDirection="asc"/>
<%-- 						<datatables:column property="readable" value="readable" titleKey="usermgmt.rolefunction.readable" searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="permission"/> --%>
<%-- 						<datatables:column property="createable" value="creatable" titleKey="usermgmt.rolefunction.creatable" searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="permission"/> --%>
<%-- 						<datatables:column property="updateable" value="updatable" titleKey="usermgmt.rolefunction.updatable" searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="permission"/> --%>
<%-- 						<datatables:column property="deleteable" value="deletable" titleKey="usermgmt.rolefunction.deletable" searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="permission"/> --%>
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