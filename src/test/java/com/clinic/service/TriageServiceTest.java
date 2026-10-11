package com.clinic.service;

import com.clinic.model.Appointment;
import com.clinic.model.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the triage ordering used by TriageService:
 * a PriorityQueue of Appointments must drain most-severe-first,
 * FIFO within the same severity (matches Appointment.compareTo).
 */
class TriageServiceTest {

    private Appointment appt(int severity, LocalDateTime createdAt, int id) {
        return new Appointment(id, 1, 1, LocalDate.of(2026, 10, 12),
                "10:00", AppointmentStatus.PENDING, severity, "test", false, createdAt);
    }

    @Test
    void drainsMostSevereFirst() {
        List<Appointment> raw = new ArrayList<>(List.of(
                appt(5, LocalDateTime.now(), 1),
                appt(1, LocalDateTime.now(), 2),   // critical — must come out first
                appt(3, LocalDateTime.now(), 3),
                appt(2, LocalDateTime.now(), 4)
        ));

        PriorityQueue<Appointment> queue = new PriorityQueue<>(raw);
        List<Integer> severityOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            severityOrder.add(queue.poll().getSeverityLevel());
        }

        assertEquals(List.of(1, 2, 3, 5), severityOrder);
    }

    @Test
    void breaksSeverityTiesFifo() {
        LocalDateTime base = LocalDateTime.of(2026, 10, 12, 8, 0).withNano(0);
        List<Appointment> raw = new ArrayList<>(List.of(
                appt(3, base.plusMinutes(30), 1),
                appt(3, base, 2),
                appt(3, base.plusMinutes(10), 3)
        ));

        PriorityQueue<Appointment> queue = new PriorityQueue<>(raw);
        List<Integer> idOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            idOrder.add(queue.poll().getApptId());
        }

        // Same severity → earliest created_at first: ids 2, 3, 1
        assertEquals(List.of(2, 3, 1), idOrder);
    }

    @Test
    void stressRandomizedOrderingsProduceSortedOutput() {
        // Stress test: 2000 random severity/created_at mixes must always
        // drain fully sorted by (severity, createdAt) — guards against
        // regressions in compareTo() and confirms queue consistency.
        Random rng = new Random(42);
        List<Appointment> raw = new ArrayList<>();
        LocalDateTime base = LocalDateTime.of(2026, 10, 12, 9, 0).withNano(0);
        for (int i = 0; i < 2000; i++) {
            raw.add(appt(1 + rng.nextInt(5), base.plusSeconds(rng.nextInt(360000)), i + 1));
        }

        PriorityQueue<Appointment> queue = new PriorityQueue<>(raw);
        List<Appointment> drained = new ArrayList<>(raw.size());
        while (!queue.isEmpty()) {
            drained.add(queue.poll());
        }

        Comparator<Appointment> expected =
                Comparator.comparingInt(Appointment::getSeverityLevel)
                          .thenComparing(Appointment::getCreatedAt);
        for (int i = 1; i < drained.size(); i++) {
            assertTrue(expected.compare(drained.get(i - 1), drained.get(i)) <= 0,
                    "Queue output not sorted at index " + i);
        }
        assertEquals(raw.size(), drained.size(), "Every appointment must drain exactly once");
    }

    @Test
    void stressConcurrentPollsStayConsistent() throws Exception {
        // Thread-safety stress: several threads draining one shared queue
        // concurrently must collectively retrieve every element exactly once.
        List<Appointment> raw = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            raw.add(appt(1 + (i % 5), LocalDateTime.of(2026, 10, 12, 7, 0).withNano(0).plusSeconds(i), i + 1));
        }
        PriorityQueue<Appointment> queue = new PriorityQueue<>(raw);

        List<Appointment> collected = java.util.Collections.synchronizedList(new ArrayList<>());
        Thread[] threads = new Thread[4];
        for (int t = 0; t < threads.length; t++) {
            threads[t] = new Thread(() -> {
                // PriorityQueue.poll is not thread-safe by itself; the webapp's
                // TriageService drains on a single request thread. Here we show
                // the "safe" pattern (synchronized drain) that keeps results
                // consistent when access must be shared.
                while (true) {
                    Appointment next;
                    synchronized (queue) {
                        next = queue.poll();
                    }
                    if (next == null) break;
                    collected.add(next);
                }
            });
            threads[t].start();
        }
        for (Thread t : threads) {
            t.join();
        }

        assertEquals(raw.size(), collected.size(),
                "Concurrent drains must not lose or duplicate elements");
    }
}
