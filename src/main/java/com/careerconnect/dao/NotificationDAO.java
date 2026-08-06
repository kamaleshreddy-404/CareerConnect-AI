package com.careerconnect.dao;

import com.careerconnect.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    private static final List<Notification> mockNotifications = new ArrayList<>();

    static {
        mockNotifications.add(new Notification(1, 4, "Application Update", "Your application for Graduate Software Engineer at TechCorp is now UNDER REVIEW.", false, new Timestamp(System.currentTimeMillis())));
        mockNotifications.add(new Notification(2, 5, "Shortlisted!", "Congratulations! GlobalSoft Solutions has ACCEPTED your application for Junior Data Analyst.", false, new Timestamp(System.currentTimeMillis())));
    }

    public boolean createNotification(int userId, String title, String message) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO notifications (user_id, title, message) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, title);
                pstmt.setString(3, message);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        Notification n = new Notification(mockNotifications.size() + 1, userId, title, message, false, new Timestamp(System.currentTimeMillis()));
        mockNotifications.add(n);
        return true;
    }

    public List<Notification> getNotificationsByUser(int userId) {
        List<Notification> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    Notification n = new Notification(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getBoolean("is_read"),
                        rs.getTimestamp("created_at")
                    );
                    list.add(n);
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Notification n : mockNotifications) {
            if (n.getUserId() == userId) list.add(n);
        }
        return list;
    }

    public boolean markAsRead(int notificationId) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, notificationId);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (Notification n : mockNotifications) {
            if (n.getId() == notificationId) {
                n.setRead(true);
                return true;
            }
        }
        return false;
    }
}
