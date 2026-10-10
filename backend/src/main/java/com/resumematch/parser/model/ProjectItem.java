package com.resumematch.parser.model;

import java.util.ArrayList;
import java.util.List;

public class ProjectItem {
    private String name;
    private String description;
    private String url;
    private List<String> technologies = new ArrayList<>();
    private List<String> bulletPoints = new ArrayList<>();

    public ProjectItem() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public List<String> getTechnologies() { return technologies; }
    public void setTechnologies(List<String> technologies) { this.technologies = technologies; }

    public List<String> getBulletPoints() { return bulletPoints; }
    public void setBulletPoints(List<String> bulletPoints) { this.bulletPoints = bulletPoints; }
}
