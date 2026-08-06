package com.careerconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Recruiter implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int companyId;
    private String position;
    private String status; // PENDING, APPROVED, REJECTED
    private Timestamp createdAt;

    // Joined fields for convenience
    private String userName;
    private String userEmail;
    private String companyName;

    public Recruiter() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}
