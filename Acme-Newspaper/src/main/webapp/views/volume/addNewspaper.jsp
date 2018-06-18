<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

	<select name="select" id="mySelect">
		<option value="0" label="----" />
		<jstl:forEach var="item" items="${newwspapers}">
			<option value="${item.id}"><fmt:formatDate value="${item.publicationDate}" pattern="dd/MM/yy" /> ${item.title}</option>
		</jstl:forEach>
	</select>
	
	<script>
	function myFunction() {
	    var x = document.getElementById("mySelect").value;
		window.location.replace('volume/user/addNewspaper2.do?volumeId=${volumeId}&newspaperId='+x);
	}
	</script>
	
	<input type="button" id="buttonSearch"
	value="<spring:message code="template.add"/>" onclick="javascript: myFunction()"/>

	<input type="button" name="cancell" value="<spring:message code="template.cancel"/>" 
	onclick="javascript: relativeRedir('volume/all/list.do');"/>	