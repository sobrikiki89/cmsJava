<%@ include file="../../../layouts/commontags.jsp"%>

<script type="text/javascript">

	function addHoverToDatatableRows() {
	    var trs = document.getElementById('menuitem').getElementsByTagName('tbody')[0]
	        .getElementsByTagName('tr');
	    for (var i = 0; i < trs.length; i++) {
	        trs[i].onmouseover = new Function("this.bgColor='#ff0000'");
	        trs[i].onmouseout = new Function("this.bgColor='#ffffff'");
	     }
	}			
	
	function edit(cellvalue, options, rowObject) {
		return '<a href=\"<spring:url value="/secured/usermgmt/role/edit/"/>${url_param_prefix}/' + rowObject.id + '\"><spring:message code="label.edit"/></a>';
	}
</script>

<div class="class-container">
	<spring:url var="gridUrl" value="/secured/usermgmt/role/rolegrid" />
	<spring:url var="postUrl" value="/secured/usermgmt/role" />
	<form:form commandName="roleForm" action="${postUrl}">
	<form:errors element="div" cssClass="errorblock" path="*" />
	<div class="row">
	<div class="col-sm-1"> </div>
		<div class="panel-group col-sm-10">
		    <div class="panel panel-primary">	
		    <div class="panel-heading"><spring:message code="usermgmt.role.role_listing" /></div>
				<div class="panel-body">
					
					<!-- role listing -->
					<datatables:table id="roleitem" cssClass="table table-striped" url="${gridUrl}" 
 						row="row" serverSide="true" pipelining="true" pipeSize="3" ext="responsive" autoWidth="false"> 					
					<datatables:column property="name" titleKey="label.name" cssStyle="padding:10px 10px" sortInitOrder="1" sortInitDirection="asc"/>
					<datatables:column property="description" titleKey="label.description" cssStyle="padding:10px 10px" sortInitOrder="2" sortInitDirection="asc"/>
					<datatables:column property="sortOrder" titleKey="label.sortOrder" cssStyle="padding:10px 10px" sortInitOrder="0" sortInitDirection="asc" searchable="false" />						
					<datatables:column searchable="false" titleKey="label.edit" sortable="false" renderFunction="edit" cssStyle="padding:10px 10px"/>
					</datatables:table>
							
					<!-- button -->	
					<div class="panel-footer" align="right">					
						<form:button name="action" value="new" class="btn btn-primary"><spring:message code="button.new" /></form:button>
						<form:button name="action" value="back" class="btn btn-primary"><spring:message code="button.back" /></form:button>		
					</div>
				</div>
		    </div>	
		</div>
	</div>
	</form:form>
</div>