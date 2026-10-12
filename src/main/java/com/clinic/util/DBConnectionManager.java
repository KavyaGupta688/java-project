package com.clinic.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central JDBC connection pool (HikariCP) built once from
 * src/main/resources/db.properties and shared by every DAO.
 */
public final class DBConnectionManager {

    private static final HikariDataSource DATA_SOURCE;
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnectionManager.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new IllegalStateException("db.properties not found on classpath");
            }
            PROPS.load(in);

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            config.setJdbcUrl(PROPS.getProperty("db.url"));
            config.setUsername(PROPS.getProperty("db.username"));
            config.setPassword(PROPS.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(PROPS.getProperty("db.pool.size", "10")));
            config.setPoolName("ClinicConnectionPool");

            DATA_SOURCE = new HikariDataSource(config);
            initDatabase();
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load db.properties: " + e.getMessage());
        }
    }

    private static void initDatabase() {
        try (Connection con = DATA_SOURCE.getConnection();
             java.sql.Statement st = con.createStatement()) {
            boolean hasTables = false;
            try {
                st.executeQuery("SELECT 1 FROM Users LIMIT 1");
                hasTables = true;
            } catch (SQLException ignored) {
            }

            if (!hasTables) {
                try (InputStream in = DBConnectionManager.class.getClassLoader().getResourceAsStream("schema.sql")) {
                    if (in != null) {
                        String script = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
                        for (String statement : script.split(";")) {
                            String trimmed = statement.trim();
                            if (!trimmed.isEmpty()) {
                                st.execute(trimmed);
                            }
                        }
                    }
                }

                // Seed sample doctor & patient for instant out-of-the-box demo
                try {
                    String docHash = PasswordUtil.hash("doctor123");
                    st.execute("INSERT INTO Users (name, email, password_hash, role) VALUES ('Dr. Sarah Smith', 'doctor@clinic.com', '" + docHash + "', 'DOCTOR')");
                    st.execute("INSERT INTO Doctors (user_id, specialization, consultation_fee) VALUES (1, 'Cardiology', 75.00)");

                    String patHash = PasswordUtil.hash("patient123");
                    st.execute("INSERT INTO Users (name, email, password_hash, role) VALUES ('John Doe', 'patient@clinic.com', '" + patHash + "', 'PATIENT')");
                    st.execute("INSERT INTO Patients (user_id, blood_group, emergency_contact) VALUES (2, 'O+', '555-0199')");
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(DBConnectionManager.class.getName())
                    .log(java.util.logging.Level.WARNING, "Auto-init database notice: " + e.getMessage());
        }
    }

    private DBConnectionManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    public static String getUploadDir() {
        return PROPS.getProperty("upload.dir");
    }

    public static void shutdown() {
        if (DATA_SOURCE != null && !DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
    }
}
