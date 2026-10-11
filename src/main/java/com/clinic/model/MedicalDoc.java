package com.clinic.model;

import java.time.LocalDateTime;

public class MedicalDoc {

    private int docId;
    private int patientId;
    private String filePath;
    private String docType;
    private String extractedText;
    private LocalDateTime uploadDate;

    public MedicalDoc() {
    }

    public MedicalDoc(int docId, int patientId, String filePath, String docType, LocalDateTime uploadDate) {
        this(docId, patientId, filePath, docType, null, uploadDate);
    }

    public MedicalDoc(int docId, int patientId, String filePath, String docType,
                      String extractedText, LocalDateTime uploadDate) {
        this.docId = docId;
        this.patientId = patientId;
        this.filePath = filePath;
        this.docType = docType;
        this.extractedText = extractedText;
        this.uploadDate = uploadDate;
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}
