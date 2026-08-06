package com.careerconnect.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO {

    public Map<String, Integer> getPlatformStats() {
        Map<String, Integer> stats = new HashMap<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users WHERE role = 'JOB_SEEKER'");
                if (rs.next()) stats.put("totalUsers", rs.getInt(1));

                rs = stmt.executeQuery("SELECT COUNT(*) FROM recruiters");
                if (rs.next()) stats.put("totalRecruiters", rs.getInt(1));

                rs = stmt.executeQuery("SELECT COUNT(*) FROM jobs");
                if (rs.next()) stats.put("totalJobs", rs.getInt(1));

                rs = stmt.executeQuery("SELECT COUNT(*) FROM applications");
                if (rs.next()) stats.put("totalApplications", rs.getInt(1));

                rs = stmt.executeQuery("SELECT COUNT(*) FROM companies");
                if (rs.next()) stats.put("totalCompanies", rs.getInt(1));

                return stats;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        // Mock Statistics
        stats.put("totalUsers", 148);
        stats.put("totalRecruiters", 24);
        stats.put("totalJobs", 62);
        stats.put("totalApplications", 312);
        stats.put("totalCompanies", 18);
        return stats;
    }
}
