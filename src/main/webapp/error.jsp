<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Something went wrong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth">
<div class="aurora"><span class="a1"></span><span class="a2"></span><span class="a3"></span></div>
<div class="auth-box" style="text-align:center;">
    <div class="auth-brand">
        <div class="logo-circle" style="background:linear-gradient(135deg,#dc2626,#b91c1c);">&#9888;</div>
        <h1>Something went wrong</h1>
        <p class="muted">An unexpected error occurred.</p>
    </div>
    <div class="error" style="text-align:left;"><%= exception != null ? exception.getMessage() : "Unknown error" %></div>
    <a class="btn" href="${pageContext.request.contextPath}/login.jsp" style="width:100%;text-align:center;">Back to login</a>
</div>
<script src="${pageContext.request.contextPath}/js/enhance.js"></script>
</body>
</html>
