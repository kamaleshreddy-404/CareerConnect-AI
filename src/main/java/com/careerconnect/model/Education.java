package com.careerconnect.model;

import java.io.Serializable;

public class Education implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String degree;
    private String institution;
    private String fieldOfStudy;
    private int startYear;
    private int endYear;
    private String grade;

    public Education() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getInstitution() { return institution; }
    public void setInstitution(String institution) { this.institution = institution; }

    public String getFieldOfStudy() { return fieldOfStudy; }
    public void setFieldOfStudy(String fieldOfStudy) { this.fieldOfStudy = fieldOfStudy; }

    public int getStartYear() { return startYear; }
    public void setStartYear(int startYear) { this.startYear = startYear; }

    public int getEndYear() { return endYear; }
    public void setEndYear(int endYear) { this.endYear = endYear; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}
