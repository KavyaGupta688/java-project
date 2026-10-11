package com.clinic.dao;

import com.clinic.model.Patient;

import java.sql.SQLException;
import java.util.Optional;

public interface IPatientDAO {

    int insertPatientProfile(int userId, String bloodGroup, String emergencyContact) throws SQLException;

    Optional<Patient> findByUserId(int userId) throws SQLException;

    Optional<Patient> findByPatientId(int patientId) throws SQLException;
}
