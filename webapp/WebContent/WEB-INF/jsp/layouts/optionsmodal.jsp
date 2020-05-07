<%@ include file="commontags.jsp"%>
<!-- Modal -->
<div class="modal bootstrap-dialog fade size-normal in" id="optionsDlg" tabindex="-1" role="dialog" aria-labelledby="optionsDlgLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header bg-primary round-corner-bg-primary">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="optionsDlgLabel"><spring:message code="options" /></h4>
      </div>
      <div class="modal-body">
<%--
      	<h3>
      		<spring:message code="language" />
      	</h3>
		<div>
			<a href="?lang=en">EN</a> <a href="?lang=ja">JA</a>
		</div>
--%>		      

		<c:url value="/index.html" var="indexUrl"/>
		<c:url value="/login" var="loginUrl"/>
		<c:url value="/loginfailed" var="loginfailedUrl"/>
		<c:set var="currentUrl" value="${requestScope['javax.servlet.forward.request_uri']}"/>
		<c:if test="${currentUrl == indexUrl || currentUrl == loginUrl || currentUrl == loginfailedUrl}">					
			<h3>
				<spring:message code="theme" />
			</h3>
			<div>
				<a href="?theme=ocean">Ocean</a>
				<a href="?theme=sunny">Sunny</a>
				<a href="?theme=blue">Blue</a>
			</div>
		</c:if>			
      	<h4><spring:message code="version" />1.2.4 (build 20180727)</h4>		
      </div>
    </div>
  </div>
</div>