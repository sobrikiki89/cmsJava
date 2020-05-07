<%@ include file="commontags.jsp"%>
<spring:url var="loginUrl" value="/login" />
<spring:url var="securedUrl" value="/secured" />

<script type="text/javascript">
$(document).ready(function() {
    function loadTimeoutDialog() { 
        var sessionAlive = ${pageContext.session.maxInactiveInterval};          
        var notifyBefore = 30; // Give client 15 seconds to choose.
        setTimeout(function() {       
            $(function() {
                $( "#timeoutDialog" ).modal();
              });
        }, (sessionAlive - notifyBefore) * 1000);
	};

	$("#keepAliveSession").click(function() {
		$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "${securedUrl}",
				data : '',
				dataType : 'json',
				timeout : 100000,
				success : function(data) {
					loadTimeoutDialog();
				},
				error : function(e) {
					window.location.replace("${loginUrl}");
				},
				done : function(e) {
				}
			});
	});	
	
	loadTimeoutDialog(); 		
});
</script>		
		
		
<div id="timeoutDialog" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4>
					<spring:message code="session.timeout.header" />
				</h4>
			</div>

			<div class="modal-body">
				<div class="panel-body">
					<div class="form-group row">
						<span class="col-sm-1"></span>
						<span class="col-sm-10">
							<spring:message code="session.timeout.body" />
						</span>
					</div>
					
					<div class="form-group row">
						<span class="col-sm-1"></span>
						<span class="col-sm-10 text-center">
							<button id="keepAliveSession" type="button" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">
								<spring:message code="button.submit" />						
							</button>
							<button type="button" class="btn btn-primary" data-dismiss="modal" aria-hidden="true">
								<spring:message code="button.close" />							
							</button>							
						</span>
					</div>					
				</div>
			</div>
		</div>
	</div>
</div>
