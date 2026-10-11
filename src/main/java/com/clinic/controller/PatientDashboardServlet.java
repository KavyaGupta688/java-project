package com.clinic.controller;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.dao.IDoctorDAO;
import com.clinic.dao.IMedicalDocDAO;
import com.clinic.dao.impl.AppointmentDAOImpl;
import com.clinic.dao.impl.DoctorDAOImpl;
import com.clinic.dao.impl.MedicalDocDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/patient/dashboard")
public class PatientDashboardServlet extends HttpServlet {

    private final IDoctorDAO doctorDAO = new DoctorDAOImpl();
    private final IAppointmentDAO appointmentDAO = new AppointmentDAOImpl();
    private final IMedicalDocDAO docDAO = new MedicalDocDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int patientId = (int) req.getSession().getAttribute("patientId");
        try {
            req.setAttribute("doctors", doctorDAO.findAll());
            req.setAttribute("appointments", appointmentDAO.findByPatient(patientId));
            req.setAttribute("docs", docDAO.findByPatient(patientId));
        } catch (SQLException e) {
            throw new ServletException("Database error loading patient dashboard", e);
        }
        req.getRequestDispatcher("/patient_dashboard.jsp").forward(req, resp);
    }
}
