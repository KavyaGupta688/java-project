package com.clinic.controller;

import com.clinic.dao.IMedicalDocDAO;
import com.clinic.dao.impl.MedicalDocDAOImpl;
import com.clinic.model.MedicalDoc;
import com.clinic.model.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Streams an uploaded medical document back from local disk.
 * Patients may only download their own files; doctors may view any
 * patient's file (needed for the Doctor Portal "patient records" view).
 */
@WebServlet("/docs/download")
public class DownloadDocServlet extends HttpServlet {

    private final IMedicalDocDAO docDAO = new MedicalDocDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=Please+log+in");
            return;
        }

        try {
            int docId = Integer.parseInt(req.getParameter("docId"));
            Optional<MedicalDoc> docOpt = docDAO.findById(docId);
            if (docOpt.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            MedicalDoc doc = docOpt.get();

            Role role = (Role) session.getAttribute("role");
            if (role == Role.PATIENT) {
                int patientId = (int) session.getAttribute("patientId");
                if (doc.getPatientId() != patientId) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            File file = new File(doc.getFilePath());
            if (!file.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File missing on server");
                return;
            }

            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            Files.copy(file.toPath(), resp.getOutputStream());
            resp.getOutputStream().flush();
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            throw new ServletException("Database error retrieving document", e);
        }
    }
}
