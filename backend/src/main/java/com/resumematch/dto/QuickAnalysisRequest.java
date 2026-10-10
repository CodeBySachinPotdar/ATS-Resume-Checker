package com.resumematch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class QuickAnalysisRequest {

    private String resumeTitle = "My Resume";

    @NotBlank(message = "Resume text is required")
    @Size(min = 40, message = "Resume text is too short")
    private String resumeText;

    private String jobTitle = "Target Role";
    private String company;

    @NotBlank(message = "Job description is required")
    @Size(min = 40, message = "Job description is too short")
    private String jobDescriptionText;

    public QuickAnalysisRequest() {}

    public String getResumeTitle() { return resumeTitle; }
    public void setResumeTitle(String resumeTitle) { this.resumeTitle = resumeTitle; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobDescriptionText() { return jobDescriptionText; }
    public void setJobDescriptionText(String jobDescriptionText) { this.jobDescriptionText = jobDescriptionText; }
}
