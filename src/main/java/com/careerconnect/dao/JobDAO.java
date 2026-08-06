package com.careerconnect.dao;

import com.careerconnect.model.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {

    private static final List<Job> mockJobs = new ArrayList<>();

    static {
        Job j1 = new Job();
        j1.setId(1);
        j1.setCompanyId(1);
        j1.setRecruiterId(1);
        j1.setCategoryId(1);
        j1.setTitle("Graduate Software Engineer (Java & React)");
        j1.setDescription("We are looking for passionate graduate engineers to join our flagship cloud products team. You will build high performance web applications using Java Servlets/Spring and React.");
        j1.setRequirements("B.Tech CS/IT graduate. Strong fundamentals in Java, Data Structures, OOPs, SQL, and Web Technologies.");
        j1.setLocation("Bangalore, India");
        j1.setJobType("FULL_TIME");
        j1.setSalaryRange("₹8,00,000 - ₹12,00,000 LPA");
        j1.setExperienceLevel("Entry Level (Freshers)");
        j1.setStatus("ACTIVE");
        j1.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        j1.setCompanyName("TechCorp Innovations");
        j1.setCompanyLogo("techcorp_logo.png");
        j1.setCategoryName("Software Engineering");
        j1.setApplicantCount(12);
        mockJobs.add(j1);

        Job j2 = new Job();
        j2.setId(2);
        j2.setCompanyId(1);
        j2.setRecruiterId(1);
        j2.setCategoryId(6);
        j2.setTitle("Frontend Web Developer Intern");
        j2.setDescription("Exciting 6-month internship opportunity for students skilled in React, HTML5, CSS3, and JavaScript. High conversion possibility to full-time.");
        j2.setRequirements("Hands-on experience with modern React, responsive design, REST APIs, and UI animations.");
        j2.setLocation("Hybrid - Bangalore");
        j2.setJobType("INTERNSHIP");
        j2.setSalaryRange("₹25,000 / month Stipend");
        j2.setExperienceLevel("Internship");
        j2.setStatus("ACTIVE");
        j2.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        j2.setCompanyName("TechCorp Innovations");
        j2.setCompanyLogo("techcorp_logo.png");
        j2.setCategoryName("Web Development");
        j2.setApplicantCount(8);
        mockJobs.add(j2);

        Job j3 = new Job();
        j3.setId(3);
        j3.setCompanyId(2);
        j3.setRecruiterId(2);
        j3.setCategoryId(2);
        j3.setTitle("Junior Data Analyst");
        j3.setDescription("Join GlobalSoft AI analytics group to extract business insights from complex big data streams using SQL and Python.");
        j3.setRequirements("Proficiency in MySQL, Python, Pandas, Tableau/PowerBI, and statistical modeling.");
        j3.setLocation("Hyderabad, India");
        j3.setJobType("FULL_TIME");
        j3.setSalaryRange("₹7,50,000 - ₹10,00,000 LPA");
        j3.setExperienceLevel("Entry Level");
        j3.setStatus("ACTIVE");
        j3.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        j3.setCompanyName("GlobalSoft Solutions");
        j3.setCompanyLogo("globalsoft_logo.png");
        j3.setCategoryName("Data Science");
        j3.setApplicantCount(15);
        mockJobs.add(j3);

        Job j4 = new Job();
        j4.setId(4);
        j4.setCompanyId(3);
        j4.setRecruiterId(1);
        j4.setCategoryId(3);
        j4.setTitle("AI Systems Developer Trainee");
        j4.setDescription("Build next-gen Generative AI and NLP workflows. Excellent opportunity for college graduates with strong algorithmic background.");
        j4.setRequirements("Strong mathematical background, Python, PyTorch/TensorFlow, RESTful services, and Git.");
        j4.setLocation("Pune, India");
        j4.setJobType("REMOTE");
        j4.setSalaryRange("₹10,00,000 - ₹15,00,000 LPA");
        j4.setExperienceLevel("Entry Level");
        j4.setStatus("ACTIVE");
        j4.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        j4.setCompanyName("Nexus AI Labs");
        j4.setCompanyLogo("nexus_logo.png");
        j4.setCategoryName("AI / Machine Learning");
        j4.setApplicantCount(24);
        mockJobs.add(j4);
    }

    public List<Job> getAllJobs() {
        return searchJobs(null, 0, null, null);
    }

    public List<Job> searchJobs(String keyword, int categoryId, String location, String jobType) {
        List<Job> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            StringBuilder sql = new StringBuilder(
                "SELECT j.*, c.name as company_name, c.logo as company_logo, cat.name as category_name, " +
                "(SELECT COUNT(*) FROM applications a WHERE a.job_id = j.id) as app_count " +
                "FROM jobs j " +
                "JOIN companies c ON j.company_id = c.id " +
                "JOIN job_categories cat ON j.category_id = cat.id " +
                "WHERE j.status = 'ACTIVE' "
            );

            List<Object> params = new ArrayList<>();
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql.append("AND (LOWER(j.title) LIKE ? OR LOWER(j.description) LIKE ? OR LOWER(c.name) LIKE ?) ");
                String kw = "%" + keyword.trim().toLowerCase() + "%";
                params.add(kw); params.add(kw); params.add(kw);
            }
            if (categoryId > 0) {
                sql.append("AND j.category_id = ? ");
                params.add(categoryId);
            }
            if (location != null && !location.trim().isEmpty()) {
                sql.append("AND LOWER(j.location) LIKE ? ");
                params.add("%" + location.trim().toLowerCase() + "%");
            }
            if (jobType != null && !jobType.trim().isEmpty() && !"ALL".equalsIgnoreCase(jobType)) {
                sql.append("AND j.job_type = ? ");
                params.add(jobType.trim().toUpperCase());
            }

            sql.append("ORDER BY j.created_at DESC");

            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(mapJob(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        // Mock Search Filtering
        for (Job j : mockJobs) {
            if (!"ACTIVE".equalsIgnoreCase(j.getStatus())) continue;
            boolean matches = true;

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = keyword.toLowerCase();
                boolean titleMatch = j.getTitle().toLowerCase().contains(kw);
                boolean descMatch = j.getDescription().toLowerCase().contains(kw);
                boolean compMatch = j.getCompanyName() != null && j.getCompanyName().toLowerCase().contains(kw);
                if (!titleMatch && !descMatch && !compMatch) matches = false;
            }

            if (categoryId > 0 && j.getCategoryId() != categoryId) {
                matches = false;
            }

            if (location != null && !location.trim().isEmpty()) {
                if (!j.getLocation().toLowerCase().contains(location.toLowerCase())) {
                    matches = false;
                }
            }

            if (jobType != null && !jobType.trim().isEmpty() && !"ALL".equalsIgnoreCase(jobType)) {
                if (!j.getJobType().equalsIgnoreCase(jobType)) {
                    matches = false;
                }
            }

            if (matches) {
                list.add(j);
            }
        }
        return list;
    }

    public Job getJobById(int id) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT j.*, c.name as company_name, c.logo as company_logo, cat.name as category_name, " +
                         "(SELECT COUNT(*) FROM applications a WHERE a.job_id = j.id) as app_count " +
                         "FROM jobs j " +
                         "JOIN companies c ON j.company_id = c.id " +
                         "JOIN job_categories cat ON j.category_id = cat.id " +
                         "WHERE j.id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return mapJob(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Job j : mockJobs) {
            if (j.getId() == id) return j;
        }
        return null;
    }

    public List<Job> getJobsByRecruiter(int recruiterId) {
        List<Job> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT j.*, c.name as company_name, c.logo as company_logo, cat.name as category_name, " +
                         "(SELECT COUNT(*) FROM applications a WHERE a.job_id = j.id) as app_count " +
                         "FROM jobs j " +
                         "JOIN companies c ON j.company_id = c.id " +
                         "JOIN job_categories cat ON j.category_id = cat.id " +
                         "WHERE j.recruiter_id = ? " +
                         "ORDER BY j.created_at DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, recruiterId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(mapJob(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Job j : mockJobs) {
            if (j.getRecruiterId() == recruiterId) list.add(j);
        }
        return list;
    }

    public boolean createJob(Job job) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO jobs (company_id, recruiter_id, category_id, title, description, requirements, location, job_type, salary_range, experience_level, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, job.getCompanyId());
                pstmt.setInt(2, job.getRecruiterId());
                pstmt.setInt(3, job.getCategoryId());
                pstmt.setString(4, job.getTitle());
                pstmt.setString(5, job.getDescription());
                pstmt.setString(6, job.getRequirements());
                pstmt.setString(7, job.getLocation());
                pstmt.setString(8, job.getJobType());
                pstmt.setString(9, job.getSalaryRange());
                pstmt.setString(10, job.getExperienceLevel());
                pstmt.setString(11, job.getStatus() != null ? job.getStatus() : "ACTIVE");
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) job.setId(rs.getInt(1));
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        job.setId(mockJobs.size() + 1);
        job.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        job.setCompanyName("TechCorp Innovations");
        job.setCategoryName("Software Engineering");
        mockJobs.add(job);
        return true;
    }

    public boolean updateJob(Job job) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE jobs SET title = ?, category_id = ?, description = ?, requirements = ?, location = ?, job_type = ?, salary_range = ?, experience_level = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, job.getTitle());
                pstmt.setInt(2, job.getCategoryId());
                pstmt.setString(3, job.getDescription());
                pstmt.setString(4, job.getRequirements());
                pstmt.setString(5, job.getLocation());
                pstmt.setString(6, job.getJobType());
                pstmt.setString(7, job.getSalaryRange());
                pstmt.setString(8, job.getExperienceLevel());
                pstmt.setInt(9, job.getId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (int i = 0; i < mockJobs.size(); i++) {
            if (mockJobs.get(i).getId() == job.getId()) {
                mockJobs.set(i, job);
                return true;
            }
        }
        return false;
    }

    public boolean deleteJob(int id) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "DELETE FROM jobs WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        return mockJobs.removeIf(j -> j.getId() == id);
    }

    public boolean updateJobStatus(int id, String status) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE jobs SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, id);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Job j : mockJobs) {
            if (j.getId() == id) {
                j.setStatus(status);
                return true;
            }
        }
        return false;
    }

    public List<Job> getRecentJobs(int limit) {
        List<Job> all = getAllJobs();
        return all.subList(0, Math.min(limit, all.size()));
    }

    private Job mapJob(ResultSet rs) throws SQLException {
        Job j = new Job();
        j.setId(rs.getInt("id"));
        j.setCompanyId(rs.getInt("company_id"));
        j.setRecruiterId(rs.getInt("recruiter_id"));
        j.setCategoryId(rs.getInt("category_id"));
        j.setTitle(rs.getString("title"));
        j.setDescription(rs.getString("description"));
        j.setRequirements(rs.getString("requirements"));
        j.setLocation(rs.getString("location"));
        j.setJobType(rs.getString("job_type"));
        j.setSalaryRange(rs.getString("salary_range"));
        j.setExperienceLevel(rs.getString("experience_level"));
        j.setStatus(rs.getString("status"));
        j.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            j.setCompanyName(rs.getString("company_name"));
            j.setCompanyLogo(rs.getString("company_logo"));
            j.setCategoryName(rs.getString("category_name"));
            j.setApplicantCount(rs.getInt("app_count"));
        } catch (SQLException ignored) {}
        return j;
    }
}
