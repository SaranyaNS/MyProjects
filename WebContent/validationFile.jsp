<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Validation of CSV and XML Files</title>
</head>
<body>
	<form action="servlet/ValidateCSVandXML" method="post">
		<h3>Please select a File to Import</h3>
		<br />
		<br /> <input type="file" id="fileVal" name="fileName" /> <input
			type="submit" value="ValidateCSVandXML" />
	</form>
</body>
</html>