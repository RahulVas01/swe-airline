<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Search Results Page</title>
	
	<style type="text/css">
		table.searchResults {
			border-width: 1px 1px 1px 1px;
			border-spacing: 2px;
			border-style: none none none none;
			border-color: gray gray gray gray;
			border-collapse: separate;
			background-color: white;
		}
		table.searchResults th {
			border-width: thin thin thin thin;
			padding: 2px 2px 2px 2px;
			border-style: inset inset inset inset;
			border-color: gray gray gray gray;
			background-color: white;
			-moz-border-radius: 0px 0px 0px 0px;
		}
		table.searchResults td {
			border-width: thin thin thin thin;
			padding: 2px 2px 2px 2px;
			border-style: inset inset inset inset;
			border-color: gray gray gray gray;
			background-color: white;
			-moz-border-radius: 0px 0px 0px 0px;
		}
	</style>
</head>
<body>
	<c:set var="basePath" value="${pageContext.request.contextPath}" />
	
	<%--
	<c:choose>
 		<c:when test="${sessionScope.currentUser != null}">
	 		${sessionScope.currentUser.username} |
	 		<a href="${basePath}/logOff">Log Out</a><br /> 
	 	</c:when>
	 	<c:otherwise>
	 		<a href="${basePath}/jsp/login.jsp">Log In</a><br />
	 	</c:otherwise>
 	</c:choose>
 	--%>
 	
	<h1 style="margin-bottom: 0px">Available Flights</h1>
	
	<c:set var="serviceMessage" value="" />
	
	<c:if test="${not empty requestScope.ejbSearch && requestScope.ejbSearch}">
		<c:set var="serviceMessage" value="Search was run using <span style='color: green'>EJB Service</span>" />
		<span style="color: green"></span>
	</c:if>
	<c:if test="${not empty requestScope.webServiceSearch && requestScope.webServiceSearch}">
		<c:set var="serviceMessage" value="Search was run using <span style='color: green'>Web Service</span>" />
	</c:if>
	
	
	<h2 style="margin-top: 0px;">(${serviceMessage})</h2>
	<c:if test="${not empty requestScope.error}">
 		<span style="color: red;">
 			<ul>
 				<li><c:out value="${requestScope.error}" escapeXml="false" /></li>
 			</ul>
 		</span>
 	
 	</c:if>
 	
 	<display:table name="requestScope.flights" cellspacing="10px" class="searchResults">
 		<display:column property="id" title="Flight #" />
 		<display:column property="departureAirport.airportCode" title="Depart Code" />
 		<display:column property="destinationAirport.airportCode" title="Dest Code" />
 		<display:column property="departureDate" format="{0,date,MM-dd-yyyy}" title="Flight Date" />
 		<display:column property="cost" format="$ {0,number,000.00}" title="Cost" />
 		<display:column property="availableSeats" title="Avail Seats" />
 		<display:column property="airplane.id" title="Airplane Id" />
 	</display:table>
 	
	<br />
	<b>Please make your choices for a reservation:</b>
	<br />
 	<form action="${basePath}/reserveFlight">
 		Which Flight # would you like?
 		<select name="flightId">
 			<c:forEach var="flight" items="${requestScope.flights}">
 				<option>${flight.id }</option>
			</c:forEach>
 		</select>
 		<br />
 		What Customer ID is the reservation for? <span style="font-style: italic;">(see customer details in table below)</span>
 		<select name="customerId">
 			<c:forEach var="customer" items="${requestScope.customers}">
 				<option>${customer.id }</option>
			</c:forEach>
 		</select>
 		<br />
 		<label>Number of Seats</label>
 		<input type="text" name="numSeats" />
 		<br />
 		<input type="submit" value="Reserve Seats" />
 	</form>
 	<br />
 	<a href="${basePath}/prepareSearch">Run New Search</a><br>
	<a href="${basePath}/jsp/home.jsp">Main Menu</a>
	<br />
	<br />
	
	<display:table name="requestScope.customers" cellspacing="10px" class="searchResults">
 		<display:column property="id" title="Customer ID" />
 		<display:column property="name" title="Name" />
 		<display:column property="address" title="Address" />
 		<display:column property="phone" title="Phone" />
 	</display:table>
</body>
</html>