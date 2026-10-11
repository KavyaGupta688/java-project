package com.clinic.controller;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.dao.impl.AppointmentDAOImpl;
import com.clinic.model.Appointment;
import com.clinic.model.AppointmentStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@WebServlet("/patient/book")
public class BookAppointmentServlet extends HttpServlet {

    private final IAppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int patientId = (int) req.getSession().getAttribute("patientId");

        try {
            int doctorId = Integer.parseInt(req.getParameter("doctorId"));
            LocalDate apptDate = LocalDate.parse(req.getParameter("apptDate"));
            String timeSlot = req.getParameter("timeSlot");
            int severity = Integer.parseInt(req.getParameter("severityLevel"));
            String reason = req.getParameter("reason");

            if (apptDate.isBefore(LocalDate.now())) {
                resp.sendRedirect(req.getContextPath() + "/patient/dashboard?error=Cannot+book+a+past+date");
                return;
            }
            if (severity < 1 || severity > 5) {
                severity = 5;
            }

            if (appointmentDAO.isSlotTaken(doctorId, apptDate, timeSlot)) {
                resp.sendRedirect(req.getContextPath() + "/patient/dashboard?error=That+slot+is+already+booked");
                return;
            }

            Appointment appt = new Appointment();
            appt.setDoctorId(doctorId);
            appt.setPatientId(patientId);
            appt.setApptDate(apptDate);
            appt.setTimeSlot(timeSlot);
            appt.setStatus(AppointmentStatus.PENDING);
            appt.setSeverityLevel(severity);
            appt.setReason(reason);

            appointmentDAO.insertAppointment(appt);

            resp.sendRedirect(req.getContextPath() + "/patient/dashboard?booked=1");
        } catch (SQLException e) {
            throw new ServletException("Database error booking appointment", e);
        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            resp.sendRedirect(req.getContextPath() + "/patient/dashboard?error=Invalid+booking+details");
        }
    }
}
