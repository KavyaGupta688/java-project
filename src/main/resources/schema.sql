-- E-Healthcare Clinic Manager - Embedded & Local Database Schema

CREATE TABLE IF NOT EXISTS Users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)        NOT NULL,
    email         VARCHAR(150)        NOT NULL UNIQUE,
    password_hash VARCHAR(255)        NOT NULL,
    role          ENUM('PATIENT','DOCTOR') NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS Doctors (
    doctor_id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT NOT NULL UNIQUE,
    specialization    VARCHAR(100) NOT NULL,
    consultation_fee  DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Patients (
    patient_id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id              INT NOT NULL UNIQUE,
    blood_group          VARCHAR(5),
    emergency_contact    VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

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
);

CREATE TABLE IF NOT EXISTS Medical_Docs (
    doc_id       INT AUTO_INCREMENT PRIMARY KEY,
    patient_id   INT NOT NULL,
    file_path    VARCHAR(500) NOT NULL,
    doc_type     VARCHAR(50)  NOT NULL DEFAULT 'PDF',
    upload_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_appt_doctor_date ON Appointments(doctor_id, appt_date);
CREATE INDEX IF NOT EXISTS idx_appt_patient     ON Appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_docs_patient     ON Medical_Docs(patient_id);
