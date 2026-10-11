package com.clinic.model;

public class Patient extends User {

    private int patientId;
    private String bloodGroup;
    private String emergencyContact;

    public Patient() {
        super();
    }

    public Patient(int userId, String name, String email, String passwordHash,
                   int patientId, String bloodGroup, String emergencyContact) {
        super(userId, name, email, passwordHash, Role.PATIENT);
        this.patientId = patientId;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }
}
