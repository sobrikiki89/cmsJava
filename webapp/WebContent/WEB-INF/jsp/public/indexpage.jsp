<%@ include file="../layouts/commontags.jsp"%>

<spring:message var="title" code="home.title" />

<tiles:insertDefinition name="unsecured-template">
	<tiles:putAttribute name="title" value="${title}" />
	<tiles:putAttribute name="content" value="/WEB-INF/jsp/public/indexcontent.jsp"/>
</tiles:insertDefinition>