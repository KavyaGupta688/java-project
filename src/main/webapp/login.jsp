<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - E-Healthcare Clinic Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth">
<div class="aurora"><span class="a1"></span><span class="a2"></span><span class="a3"></span></div>
<div class="auth-box">
    <div class="auth-brand">
        <div class="logo-circle">&#9877;</div>
        <h1>Clinic Manager</h1>
        <p class="muted">Sign in to your patient or doctor account.</p>
    </div>

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
        <input type="email" id="email" name="email" placeholder="you@example.com" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="••••••••" required>

        <button type="submit" style="width:100%;">Log In</button>
    </form>

    <footer class="link-center muted" style="margin-top:16px;">
        No account yet? <a href="${pageContext.request.contextPath}/register">Register here</a>
    </footer>
</div>
<script src="${pageContext.request.contextPath}/js/enhance.js"></script>
</body>
</html>
