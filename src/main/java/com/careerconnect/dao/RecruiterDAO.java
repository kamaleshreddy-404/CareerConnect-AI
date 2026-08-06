package com.careerconnect.dao;

import com.careerconnect.model.Recruiter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecruiterDAO {

    private static final List<Recruiter> mockRecruiters = new ArrayList<>();

    static {
        Recruiter r1 = new Recruiter();
        r1.setId(1);
        r1.setUserId(2);
        r1.setCompanyId(1);
        r1.setPosition("Lead Technical Recruiter");
        r1.setStatus("APPROVED");
        r1.setUserName("TechCorp Recruiter");
        r1.setUserEmail("recruiter@techcorp.com");
        r1.setCompanyName("TechCorp Innovations");
        mockRecruiters.add(r1);

        Recruiter r2 = new Recruiter();
        r2.setId(2);
        r2.setUserId(3);
        r2.setCompanyId(2);
        r2.setPosition("Campus Placement Manager");
        r2.setStatus("APPROVED");
        r2.setUserName("GlobalSoft HR");
        r2.setUserEmail("hr@globalsoft.com");
        r2.setCompanyName("GlobalSoft Solutions");
        mockRecruiters.add(r2);
    }

    public boolean registerRecruiter(Recruiter recruiter) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO recruiters (user_id, company_id, position, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, recruiter.getUserId());
                pstmt.setInt(2, recruiter.getCompanyId());
                pstmt.setString(3, recruiter.getPosition());
                pstmt.setString(4, recruiter.getStatus() != null ? recruiter.getStatus() : "APPROVED");
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        recruiter.setId(rs.getInt(1));
                    }
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        recruiter.setId(mockRecruiters.size() + 1);
        mockRecruiters.add(recruiter);
        return true;
    }

    public Recruiter getRecruiterByUserId(int userId) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, c.name as company_name " +
                         "FROM recruiters r " +
                         "JOIN users u ON r.user_id = u.id " +
                         "JOIN companies c ON r.company_id = c.id " +
                         "WHERE r.user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return mapRecruiter(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Recruiter r : mockRecruiters) {
            if (r.getUserId() == userId) return r;
        }
        return mockRecruiters.isEmpty() ? null : mockRecruiters.get(0);
    }

    public List<Recruiter> getAllRecruiters() {
        List<Recruiter> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT r.*, u.name as user_name, u.email as user_email, c.name as company_name " +
                         "FROM recruiters r " +
                         "JOIN users u ON r.user_id = u.id " +
                         "JOIN companies c ON r.company_id = c.id " +
                         "ORDER BY r.created_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapRecruiter(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        return new ArrayList<>(mockRecruiters);
    }

    public boolean updateRecruiterStatus(int recruiterId, String status) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE recruiters SET status = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, status);
                pstmt.setInt(2, recruiterId);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Recruiter r : mockRecruiters) {
            if (r.getId() == recruiterId) {
                r.setStatus(status);
                return true;
            }
        }
        return false;
    }

    private Recruiter mapRecruiter(ResultSet rs) throws SQLException {
        Recruiter r = new Recruiter();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setCompanyId(rs.getInt("company_id"));
        r.setPosition(rs.getString("position"));
        r.setStatus(rs.getString("status"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        try {
            r.setUserName(rs.getString("user_name"));
            r.setUserEmail(rs.getString("user_email"));
            r.setCompanyName(rs.getString("company_name"));
        } catch (SQLException ignored) {}
        return r;
    }
}
