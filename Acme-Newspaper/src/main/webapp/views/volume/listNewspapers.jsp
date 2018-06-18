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
			window.location.href = "newspaper/admin/delete.do?newspaperId=" + id; //p�gina web a la que te redirecciona si confirmas la eliminaci�n
		else
			//Y aqu� pon cualquier cosa que quieras que salga si le diste al boton de cancelar
			alert('<spring:message code="newspaper.negativeDelete"/>');
	}
</script>


<script type="text/javascript">
	$(document).ready(function() {
		$("#buttonSearch").click(function() {
			if ($("#textSearch").val()!="")
				window.location.replace('newspaper/all/list.do?search='+ $("#textSearch").val());
		});
		$("#textSearch").on('keyup',function(e) {
			if (e.keyCode === 13 && $("#textSearch").val()!="")
				window.location.replace('newspaper/all/list.do?search='+ $("#textSearch").val());
			e.preventDefault();
		});
	});
</script>

<br>
<br>
<security:authorize access="hasRole('USER')">
	<security:authentication property="principal.username" var="username" />
	<%-- <jstl:if test="${row.user.userAccount.username eq username}"> --%>

	<%-- <input type="button"  value="<spring:message code="volume.add"/>" 
				onclick="javascript: relativeRedir('./volume/user/add.do?volumeId='+${row.id});"/> --%>
	<security:authentication property="principal.username" var="pri" />
	<jstl:if test="${pri eq volume.user.userAccount.username}">
		<input type="button" value="<spring:message code="template.add"/>"
			onclick="javascript: relativeRedir('volume/user/addNewspaper.do?volumeId=${volume.id}');" />
		<br />
	</jstl:if>

	<%-- </jstl:if> --%>
</security:authorize>

<display:table name="newspapers" id="row" requestURI="${requestURI}"
	pagesize="10" class="displaytag" sort="list" defaultsort="1"
	defaultorder="descending">

	<spring:message code="event.format.dateWithoutHour" var="pattern" />
	<display:column property="publicationDate"
		titleKey="newspaper.publicationDate" format="${pattern}" />
	<display:column property="title" titleKey="general.title" />
	<display:column>
		<input type="button" value="<spring:message code="general.details"/>"
			onclick="javascript: relativeRedir('newspaper/details.do?newspaperId='+${row.id});" />
	</display:column>

	<security:authorize access="hasRole('CUSTOMER')">
		<display:column>
			<jstl:if test="${row.deprived == true and !newspapersSubs.contains(row)}">
				<spring:url value="/subscribe/customer/subscribe.do" var="editURL">
					<spring:param name="newspaperId" value="${row.id}"></spring:param>
				</spring:url>
				<a href="${editURL}"> <spring:message
						code="subscribe.toSubscribe" /></a>
			</jstl:if>
			<jstl:if test="${newspapersSubs.contains(row)}">
				<spring:message code="volume.unsub" />
			</jstl:if>
		</display:column>
	</security:authorize>

	<security:authorize access="hasRole('ADMIN')">
		<display:column>
			<a href="javascript:preguntar(${row.id})"><spring:message
					code="newspaper.delete" /></a>
		</display:column>
	</security:authorize>



	<security:authorize access="hasRole('USER')">
		<security:authentication property="principal.username" var="pri" />
		<jstl:if test="${pri eq volume.user.userAccount.username}">
			<display:column>
				<input type="button"
					value="<spring:message code="newspaper.delete"/>"
					onclick="javascript: relativeRedir('volume/user/delete.do?volumeId=${volume.id}&newspaperId=${row.id}');" />
			</display:column>
		</jstl:if>
	</security:authorize>

</display:table>