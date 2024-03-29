<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Search Page</title>
	<link type="text/css" rel="stylesheet" href="css/datepickercontrol.css"/>
	<script type="text/javascript" src="script/datepickercontrol.js" />
	<script type="text/javascript">
		function load(){
			DatePickerControl.init();
		}
	</script>
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
	
	<h1>Flight Search</h1>
	<c:if test="${not empty requestScope.error}">
 		<span style="color: red;">
 			<ul>
 				<li><c:out value="${requestScope.error}" escapeXml="false" /></li>
 			</ul>
 		</span>
 	
 	</c:if>
 	<p>Please set your search parameters:</p>
 	<form action="${basePath}/flightSearch">
 		<label>Select Departing Airport</label>
	 	<select name="departureAirport" id="departureAirport">
	 		<option></option>
	 		<c:forEach var="airport" items="${requestScope.airports}">
	 			<option>${airport}</option>
	 		</c:forEach>
	 	</select>
	 	<br />
	 	<br />
	 	<label>Select Destination Airport</label>
	 	<select name="destinationAirport" id="destinationAirport">
	 		<option></option>
	 		<c:forEach var="airport" items="${requestScope.airports}">
	 			<option>${airport}</option>
	 		</c:forEach>
	 	</select>
	 	<br />
	 	<br />
	 	<label>Departure Date (MM/DD/YYYY):</label>
		<input type="text" name="flightDate" size="10" id="flightDate" datepicker="true" datepicker_format="MM/DD/YYYY" readonly="readonly"/>
		<a href="javascript:void(0)" onclick="clearDate()">Clear Date</a>
	 	<br />
	 	<br />
	 	
	 	<input type="radio" name="searchType" value="ejb" checked="checked" /> Search using an EJB<br />
	 	<input type="radio" name="searchType" value="webService" /> Search using a Web Service
	 	<br />
	 	<br />
	 	<input type="submit" value="Search">
 	</form>
 	<br />
	<a href="${basePath}/jsp/home.jsp">Main Menu</a>
</body>
<script type="text/javascript">
	//load();

	function clearDate(){
		document.getElementById("flightDate").value = "";
	}
</script>
</html>