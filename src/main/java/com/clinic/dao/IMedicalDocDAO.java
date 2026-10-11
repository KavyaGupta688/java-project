package com.clinic.dao;

import com.clinic.model.MedicalDoc;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IMedicalDocDAO {

    int insertDoc(int patientId, String filePath, String docType, String extractedText) throws SQLException;

    List<MedicalDoc> findByPatient(int patientId) throws SQLException;

    Optional<MedicalDoc> findById(int docId) throws SQLException;
}
