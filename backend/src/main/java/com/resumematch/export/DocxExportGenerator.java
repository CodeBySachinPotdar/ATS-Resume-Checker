package com.resumematch.export;

import com.resumematch.domain.TemplateType;
import com.resumematch.parser.model.*;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class DocxExportGenerator {

    public byte[] generateDocx(ResumeContent content, TemplateType templateType) throws Exception {
        try (XWPFDocument doc = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            ContactInfo contact = content.getContactInfo();

            // Candidate Name
            XWPFParagraph titlePara = doc.createParagraph();
            titlePara.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun titleRun = titlePara.createRun();
            titleRun.setText(contact.getName() != null ? contact.getName() : "Professional Candidate");
            titleRun.setBold(true);
            titleRun.setFontSize(20);
            titleRun.setFontFamily("Calibri");

            // Contact Info
            XWPFParagraph contactPara = doc.createParagraph();
            XWPFRun contactRun = contactPara.createRun();
            StringBuilder contactLine = new StringBuilder();
            if (contact.getEmail() != null) contactLine.append(contact.getEmail()).append(" | ");
            if (contact.getPhone() != null) contactLine.append(contact.getPhone()).append(" | ");
            if (contact.getLocation() != null) contactLine.append(contact.getLocation()).append(" | ");
            if (contact.getLinkedIn() != null) contactLine.append(contact.getLinkedIn());
            contactRun.setText(contactLine.toString().replaceAll(" \\| $", ""));
            contactRun.setFontSize(10);
            contactRun.setFontFamily("Calibri");

            // Professional Summary
            if (content.getSummary() != null && !content.getSummary().isBlank()) {
                addHeading(doc, "PROFESSIONAL SUMMARY");
                XWPFParagraph sumPara = doc.createParagraph();
                XWPFRun sumRun = sumPara.createRun();
                sumRun.setText(content.getSummary());
                sumRun.setFontSize(11);
                sumRun.setFontFamily("Calibri");
            }

            // Skills
            if (!content.getSkills().isEmpty()) {
                addHeading(doc, "TECHNICAL SKILLS");
                XWPFParagraph skillPara = doc.createParagraph();
                XWPFRun skillRun = skillPara.createRun();
                skillRun.setText(String.join(", ", content.getSkills()));
                skillRun.setFontSize(11);
                skillRun.setFontFamily("Calibri");
            }

            // Experience
            if (!content.getExperiences().isEmpty()) {
                addHeading(doc, "PROFESSIONAL EXPERIENCE");
                for (WorkExperience exp : content.getExperiences()) {
                    XWPFParagraph jobPara = doc.createParagraph();
                    XWPFRun jobRun = jobPara.createRun();
                    jobRun.setBold(true);
                    jobRun.setFontFamily("Calibri");
                    jobRun.setFontSize(11);
                    String title = exp.getJobTitle() != null ? exp.getJobTitle() : "Software Engineer";
                    String company = exp.getCompany() != null ? exp.getCompany() : "Company";
                    String dates = (exp.getStartDate() != null ? exp.getStartDate() : "") + " - " + (exp.getEndDate() != null ? exp.getEndDate() : "Present");
                    jobRun.setText(title + " | " + company + " (" + dates + ")");

                    for (String bullet : exp.getBulletPoints()) {
                        XWPFParagraph bulletPara = doc.createParagraph();
                        bulletPara.setIndentationLeft(360);
                        XWPFRun bulletRun = bulletPara.createRun();
                        bulletRun.setFontFamily("Calibri");
                        bulletRun.setFontSize(10);
                        bulletRun.setText("• " + bullet);
                    }
                }
            }

            // Education
            if (!content.getEducations().isEmpty()) {
                addHeading(doc, "EDUCATION");
                for (EducationItem edu : content.getEducations()) {
                    XWPFParagraph eduPara = doc.createParagraph();
                    XWPFRun eduRun = eduPara.createRun();
                    eduRun.setFontFamily("Calibri");
                    eduRun.setFontSize(11);
                    String deg = edu.getDegree() != null ? edu.getDegree() : "Degree";
                    String inst = edu.getInstitution() != null ? " - " + edu.getInstitution() : "";
                    String yr = edu.getGraduationYear() != null ? " (" + edu.getGraduationYear() + ")" : "";
                    eduRun.setText(deg + inst + yr);
                }
            }

            // Certifications
            if (!content.getCertifications().isEmpty()) {
                addHeading(doc, "CERTIFICATIONS");
                for (CertificationItem cert : content.getCertifications()) {
                    XWPFParagraph certPara = doc.createParagraph();
                    certPara.setIndentationLeft(360);
                    XWPFRun certRun = certPara.createRun();
                    certRun.setFontFamily("Calibri");
                    certRun.setFontSize(10);
                    certRun.setText("• " + cert.getName());
                }
            }

            doc.write(out);
            return out.toByteArray();
        }
    }

    private void addHeading(XWPFDocument doc, String text) {
        XWPFParagraph headingPara = doc.createParagraph();
        headingPara.setSpacingBefore(200);
        XWPFRun headingRun = headingPara.createRun();
        headingRun.setBold(true);
        headingRun.setFontSize(13);
        headingRun.setFontFamily("Calibri");
        headingRun.setText(text);
    }
}
