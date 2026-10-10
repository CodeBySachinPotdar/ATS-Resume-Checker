package com.resumematch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResumeTextRequest {

    private String title = "My Resume";

    @NotBlank(message = "Resume text cannot be blank")
    @Size(min = 50, message = "Resume content is too short for an ATS evaluation (minimum 50 characters)")
    private String text;

    public ResumeTextRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
