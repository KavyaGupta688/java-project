<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.clinic.model.MedicalDoc" %>
<%@ page import="com.clinic.model.Patient" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Records</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="aurora"><span class="a1"></span><span class="a2"></span><span class="a3"></span></div>
<div class="scene-3d">
<header>
    <a href="${pageContext.request.contextPath}/doctor/dashboard"><span class="brand-mark">&#9877;</span> Clinic Manager</a>
    <div class="nav-links">
        <span><%= session.getAttribute("name") %></span>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<div class="container">
    <p class="muted" style="margin-bottom:14px;">
        <a href="${pageContext.request.contextPath}/doctor/dashboard">&larr; Back to queue</a>
    </p>

    <div class="page-hero">
        <div class="hero-mark">&#128196;</div>
        <div>
            <h1>Patient Records</h1>
            <p>Uploaded medical history files and their parsed contents.</p>
        </div>
    </div>

    <%
        Patient patient = (Patient) request.getAttribute("patient");
    %>
    <div class="stat-row">
        <div class="stat-tile">
            <div class="stat-icon">&#128100;</div>
            <div>
                <div class="stat-num" style="font-size:1.1rem;"><%= patient != null ? patient.getName() : "Unknown" %></div>
                <div class="stat-label">Patient</div>
            </div>
        </div>
        <div class="stat-tile">
            <div class="stat-icon" style="background:var(--red-100);">&#129370;</div>
            <div>
                <div class="stat-num" style="font-size:1.1rem;"><%= patient != null && patient.getBloodGroup() != null ? patient.getBloodGroup() : "N/A" %></div>
                <div class="stat-label">Blood group</div>
            </div>
        </div>
        <div class="stat-tile">
            <div class="stat-icon">&#128222;</div>
            <div>
                <div class="stat-num" style="font-size:1.1rem;"><%= patient != null && patient.getEmergencyContact() != null ? patient.getEmergencyContact() : "N/A" %></div>
                <div class="stat-label">Emergency contact</div>
            </div>
        </div>
    </div>

    <div class="card">
        <h2>&#128196; Uploaded Medical History</h2>
        <table>
            <tr><th>Type</th><th>Uploaded</th><th></th></tr>
            <%
                List<MedicalDoc> docs = (List<MedicalDoc>) request.getAttribute("docs");
                if (docs != null) {
                    for (MedicalDoc doc : docs) {
            %>
            <tr>
                <td><span class="badge" style="background:#eef2f7;color:#475569;"><%= doc.getDocType() %></span></td>
                <td><%= doc.getUploadDate() %></td>
                <td><a href="${pageContext.request.contextPath}/docs/download?docId=<%= doc.getDocId() %>">Download</a></td>
            </tr>
            <%
                    String text = doc.getExtractedText();
                    if (text != null && !text.isBlank()) {
            %>
            <tr>
                <td colspan="3" style="background:#fbfdfc;">
                    <div class="parsed-block">
                        <div class="parsed-label">&#128270; Parsed contents</div>
                        <pre><%= text %></pre>
                    </div>
                </td>
            </tr>
            <%
                    }
                }
                if (docs.isEmpty()) {
            %>
            <tr class="empty-row"><td colspan="3">No files uploaded by this patient.</td></tr>
            <% } } %>
        </table>
    </div>
</div>
</div>
<script src="${pageContext.request.contextPath}/js/enhance.js"></script>
</body>
</html>
