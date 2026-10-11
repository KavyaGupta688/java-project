package com.clinic.dao;

import com.clinic.model.Appointment;
import com.clinic.model.AppointmentStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface IAppointmentDAO {

    int insertAppointment(Appointment appt) throws SQLException;

    List<Appointment> findByPatient(int patientId) throws SQLException;

    List<Appointment> findByDoctorAndDate(int doctorId, LocalDate date) throws SQLException;

    boolean isSlotTaken(int doctorId, LocalDate date, String timeSlot) throws SQLException;

    void updateStatus(int apptId, AppointmentStatus status) throws SQLException;

    /** Appointments happening within the next `withinMinutes` minutes that have not had a reminder sent yet. */
    List<Appointment> findUpcomingNeedingReminder(int withinMinutes) throws SQLException;

    void markReminderSent(int apptId) throws SQLException;

    /** PENDING/CONFIRMED appointments whose date has already passed - candidates for the cleanup daemon. */
    List<Appointment> findExpired() throws SQLException;

    int expireOldAppointments() throws SQLException;
}
