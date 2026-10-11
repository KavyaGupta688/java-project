package com.clinic.service;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.dao.impl.AppointmentDAOImpl;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Background task that purges (marks EXPIRED) bookings whose date has
 * already passed but were never confirmed/completed/cancelled, so
 * stale rows don't clutter doctor queues or patient dashboards.
 */
public class CleanupDaemon implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(CleanupDaemon.class.getName());

    private final IAppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    @Override
    public void run() {
        try {
            int count = appointmentDAO.expireOldAppointments();
            if (count > 0) {
                LOGGER.info("[CleanupDaemon] Expired " + count + " stale appointment(s).");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "[CleanupDaemon] Failed to expire old appointments", e);
        }
    }
}
