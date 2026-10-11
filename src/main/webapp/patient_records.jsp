<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.clinic.model.MedicalDoc" %>
<%@ page import="com.clinic.model.Patient" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Patient Records</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <a href="${pageContext.request.contextPath}/doctor/dashboard">E-Healthcare Clinic Manager</a>
    <div class="nav-links">
        <span>Dr. <%= session.getAttribute("name") %></span>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<div class="container">
    <a href="${pageContext.request.contextPath}/doctor/dashboard" class="muted">&larr; Back to queue</a>

    <%
        Patient patient = (Patient) request.getAttribute("patient");
    %>
    <div class="card">
        <h2>Patient Record<%= patient != null ? ": " + patient.getName() : "" %></h2>
        <% if (patient != null) { %>
        <p class="muted">
            Blood Group: <%= patient.getBloodGroup() != null ? patient.getBloodGroup() : "N/A" %> &nbsp;|&nbsp;
            Emergency Contact: <%= patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A" %>
        </p>
        <% } %>

        <h3 style="margin-top:20px;">Uploaded Medical History</h3>
        <table>
            <tr><th>Type</th><th>Uploaded</th><th></th></tr>
            <%
                List<MedicalDoc> docs = (List<MedicalDoc>) request.getAttribute("docs");
                if (docs != null) {
                    for (MedicalDoc doc : docs) {
            %>
            <tr>
                <td><%= doc.getDocType() %></td>
                <td><%= doc.getUploadDate() %></td>
                <td><a href="${pageContext.request.contextPath}/docs/download?docId=<%= doc.getDocId() %>">Download</a></td>
            </tr>
            <%
                    }
                    if (docs.isEmpty()) {
            %>
            <tr><td colspan="3" class="muted">No files uploaded by this patient.</td></tr>
            <% } } %>
        </table>
    </div>
</div>
</body>
</html>
