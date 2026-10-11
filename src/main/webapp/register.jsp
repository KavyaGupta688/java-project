<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register - E-Healthcare Clinic Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script>
        function toggleRoleFields() {
            var role = document.querySelector('input[name="role"]:checked').value;
            document.getElementById('patientFields').style.display = (role === 'PATIENT') ? 'block' : 'none';
            document.getElementById('doctorFields').style.display = (role === 'DOCTOR') ? 'block' : 'none';
        }
    </script>
</head>
<body>
<div class="auth-box">
    <h1>Create Account</h1>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <label for="name">Full Name</label>
        <input type="text" id="name" name="name" required>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required minlength="6">

        <label>I am a:</label>
        <div class="role-toggle">
            <label><input type="radio" name="role" value="PATIENT" checked onchange="toggleRoleFields()"> Patient</label>
            <label><input type="radio" name="role" value="DOCTOR" onchange="toggleRoleFields()"> Doctor</label>
        </div>

        <div id="patientFields">
            <label for="bloodGroup">Blood Group</label>
            <input type="text" id="bloodGroup" name="bloodGroup" placeholder="e.g. O+">

            <label for="emergencyContact">Emergency Contact</label>
            <input type="text" id="emergencyContact" name="emergencyContact" placeholder="Phone number">
        </div>

        <div id="doctorFields" style="display:none;">
            <label for="specialization">Specialization</label>
            <input type="text" id="specialization" name="specialization" placeholder="e.g. Cardiology">

            <label for="consultationFee">Consultation Fee</label>
            <input type="number" id="consultationFee" name="consultationFee" step="0.01" min="0">
        </div>

        <button type="submit">Register</button>
    </form>

    <p class="muted" style="margin-top:16px;">
        Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Log in</a>
    </p>
</div>
</body>
</html>
