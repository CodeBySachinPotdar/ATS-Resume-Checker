package com.resumematch.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.domain.TemplateType;
import com.resumematch.entity.Resume;
import com.resumematch.exception.InvalidRequestException;
import com.resumematch.parser.ResumeTextParserHelper;
import com.resumematch.parser.model.ResumeContent;
import org.springframework.stereotype.Service;

@Service
public class ResumeExportService {

    private final PdfExportGenerator pdfGenerator;
    private final DocxExportGenerator docxGenerator;
    private final TxtExportGenerator txtGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumeExportService(
            PdfExportGenerator pdfGenerator,
            DocxExportGenerator docxGenerator,
            TxtExportGenerator txtGenerator
    ) {
        this.pdfGenerator = pdfGenerator;
        this.docxGenerator = docxGenerator;
        this.txtGenerator = txtGenerator;
    }

    public byte[] export(Resume resume, String format, TemplateType templateType) {
        ResumeContent content;
        try {
            if (resume.getParsedJson() != null && !resume.getParsedJson().isBlank()) {
                content = objectMapper.readValue(resume.getParsedJson(), ResumeContent.class);
            } else {
                content = ResumeTextParserHelper.parseFromText(resume.getRawText());
            }
        } catch (Exception e) {
            content = ResumeTextParserHelper.parseFromText(resume.getRawText());
        }

        return exportContent(content, format, templateType);
    }

    public byte[] exportContent(ResumeContent content, String format, TemplateType templateType) {
        if (templateType == null) {
            templateType = TemplateType.CLASSIC_PROFESSIONAL;
        }

        try {
            String fmt = (format != null) ? format.toLowerCase().trim() : "pdf";
            return switch (fmt) {
                case "pdf" -> pdfGenerator.generatePdf(content, templateType);
                case "docx", "doc" -> docxGenerator.generateDocx(content, templateType);
                case "txt", "text" -> txtGenerator.generateTxt(content, templateType);
                default -> throw new InvalidRequestException("Unsupported export format: " + format + ". Supported formats: pdf, docx, txt.");
            };
        } catch (InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to export resume document: " + e.getMessage(), e);
        }
    }
}
