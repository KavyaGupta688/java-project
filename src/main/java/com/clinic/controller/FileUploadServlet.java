package com.clinic.controller;

import com.clinic.dao.IMedicalDocDAO;
import com.clinic.dao.impl.MedicalDocDAOImpl;
import com.clinic.util.DBConnectionManager;
import com.clinic.util.PdfTextExtractor;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Handles upload of patient medical-history files (PDF/text) to a
 * local directory on disk (see upload.dir in db.properties) and
 * records the resulting path against the patient in Medical_Docs.
 * No cloud storage or API key is involved - storage is purely local.
 */
@WebServlet("/patient/upload")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10 MB cap
public class FileUploadServlet extends HttpServlet {

    private final IMedicalDocDAO docDAO = new MedicalDocDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int patientId = (int) req.getSession().getAttribute("patientId");

        Part filePart = req.getPart("medicalFile");
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendRedirect(req.getContextPath() + "/patient/dashboard?error=No+file+selected");
            return;
        }

        String originalName = filePart.getSubmittedFileName();
        String ext = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";
        String storedName = "patient_" + patientId + "_" + UUID.randomUUID() + ext;

        File uploadDir = new File(DBConnectionManager.getUploadDir());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        Path destination = uploadDir.toPath().resolve(storedName);

        try (var in = filePart.getInputStream()) {
            Files.copy(in, destination);
        }

        String docType = ext.replace(".", "").isBlank() ? "FILE" : ext.replace(".", "").toUpperCase();

        // Parse the uploaded PDF's contents right away ("uploading and parsing
        // of patient history PDFs" from the project statement). Non-PDF or
        // unreadable files are still stored; only the parsed text is absent.
        String extractedText = "PDF".equalsIgnoreCase(docType)
                ? PdfTextExtractor.extractText(destination.toFile())
                : null;

        try {
            docDAO.insertDoc(patientId, destination.toString(), docType, extractedText);
        } catch (SQLException e) {
            throw new ServletException("Database error saving document record", e);
        }

        resp.sendRedirect(req.getContextPath() + "/patient/dashboard?uploaded=1");
    }
}
