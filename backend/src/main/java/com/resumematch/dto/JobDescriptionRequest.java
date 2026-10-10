package com.resumematch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobDescriptionRequest {

    private String title = "Target Role";
    private String company;
    private String location;

    @NotBlank(message = "Job description cannot be blank")
    @Size(min = 50, message = "Job description is too short to extract requirements (minimum 50 characters)")
    private String text;

    public JobDescriptionRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
