package com.clinic.dao.impl;

import com.clinic.dao.IDoctorDAO;
import com.clinic.model.Doctor;
import com.clinic.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDAOImpl implements IDoctorDAO {

    private static final String JOIN_SELECT =
            "SELECT d.doctor_id, d.specialization, d.consultation_fee, " +
            "       u.user_id, u.name, u.email, u.password_hash " +
            "FROM Doctors d JOIN Users u ON d.user_id = u.user_id ";

    @Override
    public int insertDoctorProfile(int userId, String specialization, double consultationFee) throws SQLException {
        String sql = "INSERT INTO Doctors (user_id, specialization, consultation_fee) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, specialization);
            ps.setDouble(3, consultationFee);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert doctor profile");
    }

    @Override
    public Optional<Doctor> findByUserId(int userId) throws SQLException {
        String sql = JOIN_SELECT + "WHERE d.user_id = ?";
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
    public Optional<Doctor> findByDoctorId(int doctorId) throws SQLException {
        String sql = JOIN_SELECT + "WHERE d.doctor_id = ?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Doctor> findAll() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(JOIN_SELECT + "ORDER BY u.name");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                doctors.add(mapRow(rs));
            }
        }
        return doctors;
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getInt("doctor_id"),
                rs.getString("specialization"),
                rs.getDouble("consultation_fee")
        );
    }
}
