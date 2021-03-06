<%@ include file="../../layouts/commontags.jsp"%>

<script type="text/javascript">

	function addHoverToDatatableRows() {
	    var trs = document.getElementById('menuitem').getElementsByTagName('tbody')[0]
	        .getElementsByTagName('tr');
	    for (var i = 0; i < trs.length; i++) {
	        trs[i].onmouseover = new Function("this.bgColor='#ff0000'");
	        trs[i].onmouseout = new Function("this.bgColor='#ffffff'");
	     }
	}	
	
	function checkbox(cellvalue, options, rowObject) {
		return '<input type="checkbox" name="selected" value="' + rowObject.id + '" />'; 
	}
	
	function parentFlag(data) {
		if (data) {
			return '<input type="checkbox" disabled="true" checked="checked">';
		} else {
			return '<input type="checkbox" disabled="true">';
		}
	}
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/menumgmt/menu/edit/"/>${url_param_prefix}/' + rowObject.id + '\"><spring:message code="label.edit"/></a>';
	}
</script>
<spring:url var="backUrl" value="/secured" />
<spring:url var="gridUrl" value="/secured/menumgmt/menugrid" />
<spring:url var="postUrl" value="/secured/menumgmt/menu" />

<div class="class-container">
	<form:form commandName="menuForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />
	<div class="row">
	<div class="col-sm-1"> </div>
		<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">	
		    <div class="panel-heading"><spring:message code="menumgmt.menusetup.menuitem" /></div>
				<div class="panel-body">					
					<!-- menu listing -->
					<datatables:table id="menuitem" cssClass="table table-striped" url="${gridUrl}" 
 						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false"> 
					<datatables:column searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="checkbox">
					  <datatables:columnHead>
					    <input type="checkbox" onclick="$('#menuitem').find('input:checkbox').prop('checked', this.checked);" />
					  </datatables:columnHead>
					</datatables:column>
					<datatables:column property="name" titleKey="label.name" cssStyle="padding:10px 10px" sortInitOrder="2" sortInitDirection="asc"/>
					<datatables:column property="description" titleKey="label.description" cssStyle="padding:10px 10px" sortInitOrder="3" sortInitDirection="asc"/>												
					<datatables:column property="parentMenuItem" titleKey="menumgmt.menusetup.parent" cssStyle="padding:10px 10px" searchable="false" sortInitOrder="0" sortInitDirection="asc"/>
					<datatables:column property="parentFlag" value="parentFlag" titleKey="menumgmt.menusetup.parentFlag" searchable="false" sortable="false" cssStyle="padding:10px 10px" renderFunction="parentFlag"/>
					<datatables:column property="sortOrder" titleKey="label.sortOrder" cssStyle="padding:10px 10px" searchable="false" sortInitOrder="1" sortInitDirection="asc"/>						
					<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="edit" cssStyle="padding:10px 10px"/>
					</datatables:table>
							
					<!-- button -->	
					<div class="panel-footer" align="right">
					<%--<form:button name="action" value="delete" class="btn btn-primary"><spring:message code="button.delete" /></form:button>--%>
					<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
					<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>
					</div>
				</div>
		    </div>	
		</div>
	</div>
	</form:form>
</div>