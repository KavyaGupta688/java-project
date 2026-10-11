package com.clinic.model;

public class Doctor extends User {

    private int doctorId;
    private String specialization;
    private double consultationFee;

    public Doctor() {
        super();
    }

    public Doctor(int userId, String name, String email, String passwordHash,
                  int doctorId, String specialization, double consultationFee) {
        super(userId, name, email, passwordHash, Role.DOCTOR);
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.consultationFee = consultationFee;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }
}
