package com.clinic.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * Extracts readable text from an uploaded patient medical-history PDF.
 * Used by the upload servlet so the parsed contents can be stored
 * alongside the file path in the Medical_Docs table (slide 3 of the
 * project statement: "uploading and parsing of patient history PDFs").
 */
public final class PdfTextExtractor {

    private static final int MAX_CHARS = 100_000; // safety cap: don't store mega-pages in DB

    private PdfTextExtractor() {
    }

    /**
     * Parses a PDF and returns its plain text (truncated to MAX_CHARS).
     * Returns null for non-PDF/unsupported files so callers can store
     * them un-parsed without failing the upload.
     */
    public static String extractText(File pdf) {
        if (pdf == null || !pdf.exists()) {
            return null;
        }
        try (PDDocument document = org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
            if (document.isEncrypted()) {
                // Encryption means we probably can't legally/machine-read it — skip parsing.
                return null;
            }
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setEndPage(Math.min(document.getNumberOfPages(), 50)); // cap pages too
            String text = stripper.getText(document);
            if (text == null) {
                return null;
            }
            return text.length() > MAX_CHARS ? text.substring(0, MAX_CHARS) : text;
        } catch (IOException e) {
            // Corrupt/truncated PDF — record it but don't fail the whole upload.
            return null;
        }
    }
}
