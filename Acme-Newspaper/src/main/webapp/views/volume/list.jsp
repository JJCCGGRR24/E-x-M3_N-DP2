<%--
 * action-2.jsp
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<script>
	function preguntar(id) {
		eliminar = confirm('<spring:message code="newspaper.confirmDelete"/>');
		if (eliminar)
			//Redireccionamos si das a aceptar
			window.location.href = "volume/admin/delete.do?volumeId=" + id; //página web a la que te redirecciona si confirmas la eliminación
		else
			//Y aquí pon cualquier cosa que quieras que salga si le diste al boton de cancelar
			alert('<spring:message code="newspaper.negativeDelete"/>');
	}
</script>

<security:authorize access="hasRole('USER')">
	<spring:message code="volume.create" var="create" />
	<div align="left">
		<input type="button" value="${create}"
			onclick="javascript: window.location.href = './volume/user/create.do';" />
	</div>
</security:authorize>

<display:table name="volumes" id="row" requestURI="${requestURI}"
	pagesize="10" class="displaytag" sort="list" defaultsort="1"
	defaultorder="descending">

	<display:column property="user.name" titleKey="volume.user" />
	<display:column property="title" titleKey="volume.title" />
	<display:column property="description" titleKey="volume.description" />
	<display:column property="year" titleKey="volume.year" />
	<display:column titleKey="volume.newspapers">
		<a href="volume/all/listNewspapers.do?volumeId=${row.id}"> <spring:message
				code="volume.newspapers.view" />
		</a>
	</display:column>
	<security:authorize access="hasRole('CUSTOMER')">
		<display:column>
			<jstl:if test="${!mySubs.contains(row)}">
				<a href="subscribeVol/customer/subscribe.do?volumeId=${row.id}">
					<spring:message code="volume.sub" />
				</a>
			</jstl:if>
			<jstl:if test="${mySubs.contains(row)}">
				<spring:message code="volume.unsub" />
			</jstl:if>
		</display:column>
	</security:authorize>
</display:table>