package com.clinic.dao;

import com.clinic.model.Doctor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IDoctorDAO {

    int insertDoctorProfile(int userId, String specialization, double consultationFee) throws SQLException;

    Optional<Doctor> findByUserId(int userId) throws SQLException;

    Optional<Doctor> findByDoctorId(int doctorId) throws SQLException;

    List<Doctor> findAll() throws SQLException;
}
