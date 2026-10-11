package com.clinic.controller;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.dao.impl.AppointmentDAOImpl;
import com.clinic.model.AppointmentStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/doctor/updateStatus")
public class UpdateAppointmentStatusServlet extends HttpServlet {

    private final IAppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int apptId = Integer.parseInt(req.getParameter("apptId"));
            AppointmentStatus status = AppointmentStatus.valueOf(req.getParameter("status").toUpperCase());
            appointmentDAO.updateStatus(apptId, status);
        } catch (SQLException e) {
            throw new ServletException("Database error updating appointment status", e);
        } catch (IllegalArgumentException ignored) {
            // bad status/id value - just fall through and redirect back
        }

        String date = req.getParameter("date");
        String redirect = req.getContextPath() + "/doctor/dashboard"
                + (date != null && !date.isBlank() ? "?date=" + date : "");
        resp.sendRedirect(redirect);
    }
}
