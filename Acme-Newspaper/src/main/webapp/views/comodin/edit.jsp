<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
	
<form:form action="comodin/administrator/edit.do" modelAttribute="comodin">
	
<jstl:if test="${comodin.finalMode eq false}" >
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
	<input type="button" name="cancel" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('/comodin/administrator/myList.do');"/>
</jstl:if>

<jstl:if test="${comodin.finalMode eq true}" >
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="administrator"/>
	<form:hidden path="ticker"/>
	<form:hidden path="finalMode"/>
	
	<form:label path="newspaper">
		<spring:message code="comodin.newspaper" />
	</form:label>	
	<form:select  path="newspaper" >
		<form:options items="${newspapers}" itemValue="id" itemLabel="title"/>
	</form:select>
	<form:errors path="newspaper" cssClass="error" />
	<br /><br>
	
	<acme:textbox code="comodin.shortTitle" path="shortTitle" readonly="true"/><br>
	<acme:textarea code="comodin.description" path="description" readonly="true"/><br>
	<acme:textbox code="comodin.moment" placeholder="dd/MM/yyyy HH:mm" path="moment" readonly="true"/><br>
	
	<form:label path="gauge">
		<spring:message code="comodin.gauge"/>:
	</form:label>
	<form:input path="gauge" type="number" max="3" min="1" value = "${comodin.gauge}" readonly="true"/>
	<form:errors cssClass="error" path="gauge"/>
	<br /><br>
	
	<input type="submit" name="save" value="<spring:message code="template.save"/>" />
	<jstl:if test="${!(comodin.id eq 0)}">
		<input type="submit" name="delete" value="<spring:message code="template.delete"/>" />
	</jstl:if>
	<input type="button" name="cancel" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('/comodin/administrator/myList.do');"/>
</jstl:if>
</form:form>
