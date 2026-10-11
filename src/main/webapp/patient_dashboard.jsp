<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.clinic.model.Appointment" %>
<%@ page import="com.clinic.model.Doctor" %>
<%@ page import="com.clinic.model.MedicalDoc" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="aurora"><span class="a1"></span><span class="a2"></span><span class="a3"></span></div>
<div class="scene-3d">
<header>
    <a href="${pageContext.request.contextPath}/patient/dashboard"><span class="brand-mark">&#9877;</span> Clinic Manager</a>
    <div class="nav-links">
        <span>Hi, <%= session.getAttribute("name") %> &#128075;</span>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
</header>

<div class="container">
    <div class="page-hero">
        <div class="hero-mark">&#128200;</div>
        <div>
            <h1>Patient Portal</h1>
            <p>Book appointments, upload medical history, and track your visits.</p>
        </div>
    </div>

    <%
        List<Appointment> appts0 = (List<Appointment>) request.getAttribute("appointments");
        List<MedicalDoc> docs0 = (List<MedicalDoc>) request.getAttribute("docs");
        long upcoming = appts0 == null ? 0 : appts0.stream()
                .filter(a -> a.getStatus() == com.clinic.model.AppointmentStatus.PENDING
                          || a.getStatus() == com.clinic.model.AppointmentStatus.CONFIRMED)
                .count();
        long total  = appts0 == null ? 0 : appts0.size();
        long files  = docs0 == null ? 0 : docs0.size();
    %>
    <div class="stat-row">
        <div class="stat-tile">
            <div class="stat-icon">&#128197;</div>
            <div>
                <div class="stat-num"><%= total %></div>
                <div class="stat-label">Total appointments</div>
            </div>
        </div>
        <div class="stat-tile">
            <div class="stat-icon">&#128337;</div>
            <div>
                <div class="stat-num"><%= upcoming %></div>
                <div class="stat-label">Upcoming visits</div>
            </div>
        </div>
        <div class="stat-tile">
            <div class="stat-icon">&#128196;</div>
            <div>
                <div class="stat-num"><%= files %></div>
                <div class="stat-label">Medical files</div>
            </div>
        </div>
    </div>

    <% if (request.getParameter("booked") != null) { %>
        <div class="success">Appointment booked successfully.</div>
    <% } %>
    <% if (request.getParameter("uploaded") != null) { %>
        <div class="success">File uploaded and parsed successfully.</div>
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
                    <option value="1">&#128680; 1 - Critical / Emergency</option>
                    <option value="2">2 - Urgent</option>
                    <option value="3">3 - Moderate</option>
                    <option value="4">4 - Mild</option>
                    <option value="5" selected>5 - Routine checkup</option>
                </select>

                <label for="reason">Reason for visit</label>
                <textarea id="reason" name="reason" rows="2" placeholder="Briefly describe your symptoms..."></textarea>

                <button type="submit" style="width:100%;">Book Appointment</button>
            </form>
        </div>

        <div class="card">
            <h2>Upload Medical History</h2>
            <p class="muted">PDF or text files, up to 10 MB. PDFs are parsed automatically on upload.</p>
            <form method="post" action="${pageContext.request.contextPath}/patient/upload" enctype="multipart/form-data">
                <label for="medicalFile">File</label>
                <input type="file" id="medicalFile" name="medicalFile" accept=".pdf,.txt,.doc,.docx" required>
                <button type="submit" style="width:100%;">Upload &amp; Parse &#8594;</button>
            </form>

            <h3 style="margin-top:24px;">Your Uploaded Files</h3>
            <table>
                <tr><th>Type</th><th>Uploaded</th><th></th></tr>
                <%
                    List<MedicalDoc> docs = (List<MedicalDoc>) request.getAttribute("docs");
                    if (docs != null) {
                        for (MedicalDoc doc : docs) {
                %>
                <tr>
                    <td><span class="badge badge-<%= "PDF".equals(doc.getDocType()) ? 3 : 5 %>" style="background:#eef2f7;color:#475569;"><%= doc.getDocType() %></span></td>
                    <td><%= doc.getUploadDate() %></td>
                    <td><a href="${pageContext.request.contextPath}/docs/download?docId=<%= doc.getDocId() %>">Download</a></td>
                </tr>
                <%
                        }
                        if (docs.isEmpty()) {
                %>
                <tr class="empty-row"><td colspan="3">No files uploaded yet — add your first medical record above.</td></tr>
                <% } } %>
            </table>
        </div>
    </div>

    <div class="card">
        <h2>&#128203; Your Appointments</h2>
        <table>
            <tr><th>Doctor</th><th>Date</th><th>Time</th><th>Severity</th><th>Status</th></tr>
            <%
                List<Appointment> appts = (List<Appointment>) request.getAttribute("appointments");
                if (appts != null) {
                    for (Appointment a : appts) {
            %>
            <tr>
                <td><strong><%= a.getDoctorName() %></strong></td>
                <td><%= a.getApptDate() %></td>
                <td><%= a.getTimeSlot() %></td>
                <td><span class="badge badge-<%= a.getSeverityLevel() %>"><%= a.getSeverityLevel() %></span></td>
                <td><span class="status-<%= a.getStatus() %>"><%= a.getStatus() %></span></td>
            </tr>
            <%
                    }
                    if (appts.isEmpty()) {
            %>
            <tr class="empty-row"><td colspan="5">No appointments booked yet — book your first visit above.</td></tr>
            <% } } %>
        </table>
    </div>
</div>
</div>
<script src="${pageContext.request.contextPath}/js/enhance.js"></script>
</body>
</html>
