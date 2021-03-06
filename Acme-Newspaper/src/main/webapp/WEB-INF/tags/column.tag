<%--
 * column.tag
 *
 * Copyright (C) 2017 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>
 
<%@ tag language="java" body-content="empty" %>
 
 <%-- Taglibs --%>

<%@ taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<%-- Attributes --%> 
 
<%@ attribute name="code" required="true" %>
<%@ attribute name="property" required="true" %>
<%@ attribute name="sortable" required="false" %>
<%@ attribute name="moneda" required="false" %>
<%@ attribute name="porcentaje" required="false" %>
<%@ attribute name="date" required="false" %>

<jstl:if test="${moneda == true}">
	<jstl:set var="format" value="{0,number,0.00} &euro;" />
</jstl:if>

<jstl:if test="${porcentaje == true}">
	<jstl:set var="format" value="{0,number,0.00} %" />
</jstl:if>

<%-- Definition --%>


<display:column property="${property}" sortable ="${sortable}" titleKey="${code}" format="${format}"/>


