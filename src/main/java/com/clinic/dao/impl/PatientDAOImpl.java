package com.clinic.dao.impl;

import com.clinic.dao.IPatientDAO;
import com.clinic.model.Patient;
import com.clinic.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class PatientDAOImpl implements IPatientDAO {

    private static final String JOIN_SELECT =
            "SELECT p.patient_id, p.blood_group, p.emergency_contact, " +
            "       u.user_id, u.name, u.email, u.password_hash " +
            "FROM Patients p JOIN Users u ON p.user_id = u.user_id ";

    @Override
    public int insertPatientProfile(int userId, String bloodGroup, String emergencyContact) throws SQLException {
        String sql = "INSERT INTO Patients (user_id, blood_group, emergency_contact) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, bloodGroup);
            ps.setString(3, emergencyContact);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert patient profile");
    }

    @Override
    public Optional<Patient> findByUserId(int userId) throws SQLException {
        String sql = JOIN_SELECT + "WHERE p.user_id = ?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByPatientId(int patientId) throws SQLException {
        String sql = JOIN_SELECT + "WHERE p.patient_id = ?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getInt("patient_id"),
                rs.getString("blood_group"),
                rs.getString("emergency_contact")
        );
    }
}
