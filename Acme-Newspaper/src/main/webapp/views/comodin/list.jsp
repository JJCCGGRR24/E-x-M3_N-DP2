<?xml version="1.0" encoding="UTF-8" ?>

<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


<script>
	function preguntar(rendezvousId) {
		eliminar = confirm('<spring:message code="newspaper.confirmDelete"/>');
		if (eliminar)
			//Redireccionamos si das a aceptar
			window.location.href = "article/admin/delete.do?articleId=" + rendezvousId; //página web a la que te redirecciona si confirmas la eliminación
		else
			//Y aquí pon cualquier cosa que quieras que salga si le diste al boton de cancelar
			alert('<spring:message code="newspaper.negativeDelete"/>');
	}
	
	
</script>

<display:table name="comodines" id="row" requestURI="${requestURI}"
	pagesize="7" class="displaytag" sort="list" defaultsort="1" defaultorder="descending">

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
	

<acme:date code="comodin.moment" property="moment"/>
<acme:column code="comodin.shortTitle" property="shortTitle"/>
<acme:column code="comodin.description" property="description"/>

</display:table>


<input type="button" name="back" onclick="javascriptRedir('/newspaper/list.do')"  value="<spring:message code="general.back"/>"/>