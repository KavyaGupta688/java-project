package com.clinic.service;

import com.clinic.dao.IAppointmentDAO;
import com.clinic.dao.impl.AppointmentDAOImpl;
import com.clinic.model.Appointment;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Core triage/sorting logic. Pulls a doctor's queue for a given day
 * and drains it through a PriorityQueue so the most severe cases
 * (lowest severity_level) come out first, regardless of booking order.
 */
public class TriageService {

    private final IAppointmentDAO appointmentDAO = new AppointmentDAOImpl();

    public List<Appointment> getTriagedQueue(int doctorId, LocalDate date) throws SQLException {
        List<Appointment> raw = appointmentDAO.findByDoctorAndDate(doctorId, date);

        PriorityQueue<Appointment> queue = new PriorityQueue<>(raw);

        List<Appointment> ordered = new ArrayList<>(raw.size());
        while (!queue.isEmpty()) {
            ordered.add(queue.poll());
        }
        return ordered;
    }
}
