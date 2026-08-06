package com.careerconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String website;
    private String logo;
    private String description;
    private String location;
    private String industry;
    private Timestamp createdAt;

    public Company() {}

    public Company(int id, String name, String website, String logo, String description, String location, String industry, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.website = website;
        this.logo = logo;
        this.description = description;
        this.location = location;
        this.industry = industry;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
