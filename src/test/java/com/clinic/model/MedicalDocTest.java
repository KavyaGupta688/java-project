package com.clinic.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Sanity tests for the model layer (encapsulation: getters/setters round-trip). */
class MedicalDocTest {

    @Test
    void storesAllFieldsThroughGettersAndSetters() {
        MedicalDoc doc = new MedicalDoc();
        LocalDateTime now = LocalDateTime.of(2026, 10, 12, 10, 30);

        doc.setDocId(7);
        doc.setPatientId(3);
        doc.setFilePath("uploads/patient_3_x.pdf");
        doc.setDocType("PDF");
        doc.setExtractedText("Patient reports chest pain for two days.");
        doc.setUploadDate(now);

        assertAll(
                () -> assertEquals(7, doc.getDocId()),
                () -> assertEquals(3, doc.getPatientId()),
                () -> assertEquals("uploads/patient_3_x.pdf", doc.getFilePath()),
                () -> assertEquals("PDF", doc.getDocType()),
                () -> assertEquals("Patient reports chest pain for two days.", doc.getExtractedText()),
                () -> assertEquals(now, doc.getUploadDate())
        );
    }

    @Test
    void legacyConstructorLeavesExtractedTextNull() {
        MedicalDoc doc = new MedicalDoc(1, 2, "a.pdf", "PDF", LocalDateTime.now());
        assertNull(doc.getExtractedText());
    }

    @Test
    void fullConstructorCarriesExtractedText() {
        MedicalDoc doc = new MedicalDoc(1, 2, "a.pdf", "PDF", "some parsed text", LocalDateTime.now());
        assertEquals("some parsed text", doc.getExtractedText());
    }
}
