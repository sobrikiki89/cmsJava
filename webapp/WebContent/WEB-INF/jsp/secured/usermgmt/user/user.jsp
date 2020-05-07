<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/usermgmt/user/edit/"/>${url_param_prefix}/' + rowObject.id + '\"><spring:message code="label.edit"/></a>';
	}
	
	function addHoverToDatatableRows() {
	    var trs = document.getElementById('myTableId').getElementsByTagName('tbody')[0]
	        .getElementsByTagName('tr');
	    for (var i = 0; i < trs.length; i++) {
	        trs[i].onmouseover = new Function("this.bgColor='#ff0000'");
	        trs[i].onmouseout = new Function("this.bgColor='#ffffff'");
	     }
	}
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" value="' + rowObject.id + '" />'; 
	}
	
	function activeFlag(data) {
		if (data) {
			return '<input type="checkbox" disabled="true" checked="checked">';
		} else {
			return '<input type="checkbox" disabled="true">';
		}
	}
	
</script>
<div class="container-fluid">
	<spring:url var="gridUrl" value="/secured/usermgmt/user/usergrid" />
	<spring:url var="postUrl" value="/secured/usermgmt/user" />
	<form:form commandName="userForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />	
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="usermgmt.user.user_listing" /></div>
				<div class="panel-body">
					<datatables:table id="myTableId" cssClass="table table-striped" url="${gridUrl}" 
						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false">
						<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="checkbox">
						  <datatables:columnHead>
						    <input type="checkbox" onclick="$('#myTableId').find('input:checkbox').prop('checked', this.checked);" />
						  </datatables:columnHead>
						</datatables:column>
						<datatables:column property="username" titleKey="usermgmt.user.username" cssStyle="padding:10px 10px" sortInitOrder="0" sortInitDirection="asc"/>
						<datatables:column property="firstName" titleKey="usermgmt.user.firstname" cssStyle="padding:10px 10px" sortInitOrder="1"/>
						<datatables:column property="lastName" titleKey="usermgmt.user.lastname" cssStyle="padding:10px 10px" sortInitOrder="2"/>
<%-- 						<datatables:column property="companyCode" titleKey="usermgmt.user.company" cssStyle="padding:10px 10px" sortInitOrder="3" searchable="false"/> --%>
						<datatables:column property="lastAccessTime" titleKey="usermgmt.user.lastAccessTime" cssStyle="padding:10px 10px" sortable="false" searchable="false"/>
						<datatables:column property="activeFlag" value="activeFlag" titleKey="label.activeFlag" searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="activeFlag"/>
						<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="edit" cssStyle="padding:10px 10px"/>
					</datatables:table>
				</div>
				<div class="panel-footer" align="right">
					<%--<form:button name="action" value="delete" class="btn btn-primary"><spring:message code="button.delete" /></form:button>--%>
					<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>					
				</div>										
			</div>
		</div>
	</div>
	</form:form>
</div>
