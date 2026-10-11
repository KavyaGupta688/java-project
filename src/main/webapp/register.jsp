<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - E-Healthcare Clinic Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script>
        function toggleRoleFields() {
            var role = document.querySelector('input[name="role"]:checked').value;
            document.getElementById('patientFields').style.display = (role === 'PATIENT') ? 'block' : 'none';
            document.getElementById('doctorFields').style.display = (role === 'DOCTOR') ? 'block' : 'none';
            // Refresh the highlighted toggle cards
            document.querySelectorAll('.role-toggle label').forEach(function (l) {
                l.classList.toggle('checked', l.querySelector('input').value === role);
            });
        }
        window.addEventListener('DOMContentLoaded', toggleRoleFields);
    </script>
</head>
<body class="auth">
<div class="aurora"><span class="a1"></span><span class="a2"></span><span class="a3"></span></div>
<div class="auth-box">
    <div class="auth-brand">
        <div class="logo-circle">&#9877;</div>
        <h1>Create Account</h1>
        <p class="muted">Join as a patient or a doctor.</p>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <label for="name">Full Name</label>
        <input type="text" id="name" name="name" placeholder="Dr. Jane Doe" required>

        <label for="email">Email</label>
        <input type="email" id="email" name="email" placeholder="you@example.com" required>

        <label for="password">Password</label>
        <input type="password" id="password" name="password" placeholder="At least 6 characters" required minlength="6">

        <label>I am a:</label>
        <div class="role-toggle">
            <label class="checked"><input type="radio" name="role" value="PATIENT" checked onchange="toggleRoleFields()"> &#129658; Patient</label>
            <label><input type="radio" name="role" value="DOCTOR" onchange="toggleRoleFields()"> &#129656; Doctor</label>
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
            <input type="number" id="consultationFee" name="consultationFee" step="0.01" min="0" placeholder="e.g. 500">
        </div>

        <button type="submit" style="width:100%;">Register</button>
    </form>

    <footer class="link-center muted" style="margin-top:16px;">
        Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Log in</a>
    </footer>
</div>
<script src="${pageContext.request.contextPath}/js/enhance.js"></script>
</body>
</html>
