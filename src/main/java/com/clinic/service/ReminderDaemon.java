package com.clinic.service;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.dao.impl.AppointmentDAOImpl;
import com.clinic.model.Appointment;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Background task run periodically by a ScheduledExecutorService
 * (see AppLifecycleListener). Finds appointments happening today or
 * tomorrow that have not been reminded about yet, and "sends" a
 * reminder (logged to the server console - no external email/SMS
 * API is wired up, so no API key is required to run this project).
 */
public class ReminderDaemon implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ReminderDaemon.class.getName());

    private final IAppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    public void run() {
        try {
            List<Appointment> due = appointmentDAO.findUpcomingNeedingReminder(24 * 60);
            for (Appointment appt : due) {
                sendReminder(appt);
                appointmentDAO.markReminderSent(appt.getApptId());
            }
            if (!due.isEmpty()) {
                LOGGER.info("[ReminderDaemon] Sent " + due.size() + " reminder(s).");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[ReminderDaemon] Failed to process reminders", e);
        }
    }

    private void sendReminder(Appointment appt) {
        LOGGER.info(String.format(
                "[ReminderDaemon] Reminder -> %s: appointment with Dr. %s on %s at %s (severity %d)",
                appt.getPatientName(), appt.getDoctorName(), appt.getApptDate(),
                appt.getTimeSlot(), appt.getSeverityLevel()));
    }
}
