<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<form:form action="${requestURI}" modelAttribute="volume">
	
	<form:hidden path="id"/>
	<form:hidden path="version"/>
	<form:hidden path="subscribesVol"/>
	<form:hidden path="user"/>
	<form:hidden path="newspapers"/>
	
	
	
	<form:label path="title">
		<spring:message code="volume.title"/>:
	</form:label>
	<form:input path="title" type="title"/>
	<form:errors cssClass="error" path="title"/>
	<br />
	
	<form:label path="description">
		<spring:message code="volume.description"/>:
	</form:label>
	<form:textarea path="description" type="description"/>
	<form:errors cssClass="error" path="description"/>
	<br />
	
	<form:label path="year">
		<spring:message code="volume.year"/>:
	</form:label>
	<form:input path="year" type="year"/>
	<form:errors cssClass="error" path="year"/>
	<br />
	
	
	
	
	
<%-- 	<form:label path="newspapers"> --%>
<%-- 		<spring:message code="volume.newspapers" />: --%>
<%-- 	</form:label> --%>
<%-- 	<form:select path="newspapers"> --%>
<%-- 		<form:options items="${newspapers}" itemLabel="title" --%>
<%-- 			itemValue="id" /> --%>
<%-- 	</form:select> --%>
<%-- 	<form:errors cssClass="error" path="newspapers" /> --%>
<!-- 	<br /> -->
<!-- 	<br /> -->
	
	
	
	
	
	<input type="submit" name="save" onclick="javascript:relativeRedir('volume/user/edit.do');" value="<spring:message code="note.save"/>" />


	<jstl:if test="${volume.id != 0}">	
	<input type="submit" name="delete" 
	onclick="javascript: relativeRedir('jisit/manager/edit.do');"
		value="<spring:message code="template.delete" />" />&nbsp; 
	</jstl:if>
	
	
	
	<input type="button" name="cancell" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('volume/all/list.do');"/>


</form:form>
