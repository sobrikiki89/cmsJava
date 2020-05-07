<%@ include file="../../layouts/commontags.jsp"%>

<spring:url var="backUrl" value="/secured/claim/edit" />
<spring:url var="sendUrl" value="/secured/claim/notification/send" />
<sec:authentication property="principal.username" var="currentUser"/>

<script type="text/javascript">
	// When the document is ready
	$(document).ready(function () {
		
	});

	function back() {
		$(location).attr(
				'href',
				"${backUrl}" + "/" + "${url_param_prefix}" + "/"
						+ $("#claimId").val());
	}
	
	function insurerEmailChecked(){
		$("#insurerEmail").val($("#recipientInsurer").val());
	}
</script>

<div class="container-fluid">
	<div class="row">
	    <div class="col-sm-1"></div>
	  	<div class="panel-group col-sm-12">
		    <div class="panel panel-primary">
		    	<div class="panel-heading"><spring:message code="claim.claimEmailNotification" /></div>
				<form:form commandName="claimNotificationForm" method="post" action="${sendUrl}" role="form" class="form-horizontal">
				<form:errors element="div" cssClass="errorblock" path="*" />
				<form:hidden id="claimId" path="notificationEmail.claim.id"/>
				<div class="panel-body">
					<fieldset>
 						<legend><spring:message code="claim.email.recipient" /><span class="mandatory">*</span></legend>
						<div class="form-group row">
	  						<span class="col-sm-1"></span>
	  						<label class="control-label col-sm-2" for="recipientInsurer">
	  							<spring:message code="claim.email.recipient.insurer" />
	  						</label>
	  						<span class="col-sm-6">
	   							<form:input path="notificationEmail.claim.policy.insurer.contact.email" class="form-control input-sm" id="recipientInsurer" readOnly="true"/>
	   							<form:hidden id="insurerEmail" path="insurerEmail"/>
	   						</span>
	   						<span class="col-sm-1">
								<form:checkbox path="insurerRecipientSelected" id="activeFlag" onclick="insurerEmailChecked()"/>	   						
	   						</span>
	   					</div>			
						<div class="form-group row">
	  						<span class="col-sm-1"></span>
	  						<label class="control-label col-sm-2" for="recipientSib">
	  							<spring:message code="claim.email.recipient.sib" />
	  						</label>
	  						<span class="col-sm-6">
								<form:select multiple="true" path="sibEmails" class="form-control input-sm">
								    <form:options items="${claimNotificationForm.emailRecipientSib}" />
								</form:select>	  						
	   						</span>
	   						<span class="col-sm-1">
								<form:checkbox path="sibRecipientSelected" id="activeFlag"/>	   						
	   						</span>
						</div>
					</fieldset>
					<fieldset>
 						<legend><spring:message code="claim.email.subject" /></legend>
						<div class="form-group row">
	  						<span class="col-sm-1"></span>
   							<span class="col-sm-10"><c:out value="${claimNotificationForm.notificationEmail.subject}" escapeXml="false"/></span>   							
						</div>
 					</fieldset>					
					<fieldset>
 						<legend><spring:message code="claim.email.content" /></legend>
						<div class="form-group row">
	  						<span class="col-sm-1"></span>
   							<span class="col-sm-10"><c:out value="${claimNotificationForm.notificationEmail.content}" escapeXml="false"/></span>   							
						</div>
 					</fieldset>
 					<fieldset>
 						<legend><spring:message code="claim.email.attachment" /></legend>
						<c:if test="${claimNotificationForm.attachments != null}">
							<c:forEach var="attachment" varStatus="i" begin="0" items="${claimNotificationForm.attachments}">
								<div class="form-group row">
									<span class="col-sm-1"></span>
									<span class="col-sm-1">${i.index + 1}</span>
									<span class="col-sm-6">${attachment.name}</span>
									<span class="col-sm-1">${attachment.dispFileSize}</span>
									<span class="col-sm-1">
										<form:checkbox path="selectedAttachments" value="${attachment.id}"/>
									</span>
								 </div>							
							</c:forEach>
						</c:if>						
 					</fieldset>
				</div>				
				<div class="panel-footer" align="right">
					<input type="submit" class="btn btn-primary" value="<spring:message code="button.send" />" />
					<input type="button" class="btn btn-primary" value="<spring:message code="button.back" />" onclick="back()"/>
				</div>	
				</form:form>
			</div>
		</div>
	</div>
</div>
