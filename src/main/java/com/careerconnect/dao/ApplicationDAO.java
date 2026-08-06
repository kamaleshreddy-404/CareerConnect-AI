package com.careerconnect.dao;

import com.careerconnect.model.Application;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    private static final List<Application> mockApps = new ArrayList<>();

    static {
        Application a1 = new Application();
        a1.setId(1);
        a1.setJobId(1);
        a1.setUserId(4);
        a1.setResumePath("alex_johnson_resume.pdf");
        a1.setCoverLetter("I am excited to apply for the Graduate Software Engineer position. I have built full stack Java MVC applications and React components.");
        a1.setStatus("UNDER_REVIEW");
        a1.setAiScore(88);
        a1.setAppliedAt(new Timestamp(System.currentTimeMillis()));
        a1.setJobTitle("Graduate Software Engineer (Java & React)");
        a1.setCompanyName("TechCorp Innovations");
        a1.setApplicantName("Alex Johnson");
        a1.setApplicantEmail("alex.student@college.edu");
        a1.setApplicantPhone("9876543213");
        mockApps.add(a1);

        Application a2 = new Application();
        a2.setId(2);
        a2.setJobId(3);
        a2.setUserId(5);
        a2.setResumePath("sarah_miller_resume.pdf");
        a2.setCoverLetter("I hold a 9.2 CGPA in AI & Data Science with hands-on projects in Python and MySQL analytics.");
        a2.setStatus("ACCEPTED");
        a2.setAiScore(94);
        a2.setAppliedAt(new Timestamp(System.currentTimeMillis()));
        a2.setJobTitle("Junior Data Analyst");
        a2.setCompanyName("GlobalSoft Solutions");
        a2.setApplicantName("Sarah Miller");
        a2.setApplicantEmail("sarah.m@college.edu");
        a2.setApplicantPhone("9876543214");
        mockApps.add(a2);
    }

    public boolean applyForJob(Application app) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO applications (job_id, user_id, resume_path, cover_letter, status, ai_score) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, app.getJobId());
                pstmt.setInt(2, app.getUserId());
                pstmt.setString(3, app.getResumePath());
                pstmt.setString(4, app.getCoverLetter());
                pstmt.setString(5, app.getStatus() != null ? app.getStatus() : "APPLIED");
                pstmt.setInt(6, app.getAiScore() > 0 ? app.getAiScore() : 80);
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) app.setId(rs.getInt(1));
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        app.setId(mockApps.size() + 1);
        app.setAppliedAt(new Timestamp(System.currentTimeMillis()));
        mockApps.add(app);
        return true;
    }

    public boolean hasUserApplied(int userId, int jobId) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT id FROM applications WHERE user_id = ? AND job_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setInt(2, jobId);
                ResultSet rs = pstmt.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Application a : mockApps) {
            if (a.getUserId() == userId && a.getJobId() == jobId) return true;
        }
        return false;
    }

    public List<Application> getApplicationsByUser(int userId) {
        List<Application> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT a.*, j.title as job_title, c.name as company_name, u.name as applicant_name, u.email as applicant_email, u.phone as applicant_phone " +
                         "FROM applications a " +
                         "JOIN jobs j ON a.job_id = j.id " +
                         "JOIN companies c ON j.company_id = c.id " +
                         "JOIN users u ON a.user_id = u.id " +
                         "WHERE a.user_id = ? " +
                         "ORDER BY a.applied_at DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(mapApplication(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Application a : mockApps) {
            if (a.getUserId() == userId) list.add(a);
        }
        return list;
    }

    public List<Application> getApplicationsByRecruiter(int recruiterId) {
        List<Application> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT a.*, j.title as job_title, c.name as company_name, u.name as applicant_name, u.email as applicant_email, u.phone as applicant_phone " +
                         "FROM applications a " +
                         "JOIN jobs j ON a.job_id = j.id " +
                         "JOIN companies c ON j.company_id = c.id " +
                         "JOIN users u ON a.user_id = u.id " +
                         "WHERE j.recruiter_id = ? " +
                         "ORDER BY a.applied_at DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, recruiterId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(mapApplication(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        return new ArrayList<>(mockApps);
    }

    public boolean updateApplicationStatus(int applicationId, String status) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE applications SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, applicationId);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Application a : mockApps) {
            if (a.getId() == applicationId) {
                a.setStatus(status);
                return true;
            }
        }
        return false;
    }

    private Application mapApplication(ResultSet rs) throws SQLException {
        Application a = new Application();
        a.setId(rs.getInt("id"));
        a.setJobId(rs.getInt("job_id"));
        a.setUserId(rs.getInt("user_id"));
        a.setResumePath(rs.getString("resume_path"));
        a.setCoverLetter(rs.getString("cover_letter"));
        a.setStatus(rs.getString("status"));
        a.setAiScore(rs.getInt("ai_score"));
        a.setAppliedAt(rs.getTimestamp("applied_at"));
        try {
            a.setJobTitle(rs.getString("job_title"));
            a.setCompanyName(rs.getString("company_name"));
            a.setApplicantName(rs.getString("applicant_name"));
            a.setApplicantEmail(rs.getString("applicant_email"));
            a.setApplicantPhone(rs.getString("applicant_phone"));
        } catch (SQLException ignored) {}
        return a;
    }
}
