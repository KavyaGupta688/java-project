package com.clinic.controller;

import com.clinic.service.TriageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Shows the doctor's queue for a chosen day (defaults to today),
 * sorted by medical severity via TriageService's PriorityQueue.
 */
@WebServlet("/doctor/dashboard")
public class DoctorDashboardServlet extends HttpServlet {

    private final TriageService triageService = new TriageService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int doctorId = (int) req.getSession().getAttribute("doctorId");

        LocalDate date = LocalDate.now();
        String dateParam = req.getParameter("date");
        if (dateParam != null && !dateParam.isBlank()) {
            try {
                date = LocalDate.parse(dateParam);
            } catch (Exception ignored) {
                // fall back to today
            }
        }

        try {
            req.setAttribute("queue", triageService.getTriagedQueue(doctorId, date));
            req.setAttribute("selectedDate", date.toString());
        } catch (SQLException e) {
            throw new ServletException("Database error loading doctor queue", e);
        }

        req.getRequestDispatcher("/doctor_dashboard.jsp").forward(req, resp);
    }
}
