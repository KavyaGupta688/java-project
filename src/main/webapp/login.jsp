<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - E-Healthcare Clinic Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="auth-box">
    <h1>Clinic Manager</h1>
    <p class="muted">Sign in to your patient or doctor account.</p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>
    <% if ("1".equals(request.getParameter("registered"))) { %>
        <div class="success">Account created. Please log in.</div>
    <% } %>
    <% if (request.getParameter("error") != null) { %>
        <div class="error"><%= request.getParameter("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Log In</button>
    </form>

    <p class="muted" style="margin-top:16px;">
        No account yet? <a href="${pageContext.request.contextPath}/register">Register here</a>
    </p>
</div>
</body>
</html>
