<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags"%>
	
<form:form action="fust/administrator/edit.do" modelAttribute="fust">
	
<jstl:choose>	

<jstl:when test="${fustBD.finalMode eq true}" >
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	
	<form:label path="newspaper">
		<spring:message code="fust.newspaper" />
	</form:label>	
	<form:select path="newspaper" cssStyle="width:400px;"> 
		<jstl:forEach items="${newspapers}" var="cat">
			<form:option value="${cat.id}" label="${cat.title} (${cat.publicationDate})"/>
		</jstl:forEach>
	</form:select> 
	<form:errors path="newspaper" cssClass="error" />
	<br /><br>
	
	<acme:textbox code="fust.shortTitle" path="shortTitle" readonly="true"/><br>
	<acme:textarea code="fust.description" path="description" readonly="true"/><br>
	<acme:textbox code="fust.moment" placeholder="dd/MM/yyyy HH:mm" path="moment" readonly="true"/><br>
	
	<form:label path="gauge">
		<spring:message code="fust.gauge"/>:
	</form:label>
	<form:input path="gauge" type="number" max="3" min="1" value = "${fust.gauge}" readonly="true"/>
	<form:errors cssClass="error" path="gauge"/>
	<br /><br>
	
	<spring:message code="fust.finalMode" var="finalModeHeader"/>
	<form:checkbox  label="${finalModeHeader}" path="finalMode" onclick="javascript: return false;"/><br><br>
	
	
	<input type="submit" name="save" value="<spring:message code="template.save"/>" />
	
	<input type="button" name="cancel" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('/fust/administrator/myList.do');"/>
</jstl:when>

<jstl:otherwise>
	<form:hidden path="id"/>
	<form:hidden path="version"/>

	
	<acme:textbox code="fust.shortTitle" path="shortTitle"/><br>
	<acme:textarea code="fust.description" path="description"/><br>
	<acme:textbox code="fust.moment" placeholder="dd/MM/yyyy HH:mm" path="moment"/><br>
	
	<form:label path="gauge">
		<spring:message code="fust.gauge"/>:
	</form:label>
	<form:input path="gauge" type="number" max="3" min="1" value = "${fust.gauge}"/>
	<form:errors cssClass="error" path="gauge"/>
	<br /><br>
	
	
	<spring:message code="fust.finalMode" var="finalModeHeader"/>
	<form:checkbox  label="${finalModeHeader}" path="finalMode"/><br><br>

	
	<input type="submit" name="save" value="<spring:message code="template.save"/>" />
	<jstl:if test="${!(fust.id eq 0)}">
		<input type="submit" name="delete" value="<spring:message code="template.delete"/>" />
	</jstl:if>
	<input type="button" name="cancel" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('/fust/administrator/myList.do');"/>
</jstl:otherwise>
</jstl:choose>
</form:form>
