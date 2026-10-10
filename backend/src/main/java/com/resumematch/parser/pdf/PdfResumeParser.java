package com.resumematch.parser.pdf;

import com.resumematch.exception.ResumeParsingException;
import com.resumematch.parser.ResumeParser;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PdfResumeParser implements ResumeParser {

    @Override
    public boolean supports(String fileType) {
        if (fileType == null) return false;
        String type = fileType.toLowerCase().trim();
        return type.endsWith("pdf") || type.contains("pdf");
    }

    @Override
    public ResumeContent parse(InputStream inputStream) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            try (PDDocument document = Loader.loadPDF(bytes)) {
                if (document.isEncrypted()) {
                    throw new ResumeParsingException("PDF is password protected. Please remove password encryption before uploading.");
                }
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                String extractedText = stripper.getText(document);

                if (extractedText == null || extractedText.trim().length() < 30) {
                    ResumeContent fallback = new ResumeContent();
                    fallback.setRawText(extractedText != null ? extractedText : "");
                    fallback.getFormattingInspection().setTextIsMachineReadable(false);
                    fallback.getFormattingInspection().setScore(30.0);
                    fallback.getFormattingInspection().getCriticalIssues().add(
                            "This PDF appears to be an image scan or flattened vector graphics with very little extractable text. An OCR tool may be required."
                    );
                    return fallback;
                }

                ResumeContent content = ResumeTextParserHelper.parseFromText(extractedText);
                return content;
            }
        } catch (ResumeParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ResumeParsingException("Failed to parse PDF resume: " + e.getMessage(), e);
        }
    }
}
