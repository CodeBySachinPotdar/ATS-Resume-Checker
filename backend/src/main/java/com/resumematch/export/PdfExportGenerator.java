package com.resumematch.export;

import com.resumematch.domain.TemplateType;
import com.resumematch.parser.model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfExportGenerator {

    public byte[] generatePdf(ResumeContent content, TemplateType templateType) throws Exception {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDPageContentStream stream = new PDPageContentStream(document, page);
            float margin = 50;
            float yPosition = 740;
            float leading = 14;

            ContactInfo contact = content.getContactInfo();

            // Candidate Name
            stream.beginText();
            stream.setFont(fontBold, 18);
            stream.newLineAtOffset(margin, yPosition);
            stream.showText(contact.getName() != null ? contact.getName() : "Candidate Name");
            stream.endText();
            yPosition -= 22;

            // Contact Info
            StringBuilder contactLine = new StringBuilder();
            if (contact.getEmail() != null) contactLine.append(contact.getEmail()).append(" | ");
            if (contact.getPhone() != null) contactLine.append(contact.getPhone()).append(" | ");
            if (contact.getLocation() != null) contactLine.append(contact.getLocation()).append(" | ");
            if (contact.getLinkedIn() != null) contactLine.append(contact.getLinkedIn());

            stream.beginText();
            stream.setFont(fontRegular, 9);
            stream.newLineAtOffset(margin, yPosition);
            stream.showText(sanitize(contactLine.toString().replaceAll(" \\| $", "")));
            stream.endText();
            yPosition -= 25;

            // Summary
            if (content.getSummary() != null && !content.getSummary().isBlank()) {
                yPosition = drawSectionHeading(stream, fontBold, margin, yPosition, "PROFESSIONAL SUMMARY");
                List<String> wrapped = wrapText(content.getSummary(), 80);
                for (String line : wrapped) {
                    if (yPosition < 60) break;
                    stream.beginText();
                    stream.setFont(fontRegular, 10);
                    stream.newLineAtOffset(margin, yPosition);
                    stream.showText(sanitize(line));
                    stream.endText();
                    yPosition -= leading;
                }
                yPosition -= 10;
            }

            // Skills
            if (!content.getSkills().isEmpty() && yPosition > 80) {
                yPosition = drawSectionHeading(stream, fontBold, margin, yPosition, "TECHNICAL SKILLS");
                String skillText = String.join(", ", content.getSkills());
                List<String> wrapped = wrapText(skillText, 80);
                for (String line : wrapped) {
                    if (yPosition < 60) break;
                    stream.beginText();
                    stream.setFont(fontRegular, 10);
                    stream.newLineAtOffset(margin, yPosition);
                    stream.showText(sanitize(line));
                    stream.endText();
                    yPosition -= leading;
                }
                yPosition -= 10;
            }

            // Experience
            if (!content.getExperiences().isEmpty() && yPosition > 80) {
                yPosition = drawSectionHeading(stream, fontBold, margin, yPosition, "WORK EXPERIENCE");
                for (WorkExperience exp : content.getExperiences()) {
                    if (yPosition < 80) break;
                    String title = exp.getJobTitle() != null ? exp.getJobTitle() : "Software Engineer";
                    String company = exp.getCompany() != null ? exp.getCompany() : "Company";
                    String dates = (exp.getStartDate() != null ? exp.getStartDate() : "") + " - " + (exp.getEndDate() != null ? exp.getEndDate() : "Present");

                    stream.beginText();
                    stream.setFont(fontBold, 10);
                    stream.newLineAtOffset(margin, yPosition);
                    stream.showText(sanitize(title + " | " + company + " (" + dates + ")"));
                    stream.endText();
                    yPosition -= leading;

                    for (String bullet : exp.getBulletPoints()) {
                        if (yPosition < 60) break;
                        List<String> bulletWrapped = wrapText("• " + bullet, 85);
                        for (String bLine : bulletWrapped) {
                            if (yPosition < 60) break;
                            stream.beginText();
                            stream.setFont(fontRegular, 9);
                            stream.newLineAtOffset(margin + 10, yPosition);
                            stream.showText(sanitize(bLine));
                            stream.endText();
                            yPosition -= leading;
                        }
                    }
                    yPosition -= 5;
                }
            }

            // Education
            if (!content.getEducations().isEmpty() && yPosition > 80) {
                yPosition = drawSectionHeading(stream, fontBold, margin, yPosition, "EDUCATION");
                for (EducationItem edu : content.getEducations()) {
                    if (yPosition < 60) break;
                    String deg = edu.getDegree() != null ? edu.getDegree() : "Degree";
                    String inst = edu.getInstitution() != null ? " - " + edu.getInstitution() : "";
                    String yr = edu.getGraduationYear() != null ? " (" + edu.getGraduationYear() + ")" : "";

                    stream.beginText();
                    stream.setFont(fontRegular, 10);
                    stream.newLineAtOffset(margin, yPosition);
                    stream.showText(sanitize(deg + inst + yr));
                    stream.endText();
                    yPosition -= leading;
                }
            }

            stream.close();
            document.save(out);
            return out.toByteArray();
        }
    }

    private float drawSectionHeading(PDPageContentStream stream, PDType1Font font, float margin, float y, String heading) throws Exception {
        stream.beginText();
        stream.setFont(font, 12);
        stream.newLineAtOffset(margin, y);
        stream.showText(heading);
        stream.endText();
        return y - 16;
    }

    private List<String> wrapText(String text, int maxCharsPerLine) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split("\\s+");
        StringBuilder current = new StringBuilder();

        for (String w : words) {
            if (current.length() + w.length() + 1 > maxCharsPerLine) {
                lines.add(current.toString());
                current = new StringBuilder(w);
            } else {
                if (!current.isEmpty()) current.append(" ");
                current.append(w);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    private String sanitize(String input) {
        if (input == null) return "";
        // Replace unicode bullets and non-latin1 characters for Standard14 Fonts
        return input.replace("•", "*")
                .replaceAll("[^\\x00-\\x7F]", " ");
    }
}
