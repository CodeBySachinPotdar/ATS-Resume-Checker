package com.resumematch.export;

import com.resumematch.domain.TemplateType;
import com.resumematch.parser.model.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TxtExportGenerator {

    public byte[] generateTxt(ResumeContent content, TemplateType templateType) {
        StringBuilder sb = new StringBuilder();

        // Header
        ContactInfo contact = content.getContactInfo();
        String name = contact.getName() != null ? contact.getName() : "Professional Candidate";
        sb.append(name.toUpperCase()).append("\n");

        StringBuilder contactLine = new StringBuilder();
        if (contact.getEmail() != null) contactLine.append(contact.getEmail()).append(" | ");
        if (contact.getPhone() != null) contactLine.append(contact.getPhone()).append(" | ");
        if (contact.getLocation() != null) contactLine.append(contact.getLocation()).append(" | ");
        if (contact.getLinkedIn() != null) contactLine.append(contact.getLinkedIn());
        sb.append(contactLine.toString().replaceAll(" \\| $", "")).append("\n\n");

        // Summary
        if (content.getSummary() != null && !content.getSummary().isBlank()) {
            sb.append("PROFESSIONAL SUMMARY\n");
            sb.append("--------------------\n");
            sb.append(content.getSummary()).append("\n\n");
        }

        // Skills
        if (!content.getSkills().isEmpty()) {
            sb.append("TECHNICAL SKILLS\n");
            sb.append("----------------\n");
            sb.append(String.join(", ", content.getSkills())).append("\n\n");
        }

        // Experience
        if (!content.getExperiences().isEmpty()) {
            sb.append("PROFESSIONAL EXPERIENCE\n");
            sb.append("-----------------------\n");
            for (WorkExperience exp : content.getExperiences()) {
                String title = exp.getJobTitle() != null ? exp.getJobTitle() : "Engineer";
                String company = exp.getCompany() != null ? exp.getCompany() : "Company";
                String dates = (exp.getStartDate() != null ? exp.getStartDate() : "") + " - " + (exp.getEndDate() != null ? exp.getEndDate() : "Present");

                sb.append(title).append(" - ").append(company).append(" (").append(dates).append(")\n");
                for (String bullet : exp.getBulletPoints()) {
                    sb.append("  * ").append(bullet).append("\n");
                }
                sb.append("\n");
            }
        }

        // Education
        if (!content.getEducations().isEmpty()) {
            sb.append("EDUCATION\n");
            sb.append("---------\n");
            for (EducationItem edu : content.getEducations()) {
                sb.append(edu.getDegree() != null ? edu.getDegree() : "Degree");
                if (edu.getInstitution() != null) sb.append(", ").append(edu.getInstitution());
                if (edu.getGraduationYear() != null) sb.append(" (").append(edu.getGraduationYear()).append(")");
                sb.append("\n");
            }
            sb.append("\n");
        }

        // Certifications
        if (!content.getCertifications().isEmpty()) {
            sb.append("CERTIFICATIONS\n");
            sb.append("--------------\n");
            for (CertificationItem cert : content.getCertifications()) {
                sb.append("  * ").append(cert.getName()).append("\n");
            }
            sb.append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
