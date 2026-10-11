<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.clinic.model.Appointment" %>
<%@ page import="com.clinic.model.Doctor" %>
<%@ page import="com.clinic.model.MedicalDoc" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Patient Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header>
    <a href="${pageContext.request.contextPath}/patient/dashboard">E-Healthcare Clinic Manager</a>
    <div class="nav-links">
        <span>Hi, <%= session.getAttribute("name") %></span>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<div class="container">
    <% if (request.getParameter("booked") != null) { %>
        <div class="success">Appointment booked successfully.</div>
    <% } %>
    <% if (request.getParameter("uploaded") != null) { %>
        <div class="success">File uploaded successfully.</div>
    <% } %>
    <% if (request.getParameter("error") != null) { %>
        <div class="error"><%= request.getParameter("error") %></div>
    <% } %>

    <div class="grid-2">
        <div class="card">
            <h2>Book an Appointment</h2>
            <form method="post" action="${pageContext.request.contextPath}/patient/book">
                <label for="doctorId">Doctor</label>
                <select id="doctorId" name="doctorId" required>
                    <%
                        List<Doctor> doctors = (List<Doctor>) request.getAttribute("doctors");
                        if (doctors != null) {
                            for (Doctor d : doctors) {
                    %>
                        <option value="<%= d.getDoctorId() %>"><%= d.getName() %> - <%= d.getSpecialization() %></option>
                    <%
                            }
                        }
                    %>
                </select>

                <label for="apptDate">Date</label>
                <input type="date" id="apptDate" name="apptDate" required>

                <label for="timeSlot">Time Slot</label>
                <select id="timeSlot" name="timeSlot" required>
                    <option value="09:00-09:30">09:00 - 09:30</option>
                    <option value="09:30-10:00">09:30 - 10:00</option>
                    <option value="10:00-10:30">10:00 - 10:30</option>
                    <option value="10:30-11:00">10:30 - 11:00</option>
                    <option value="11:00-11:30">11:00 - 11:30</option>
                    <option value="14:00-14:30">14:00 - 14:30</option>
                    <option value="14:30-15:00">14:30 - 15:00</option>
                    <option value="15:00-15:30">15:00 - 15:30</option>
                </select>

                <label for="severityLevel">Severity (1 = Critical, 5 = Routine)</label>
                <select id="severityLevel" name="severityLevel" required>
                    <option value="1">1 - Critical / Emergency</option>
                    <option value="2">2 - Urgent</option>
                    <option value="3">3 - Moderate</option>
                    <option value="4">4 - Mild</option>
                    <option value="5" selected>5 - Routine checkup</option>
                </select>

                <label for="reason">Reason for visit</label>
                <textarea id="reason" name="reason" rows="2"></textarea>

                <button type="submit">Book Appointment</button>
            </form>
        </div>

        <div class="card">
            <h2>Upload Medical History</h2>
            <p class="muted">PDF or text files, up to 10 MB.</p>
            <form method="post" action="${pageContext.request.contextPath}/patient/upload" enctype="multipart/form-data">
                <label for="medicalFile">File</label>
                <input type="file" id="medicalFile" name="medicalFile" accept=".pdf,.txt,.doc,.docx" required>
                <button type="submit">Upload</button>
            </form>

            <h3 style="margin-top:22px;">Your Uploaded Files</h3>
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
                <tr><td colspan="3" class="muted">No files uploaded yet.</td></tr>
                <% } } %>
            </table>
        </div>
    </div>

    <div class="card">
        <h2>Your Appointments</h2>
        <table>
            <tr><th>Doctor</th><th>Date</th><th>Time</th><th>Severity</th><th>Status</th></tr>
            <%
                List<Appointment> appts = (List<Appointment>) request.getAttribute("appointments");
                if (appts != null) {
                    for (Appointment a : appts) {
            %>
            <tr>
                <td><%= a.getDoctorName() %></td>
                <td><%= a.getApptDate() %></td>
                <td><%= a.getTimeSlot() %></td>
                <td><span class="badge badge-<%= a.getSeverityLevel() %>"><%= a.getSeverityLevel() %></span></td>
                <td><span class="status-<%= a.getStatus() %>"><%= a.getStatus() %></span></td>
            </tr>
            <%
                    }
                    if (appts.isEmpty()) {
            %>
            <tr><td colspan="5" class="muted">No appointments booked yet.</td></tr>
            <% } } %>
        </table>
    </div>
</div>
</body>
</html>
