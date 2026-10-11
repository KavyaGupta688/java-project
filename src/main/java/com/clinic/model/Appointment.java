package com.clinic.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Polymorphism: implements Comparable so a java.util.PriorityQueue of
 * Appointments automatically orders itself by medical severity
 * (1 = most critical ... 5 = routine), and within the same severity,
 * by whichever appointment was created first (FIFO tie-break).
 *
 * Severity lives on the Appointment (matches the Appointments table,
 * since the same patient can have different severities on different
 * visits) rather than on Patient.
 */
public class Appointment implements Comparable<Appointment> {

    private int apptId;
    private int doctorId;
    private int patientId;
    private LocalDate apptDate;
    private String timeSlot;
    private AppointmentStatus status;
    private int severityLevel;
    private String reason;
    private boolean reminderSent;
    private LocalDateTime createdAt;

    // Populated by joined DAO queries for display purposes only.
    private String patientName;
    private String doctorName;

    public Appointment() {
    }

    public Appointment(int apptId, int doctorId, int patientId, LocalDate apptDate,
                        String timeSlot, AppointmentStatus status, int severityLevel,
                        String reason, boolean reminderSent, LocalDateTime createdAt) {
        this.apptId = apptId;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.apptDate = apptDate;
        this.timeSlot = timeSlot;
        this.status = status;
        this.severityLevel = severityLevel;
        this.reason = reason;
        this.reminderSent = reminderSent;
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(Appointment other) {
        int bySeverity = Integer.compare(this.severityLevel, other.severityLevel);
        if (bySeverity != 0) {
            return bySeverity;
        }
        if (this.createdAt != null && other.createdAt != null) {
            return this.createdAt.compareTo(other.createdAt);
        }
        return 0;
    }

    public int getApptId() {
        return apptId;
    }

    public void setApptId(int apptId) {
        this.apptId = apptId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public LocalDate getApptDate() {
        return apptDate;
    }

    public void setApptDate(LocalDate apptDate) {
        this.apptDate = apptDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public int getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(int severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
