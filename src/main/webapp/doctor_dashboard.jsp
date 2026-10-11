<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.clinic.model.Appointment" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Doctor Dashboard</title>
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
    <div class="card">
        <h2>Triaged Patient Queue</h2>
        <p class="muted">Sorted automatically by severity (PriorityQueue) - most critical cases appear first.</p>

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
                <td><%= rank++ %></td>
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
                        <button type="submit" class="btn-secondary" style="margin-top:0;padding:6px 10px;font-size:0.8rem;">Confirm</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/updateStatus" style="display:inline;">
                        <input type="hidden" name="apptId" value="<%= a.getApptId() %>">
                        <input type="hidden" name="date" value="<%= selectedDate %>">
                        <input type="hidden" name="status" value="COMPLETED">
                        <button type="submit" style="margin-top:0;padding:6px 10px;font-size:0.8rem;">Complete</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/doctor/updateStatus" style="display:inline;">
                        <input type="hidden" name="apptId" value="<%= a.getApptId() %>">
                        <input type="hidden" name="date" value="<%= selectedDate %>">
                        <input type="hidden" name="status" value="CANCELLED">
                        <button type="submit" class="btn-danger" style="margin-top:0;padding:6px 10px;font-size:0.8rem;">Cancel</button>
                    </form>
                </td>
            </tr>
            <%
                    }
                    if (queue.isEmpty()) {
            %>
            <tr><td colspan="7" class="muted">No appointments for this date.</td></tr>
            <% } } %>
        </table>
    </div>
</div>
</body>
</html>
