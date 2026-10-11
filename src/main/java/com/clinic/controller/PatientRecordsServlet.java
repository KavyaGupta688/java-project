package com.clinic.controller;

import com.clinic.dao.IMedicalDocDAO;
import com.clinic.dao.IPatientDAO;
import com.clinic.dao.impl.MedicalDocDAOImpl;
import com.clinic.dao.impl.PatientDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Lets a logged-in doctor view a given patient's uploaded medical history files. */
@WebServlet("/doctor/patientRecords")
public class PatientRecordsServlet extends HttpServlet {

    private final IMedicalDocDAO docDAO = new MedicalDocDAOImpl();
    private final IPatientDAO patientDAO = new PatientDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int patientId = Integer.parseInt(req.getParameter("patientId"));
            req.setAttribute("patient", patientDAO.findByPatientId(patientId).orElse(null));
            req.setAttribute("docs", docDAO.findByPatient(patientId));
        } catch (SQLException e) {
            throw new ServletException("Database error loading patient records", e);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        req.getRequestDispatcher("/patient_records.jsp").forward(req, resp);
    }
}
