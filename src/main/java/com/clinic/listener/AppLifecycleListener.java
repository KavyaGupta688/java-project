package com.clinic.listener;

import com.clinic.service.CleanupDaemon;
import com.clinic.service.ReminderDaemon;
import com.clinic.util.DBConnectionManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Wires up the two background daemon threads described in the
 * architecture diagram: an automated-reminder worker and an
 * expired-booking cleanup worker. Both run on a shared
 * ScheduledExecutorService whose threads are marked daemon so they
 * never block server shutdown.
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(AppLifecycleListener.class.getName());

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Make sure the local upload directory exists before any file upload is attempted.
        File uploadDir = new File(DBConnectionManager.getUploadDir());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        ThreadFactory daemonFactory = r -> {
            Thread t = new Thread(r, "clinic-background-worker");
            t.setDaemon(true);
            return t;
        };
        scheduler = Executors.newScheduledThreadPool(2, daemonFactory);

        // Reminder sweep every 60 seconds; cleanup sweep every 5 minutes.
        scheduler.scheduleAtFixedRate(new ReminderDaemon(), 10, 60, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(new CleanupDaemon(), 20, 300, TimeUnit.SECONDS);

        LOGGER.info("E-Healthcare Clinic Manager started - background daemon threads active.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        DBConnectionManager.shutdown();
        LOGGER.info("E-Healthcare Clinic Manager stopped - background threads and DB pool closed.");
    }
}
