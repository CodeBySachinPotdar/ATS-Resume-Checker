package com.resumematch.parser.model;

import java.util.ArrayList;
import java.util.List;

public class WorkExperience {
    private String company;
    private String jobTitle;
    private String location;
    private String startDate;
    private String endDate;
    private boolean current;
    private List<String> bulletPoints = new ArrayList<>();
    private List<String> technologiesUsed = new ArrayList<>();

    public WorkExperience() {}

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public boolean isCurrent() { return current; }
    public void setCurrent(boolean current) { this.current = current; }

    public List<String> getBulletPoints() { return bulletPoints; }
    public void setBulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints; }

    public List<String> getTechnologiesUsed() { return technologiesUsed; }
    public void setTechnologiesUsed(List<String> technologiesUsed) { this.technologiesUsed = technologiesUsed; }
}
