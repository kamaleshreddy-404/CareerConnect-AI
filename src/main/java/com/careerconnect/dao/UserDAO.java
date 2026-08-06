package com.careerconnect.dao;

import com.careerconnect.model.User;
import com.careerconnect.model.Education;
import com.careerconnect.model.Experience;
import com.careerconnect.model.Skill;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Fallback memory storage when DB is offline
    private static final List<User> mockUsers = new ArrayList<>();

    static {
        mockUsers.add(new User(1, "System Administrator", "admin@careerconnect.ai", "password123", "9876543210", "ADMIN", "CareerConnect AI System Administrator", "default_avatar.png", new Timestamp(System.currentTimeMillis())));
        mockUsers.add(new User(2, "TechCorp Recruiter", "recruiter@techcorp.com", "password123", "9876543211", "RECRUITER", "Senior Technical Recruiter at TechCorp Innovations", "default_avatar.png", new Timestamp(System.currentTimeMillis())));
        mockUsers.add(new User(3, "GlobalSoft HR", "hr@globalsoft.com", "password123", "9876543212", "RECRUITER", "Campus Placement Manager at GlobalSoft", "default_avatar.png", new Timestamp(System.currentTimeMillis())));
        mockUsers.add(new User(4, "Alex Johnson", "alex.student@college.edu", "password123", "9876543213", "JOB_SEEKER", "Final year Computer Science student specializing in Java & React Web Dev", "default_avatar.png", new Timestamp(System.currentTimeMillis())));
        mockUsers.add(new User(5, "Sarah Miller", "sarah.m@college.edu", "password123", "9876543214", "JOB_SEEKER", "Data Science & AI enthusiast looking for Entry-Level roles", "default_avatar.png", new Timestamp(System.currentTimeMillis())));
    }

    public User authenticateUser(String email, String password) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return mapUser(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        // Mock Fallback
        for (User u : mockUsers) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean registerUser(User user) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO users (name, email, password, phone, role, bio) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getEmail());
                pstmt.setString(3, user.getPassword());
                pstmt.setString(4, user.getPhone());
                pstmt.setString(5, user.getRole());
                pstmt.setString(6, user.getBio());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        user.setId(rs.getInt(1));
                    }
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        // Mock Register
        user.setId(mockUsers.size() + 1);
        mockUsers.add(user);
        return true;
    }

    public User getUserById(int id) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM users WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return mapUser(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (User u : mockUsers) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public boolean updateUser(User user) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE users SET name = ?, phone = ?, bio = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getName());
                pstmt.setString(2, user.getPhone());
                pstmt.setString(3, user.getBio());
                pstmt.setInt(4, user.getId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (int i = 0; i < mockUsers.size(); i++) {
            if (mockUsers.get(i).getId() == user.getId()) {
                mockUsers.set(i, user);
                return true;
            }
        }
        return false;
    }

    public boolean resetPassword(String email, String newPassword) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE users SET password = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, newPassword);
                pstmt.setString(2, email);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (User u : mockUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                u.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM users ORDER BY created_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapUser(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        return new ArrayList<>(mockUsers);
    }

    public List<Education> getUserEducation(int userId) {
        List<Education> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM education WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Education edu = new Education();
                    edu.setId(rs.getInt("id"));
                    edu.setUserId(rs.getInt("user_id"));
                    edu.setDegree(rs.getString("degree"));
                    edu.setInstitution(rs.getString("institution"));
                    edu.setFieldOfStudy(rs.getString("field_of_study"));
                    edu.setStartYear(rs.getInt("start_year"));
                    edu.setEndYear(rs.getInt("end_year"));
                    edu.setGrade(rs.getString("grade"));
                    list.add(edu);
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        // Mock Education
        Education edu = new Education();
        edu.setId(1);
        edu.setUserId(userId);
        edu.setDegree("B.Tech in Computer Science");
        edu.setInstitution("State Institute of Technology");
        edu.setFieldOfStudy("Computer Science & Engineering");
        edu.setStartYear(2021);
        edu.setEndYear(2025);
        edu.setGrade("8.9 CGPA");
        list.add(edu);
        return list;
    }

    public List<Experience> getUserExperience(int userId) {
        List<Experience> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM experience WHERE user_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Experience exp = new Experience();
                    exp.setId(rs.getInt("id"));
                    exp.setUserId(rs.getInt("user_id"));
                    exp.setTitle(rs.getString("title"));
                    exp.setCompanyName(rs.getString("company_name"));
                    exp.setLocation(rs.getString("location"));
                    exp.setStartDate(rs.getString("start_date"));
                    exp.setEndDate(rs.getString("end_date"));
                    exp.setDescription(rs.getString("description"));
                    list.add(exp);
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        Experience exp = new Experience();
        exp.setId(1);
        exp.setUserId(userId);
        exp.setTitle("Software Engineer Intern");
        exp.setCompanyName("DevSolutions Inc");
        exp.setLocation("Remote");
        exp.setStartDate("2024-05");
        exp.setEndDate("2024-08");
        exp.setDescription("Developed REST APIs using Java Servlets, React, and MySQL.");
        list.add(exp);
        return list;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setBio(rs.getString("bio"));
        u.setProfilePic(rs.getString("profile_pic"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }
}
