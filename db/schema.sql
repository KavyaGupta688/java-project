-- ============================================================
-- E-Healthcare Clinic Manager - Database Schema
-- Run this once against a local MySQL server:
--   mysql -u root -p < db/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS clinic_db;
USE clinic_db;

-- ------------------------------------------------------------
-- Users: base authentication table shared by patients & doctors
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)        NOT NULL,
    email         VARCHAR(150)        NOT NULL UNIQUE,
    password_hash VARCHAR(255)        NOT NULL,
    role          ENUM('PATIENT','DOCTOR') NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Doctors: 1-1 extension of Users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Doctors (
    doctor_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT NOT NULL UNIQUE,
    specialization    VARCHAR(100) NOT NULL,
    consultation_fee  DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Patients: 1-1 extension of Users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Patients (
    patient_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id              INT NOT NULL UNIQUE,
    blood_group          VARCHAR(5),
    emergency_contact    VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Appointments: booking + triage severity lives here
-- severity_level: 1 = CRITICAL ... 5 = ROUTINE (lower = more urgent)
-- status: PENDING, CONFIRMED, COMPLETED, CANCELLED, EXPIRED
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Appointments (
    appt_id        INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id      INT NOT NULL,
    patient_id     INT NOT NULL,
    appt_date      DATE NOT NULL,
    time_slot      VARCHAR(20) NOT NULL,
    status         ENUM('PENDING','CONFIRMED','COMPLETED','CANCELLED','EXPIRED') NOT NULL DEFAULT 'PENDING',
    severity_level INT NOT NULL DEFAULT 5,
    reason         VARCHAR(255),
    reminder_sent  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id)  REFERENCES Doctors(doctor_id)   ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Medical_Docs: uploaded PDF/text history files, path stored
-- only (actual files live on local disk under the upload dir)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Medical_Docs (
    doc_id       INT AUTO_INCREMENT PRIMARY KEY,
    patient_id   INT NOT NULL,
    file_path    VARCHAR(500) NOT NULL,
    doc_type     VARCHAR(50)  NOT NULL DEFAULT 'PDF',
    upload_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Helpful indexes for frequent lookups
CREATE INDEX idx_appt_doctor_date ON Appointments(doctor_id, appt_date);
CREATE INDEX idx_appt_patient     ON Appointments(patient_id);
CREATE INDEX idx_docs_patient     ON Medical_Docs(patient_id);
