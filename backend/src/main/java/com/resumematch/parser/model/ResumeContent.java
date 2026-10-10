package com.resumematch.parser.model;

import java.util.ArrayList;
import java.util.List;

public class ResumeContent {
    private String rawText = "";
    private ContactInfo contactInfo = new ContactInfo();
    private String summary = "";
    private List<String> skills = new ArrayList<>();
    private List<String> technicalSkills = new ArrayList<>();
    private List<String> softSkills = new ArrayList<>();
    private List<WorkExperience> experiences = new ArrayList<>();
    private List<EducationItem> educations = new ArrayList<>();
    private List<CertificationItem> certifications = new ArrayList<>();
    private List<ProjectItem> projects = new ArrayList<>();
    private List<String> achievements = new ArrayList<>();
    private FormattingInspection formattingInspection = new FormattingInspection();
    private Double estimatedYearsExperience = 0.0;

    public ResumeContent() {}

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public ContactInfo getContactInfo() { return contactInfo; }
    public void setContactInfo(ContactInfo contactInfo) { this.contactInfo = contactInfo; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(List<String> technicalSkills) { this.technicalSkills = technicalSkills; }

    public List<String> getSoftSkills() { return softSkills; }
    public void setSoftSkills(List<String> softSkills) { this.softSkills = softSkills; }

    public List<WorkExperience> getExperiences() { return experiences; }
    public void setExperiences(List<WorkExperience> experiences) { this.experiences = experiences; }

    public List<EducationItem> getEducations() { return educations; }
    public void setEducations(List<EducationItem> educations) { this.educations = educations; }

    public List<CertificationItem> getCertifications() { return certifications; }
    public void setCertifications(List<CertificationItem> certifications) { this.certifications = certifications; }

    public List<ProjectItem> getProjects() { return projects; }
    public void setProjects(List<ProjectItem> projects) { this.projects = projects; }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }

    public FormattingInspection getFormattingInspection() { return formattingInspection; }
    public void setFormattingInspection(FormattingInspection formattingInspection) { this.formattingInspection = formattingInspection; }

    public Double getEstimatedYearsExperience() { return estimatedYearsExperience; }
    public void setEstimatedYearsExperience(Double estimatedYearsExperience) { this.estimatedYearsExperience = estimatedYearsExperience; }
}
