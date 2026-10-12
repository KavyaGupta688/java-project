package com.clinic.dao.impl;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.model.Appointment;
import com.clinic.model.AppointmentStatus;
import com.clinic.util.DBConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOImpl implements IAppointmentDAO {

    private static final String JOIN_SELECT =
            "SELECT a.appt_id, a.doctor_id, a.patient_id, a.appt_date, a.time_slot, a.status, " +
            "       a.severity_level, a.reason, a.reminder_sent, a.created_at, " +
            "       pu.name AS patient_name, du.name AS doctor_name " +
            "FROM Appointments a " +
            "JOIN Patients p ON a.patient_id = p.patient_id " +
            "JOIN Users pu ON p.user_id = pu.user_id " +
            "JOIN Doctors d ON a.doctor_id = d.doctor_id " +
            "JOIN Users du ON d.user_id = du.user_id ";

    @Override
    public int insertAppointment(Appointment appt) throws SQLException {
        String sql = "INSERT INTO Appointments (doctor_id, patient_id, appt_date, time_slot, status, severity_level, reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, appt.getDoctorId());
            ps.setInt(2, appt.getPatientId());
            ps.setDate(3, java.sql.Date.valueOf(appt.getApptDate()));
            ps.setString(4, appt.getTimeSlot());
            ps.setString(5, appt.getStatus().name());
            ps.setInt(6, appt.getSeverityLevel());
            ps.setString(7, appt.getReason());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert appointment");
    }

    @Override
    public List<Appointment> findByPatient(int patientId) throws SQLException {
        String sql = JOIN_SELECT + "WHERE a.patient_id = ? ORDER BY a.appt_date, a.time_slot";
        List<Appointment> list = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date) throws SQLException {
        String sql = JOIN_SELECT + "WHERE a.doctor_id = ? AND a.appt_date = ? " +
                "AND a.status IN ('PENDING','CONFIRMED') ORDER BY a.severity_level, a.created_at";
        List<Appointment> list = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean isSlotTaken(int doctorId, LocalDate date, String timeSlot) throws SQLException {
        String sql = "SELECT 1 FROM Appointments WHERE doctor_id = ? AND appt_date = ? AND time_slot = ? " +
                "AND status IN ('PENDING','CONFIRMED')";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setString(3, timeSlot);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public void updateStatus(int apptId, AppointmentStatus status) throws SQLException {
        String sql = "UPDATE Appointments SET status = ? WHERE appt_id = ?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, apptId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Appointment> findUpcomingNeedingReminder(int withinMinutes) throws SQLException {
        // Appointments scheduled for today or tomorrow, still pending/confirmed, no reminder sent yet.
        String sql = JOIN_SELECT + "WHERE a.reminder_sent = FALSE AND a.status IN ('PENDING','CONFIRMED') " +
                "AND a.appt_date BETWEEN ? AND ?";
        List<Appointment> list = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            java.time.LocalDate today = java.time.LocalDate.now();
            ps.setDate(1, java.sql.Date.valueOf(today));
            ps.setDate(2, java.sql.Date.valueOf(today.plusDays(1)));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void markReminderSent(int apptId) throws SQLException {
        String sql = "UPDATE Appointments SET reminder_sent = TRUE WHERE appt_id = ?";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, apptId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Appointment> findExpired() throws SQLException {
        String sql = JOIN_SELECT + "WHERE a.appt_date < ? AND a.status IN ('PENDING','CONFIRMED')";
        List<Appointment> list = new ArrayList<>();
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public int expireOldAppointments() throws SQLException {
        String sql = "UPDATE Appointments SET status = 'EXPIRED' " +
                "WHERE appt_date < ? AND status IN ('PENDING','CONFIRMED')";
        try (Connection con = DBConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(java.time.LocalDate.now()));
            return ps.executeUpdate();
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setApptId(rs.getInt("appt_id"));
        a.setDoctorId(rs.getInt("doctor_id"));
        a.setPatientId(rs.getInt("patient_id"));
        a.setApptDate(rs.getDate("appt_date").toLocalDate());
        a.setTimeSlot(rs.getString("time_slot"));
        a.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        a.setSeverityLevel(rs.getInt("severity_level"));
        a.setReason(rs.getString("reason"));
        a.setReminderSent(rs.getBoolean("reminder_sent"));
        Timestamp ts = rs.getTimestamp("created_at");
        a.setCreatedAt(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());
        a.setPatientName(rs.getString("patient_name"));
        a.setDoctorName(rs.getString("doctor_name"));
        return a;
    }
}
