package com.resumematch.parser.model;

public class EducationItem {
    private String institution;
    private String degree;
    private String fieldOfStudy;
    private String graduationYear;
    private String gpa;

    public EducationItem() {}

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public String getGraduationYear() { return graduationYear; }
    public void setGraduationYear(String graduationYear) { this.graduationYear = graduationYear; }

    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
}
