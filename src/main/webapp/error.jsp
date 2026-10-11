<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Something went wrong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-box">
    <h1>Something went wrong</h1>
    <div class="error"><%= exception != null ? exception.getMessage() : "Unknown error" %></div>
    <a class="btn" href="${pageContext.request.contextPath}/login.jsp">Back to login</a>
</div>
</body>
</html>
