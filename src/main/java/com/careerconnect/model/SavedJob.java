package com.careerconnect.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class SavedJob implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private int jobId;
    private Timestamp savedAt;

    // Joined properties
    private Job job;

    public SavedJob() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }

    public Timestamp getSavedAt() { return savedAt; }
    public void setSavedAt(Timestamp savedAt) { this.savedAt = savedAt; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
}
