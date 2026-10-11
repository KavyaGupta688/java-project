package com.clinic.dao.impl;

import com.clinic.dao.IMedicalDocDAO;
import com.clinic.model.MedicalDoc;
import com.clinic.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicalDocDAOImpl implements IMedicalDocDAO {

    @Override
    public int insertDoc(int patientId, String filePath, String docType) throws SQLException {
        String sql = "INSERT INTO Medical_Docs (patient_id, file_path, doc_type) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, patientId);
            ps.setString(2, filePath);
            ps.setString(3, docType);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert medical document record");
    }

    @Override
    public List<MedicalDoc> findByPatient(int patientId) throws SQLException {
        String sql = "SELECT doc_id, patient_id, file_path, doc_type, upload_date " +
                "FROM Medical_Docs WHERE patient_id = ? ORDER BY upload_date DESC";
        List<MedicalDoc> docs = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp ts = rs.getTimestamp("upload_date");
                    docs.add(new MedicalDoc(
                            rs.getInt("doc_id"),
                            rs.getInt("patient_id"),
                            rs.getString("file_path"),
                            rs.getString("doc_type"),
                            ts != null ? ts.toLocalDateTime() : null
                    ));
                }
            }
        }
        return docs;
    }

    @Override
    public Optional<MedicalDoc> findById(int docId) throws SQLException {
        String sql = "SELECT doc_id, patient_id, file_path, doc_type, upload_date " +
                "FROM Medical_Docs WHERE doc_id = ?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, docId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("upload_date");
                    return Optional.of(new MedicalDoc(
                            rs.getInt("doc_id"),
                            rs.getInt("patient_id"),
                            rs.getString("file_path"),
                            rs.getString("doc_type"),
                            ts != null ? ts.toLocalDateTime() : null
                    ));
                }
            }
        }
        return Optional.empty();
    }
}
