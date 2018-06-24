<?xml version="1.0" encoding="UTF-8" ?>

<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<security:authorize access="hasRole('ADMIN')">
<input type="button" name="template.create" value="<spring:message code="template.create"/>" onclick="javascript:relativeRedir('comodin/administrator/create.do')" />
</security:authorize>


<display:table name="comodines" id="row" requestURI="${requestURI}"
	pagesize="2" class="displaytag" sort="list" defaultsort="1" defaultorder="descending">

<acme:column code="comodin.ticker" property="ticker"/>

<jstl:choose>
	<jstl:when test="${row.gauge == 1}">
	<spring:message code="comodin.gauge" var="gaugeHeader"/>
	<display:column  title="${gaugeHeader}" sortable="true">
	<div style="background: lightYellow;"> <jstl:out value="${row.gauge}"/></div>
	</display:column>
	</jstl:when>
	
	<jstl:when test="${row.gauge == 2}">
	<spring:message code="comodin.gauge" var="gaugeHeader"/>
	<display:column  title="${gaugeHeader}" sortable="true">
	<div style="background: Moccasin;"> <jstl:out value="${row.gauge}"/></div>
	</display:column>
	</jstl:when>
	
	<jstl:when test="${row.gauge == 3}">
	<spring:message code="comodin.gauge" var="gaugeHeader"/>
	<display:column  title="${gaugeHeader}" sortable="true">
	<div style="background: Blue;"> <jstl:out value="${row.gauge}"/></div>
	</display:column>
	</jstl:when>
	</jstl:choose>
	

<acme:date2 code="comodin.moment" property="moment"/>
<acme:column code="comodin.shortTitle" property="shortTitle"/>
<acme:column code="comodin.description" property="description"/>

<display:column>
<security:authentication property="principal" var="userAccount"/>
	<spring:url value="comodin/administrator/edit.do" var="editURL">
		<spring:param name="comodinId" value="${row.id}"></spring:param>
	</spring:url>
	
	<jstl:if test="${(row.administrator.userAccount eq userAccount) and (row.finalMode == false)}">	
	<a href="${editURL}"><spring:message code="template.edit"/></a>
	</jstl:if>

	<jstl:if test="${(row.administrator.userAccount eq userAccount) and (row.finalMode eq true) and (row.newspaper == null)}">	
	<a href="${editURL}"><spring:message code="template.associate"/></a>
	</jstl:if>
	
	<jstl:if test="${!(row.newspaper == null)}">	
	<jstl:out value="${row.newspaper.title}"/>
	</jstl:if>
</display:column>

</display:table>


<input type="button" name="back" onclick="javascript:relativeRedir('/newspaper/list.do')"  value="<spring:message code="general.back"/>"/>