<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
	
<form:form action="comodin/administrator/edit.do" modelAttribute="comodin">
	
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="administrator"/>
	<form:hidden path="ticker"/>

	
	<acme:textbox code="comodin.shortTitle" path="shortTitle"/><br>
	<acme:textarea code="comodin.description" path="description"/><br>
	<acme:textbox code="comodin.moment" placeholder="dd/MM/yyyy HH:mm" path="moment"/><br>
	
	<form:label path="gauge">
		<spring:message code="comodin.gauge"/>:
	</form:label>
	<form:input path="gauge" type="number" max="3" min="1" value = "1"/>
	<form:errors cssClass="error" path="gauge"/>
	<br /><br>
	
	
	<spring:message code="comodin.finalMode" var="finalModeHeader"/>
	<form:checkbox  label="${finalModeHeader}" path="finalMode"/><br><br>

	
	<input type="submit" name="save" value="<spring:message code="template.save"/>" />
	<jstl:if test="${!(comodin.id eq 0)}">
		<input type="submit" name="delete" value="<spring:message code="template.delete"/>" />
	</jstl:if>
	<input type="button" name="cancel" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('/comodin/administrator/myList.do');"/>

</form:form>
