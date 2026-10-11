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
            config.setJdbcUrl(PROPS.getProperty("db.url"));
            config.setUsername(PROPS.getProperty("db.username"));
            config.setPassword(PROPS.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(PROPS.getProperty("db.pool.size", "10")));
            config.setPoolName("ClinicConnectionPool");

            DATA_SOURCE = new HikariDataSource(config);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load db.properties: " + e.getMessage());
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
