<%@ include file="../layouts/commontags.jsp"%>

<spring:message var="title" code="login.title" />

<tiles:insertDefinition name="unsecured-template">
	<tiles:putAttribute name="title" value="${title}" />
	<tiles:putAttribute name="content" value="/WEB-INF/jsp/public/logincontent.jsp" />
</tiles:insertDefinition>