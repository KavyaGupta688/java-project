<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.clinic.model.Appointment" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Doctor Dashboard</title>
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
    <div class="page-hero">
        <div class="hero-mark">&#127973;</div>
        <div>
            <h1>Doctor Portal</h1>
            <p>Your day's queue, automatically prioritized by medical severity.</p>
        </div>
    </div>

    <%
        List<Appointment> queue0 = (List<Appointment>) request.getAttribute("queue");
        long critical  = queue0 == null ? 0 : queue0.stream().filter(a -> a.getSeverityLevel() <= 2).count();
        long pendingNow = queue0 == null ? 0 : queue0.stream()
                .filter(a -> a.getStatus() == com.clinic.model.AppointmentStatus.PENDING
                          || a.getStatus() == com.clinic.model.AppointmentStatus.CONFIRMED)
                .count();
        long done      = queue0 == null ? 0 : queue0.stream()
                .filter(a -> a.getStatus() == com.clinic.model.AppointmentStatus.COMPLETED).count();
    %>
    <div class="stat-row">
        <div class="stat-tile">
            <div class="stat-icon" style="background:var(--red-100);">&#128680;</div>
            <div>
                <div class="stat-num"><%= critical %></div>
                <div class="stat-label">Critical / urgent</div>
            </div>
        </div>
        <div class="stat-tile">
            <div class="stat-icon">&#128337;</div>
            <div>
                <div class="stat-num"><%= pendingNow %></div>
                <div class="stat-label">Active in queue</div>
            </div>
        </div>
        <div class="stat-tile">
            <div class="stat-icon" style="background:var(--green-100);">&#9989;</div>
            <div>
                <div class="stat-num"><%= done %></div>
                <div class="stat-label">Completed today</div>
            </div>
        </div>
    </div>

    <div class="card">
        <h2>Triaged Patient Queue</h2>
        <p class="muted">Sorted automatically by severity (PriorityQueue) — most critical cases appear first.</p>

        <form method="get" action="${pageContext.request.contextPath}/doctor/dashboard" style="max-width:260px;">
            <label for="date">Date</label>
            <input type="date" id="date" name="date" value="<%= request.getAttribute("selectedDate") %>"
                   onchange="this.form.submit()">
        </form>

        <table>
            <tr><th>#</th><th>Severity</th><th>Patient</th><th>Time</th><th>Reason</th><th>Status</th><th>Actions</th></tr>
            <%
                List<Appointment> queue = (List<Appointment>) request.getAttribute("queue");
                String selectedDate = (String) request.getAttribute("selectedDate");
                if (queue != null) {
                    int rank = 1;
                    for (Appointment a : queue) {
            %>
            <tr>
                <td><span class="rank-chip"><%= rank++ %></span></td>
                <td><span class="badge badge-<%= a.getSeverityLevel() %>"><%= a.getSeverityLevel() %></span></td>
                <td><a href="${pageContext.request.contextPath}/doctor/patientRecords?patientId=<%= a.getPatientId() %>"><%= a.getPatientName() %></a></td>
                <td><%= a.getTimeSlot() %></td>
                <td><%= a.getReason() != null ? a.getReason() : "" %></td>
                <td><span class="status-<%= a.getStatus() %>"><%= a.getStatus() %></span></td>
                <td>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/updateStatus" style="display:inline;">
                        <input type="hidden" name="apptId" value="<%= a.getApptId() %>">
                        <input type="hidden" name="date" value="<%= selectedDate %>">
                        <input type="hidden" name="status" value="CONFIRMED">
                        <button type="submit" class="btn-secondary" style="margin-top:0;padding:6px 11px;font-size:0.78rem;">Confirm</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/updateStatus" style="display:inline;">
                        <input type="hidden" name="apptId" value="<%= a.getApptId() %>">
                        <input type="hidden" name="date" value="<%= selectedDate %>">
                        <input type="hidden" name="status" value="COMPLETED">
                        <button type="submit" style="margin-top:0;padding:6px 11px;font-size:0.78rem;">Complete</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/updateStatus" style="display:inline;">
                        <input type="hidden" name="apptId" value="<%= a.getApptId() %>">
                        <input type="hidden" name="date" value="<%= selectedDate %>">
                        <input type="hidden" name="status" value="CANCELLED">
                        <button type="submit" class="btn-danger" style="margin-top:0;padding:6px 11px;font-size:0.78rem;">Cancel</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                    if (queue.isEmpty()) {
            %>
            <tr class="empty-row"><td colspan="7">No appointments for this date — pick another day or share your booking link.</td></tr>
            <% } } %>
        </table>
    </div>
</div>
</div>
<script src="${pageContext.request.contextPath}/js/enhance.js"></script>
</body>
</html>
