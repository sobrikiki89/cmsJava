<%@ include file="../../../layouts/commontags.jsp"%>
<spring:url var="departmentSaveUrl" value="/secured/setup/company/departmentsave" />

<script type="text/javascript">
	function closeAndRefreshGrid () {	
		var form = {}		
		form["code"] = $("#dDepartmentCode").val();
		form["name"] = $("#dDepartmentName").val();

		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");		
		
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "${departmentSaveUrl}",
			data : JSON.stringify(form),
			dataType : 'json',
			timeout : 100000,
			beforeSend: function(xhr) {
	            xhr.setRequestHeader(header, token);
	        },			
			success : function(data) {
				console.log("SUCCESS: ", data);
				if(data.status != null) {
					$('#dStatus').css('display', 'block');
					$('#dStatus').html(data.status);
				}	
				else {
					$('#dStatus').css('display', 'none');
					$('#dStatus').html('');
						
					$('#dDepartmentCode').val('');
					$('#dDepartmentName').val('');

					//jQuery.noConflict();
					$('#departmentDialog').modal('hide');		
					oTable_departmentgrid.ajax.reload();
				}
			},
			error : function(e) {
				console.log("ERROR: ", e);
			},
			done : function(e) {
				console.log("DONE");
			}
		});
	}
</script>

<div id="departmentDialog" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-hidden="true">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4>
					<spring:message code="setup.company.department" />
				</h4>
			</div>

			<div class="modal-body">
				<input type="hidden" id="dAction"/>
				<div class="errorblock" id="dStatus" style="display: none"></div>			
				<div class="panel-body">
					<div class="form-group row">
						<label class="control-label col-sm-3" for="dDepartmentCode">
							<spring:message code="setup.company.departmentCode" />
							<span class="mandatory">*</span>
						</label> 
						<span class="col-sm-7">
							<input class="form-control input-sm" id="dDepartmentCode"/>
						</span>
					</div>

					<div class="form-group row">
						<label class="control-label col-sm-3" for="dDepartmentName">
							<spring:message code="setup.company.departmentName" />
							<span class="mandatory">*</span>
						</label>
						<span class="col-sm-7">
							<input class="form-control input-sm" id="dDepartmentName"/>
						</span>							
					</div>
				</div>
				<div class="panel-footer" align="right">
					<a href="#" onclick="closeAndRefreshGrid()" class="btn btn-primary"><spring:message code="button.submit" /></a>
				</div>					
			</div>
		</div>
	</div>
</div>
