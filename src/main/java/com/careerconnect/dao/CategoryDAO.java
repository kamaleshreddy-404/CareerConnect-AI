package com.careerconnect.dao;

import com.careerconnect.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    private static final List<Category> mockCategories = new ArrayList<>();

    static {
        mockCategories.add(new Category(1, "Software Engineering", "code", "Frontend, Backend, and Full Stack Engineering roles"));
        mockCategories.add(new Category(2, "Data Science", "database", "Data Analytics, Big Data, and Business Intelligence"));
        mockCategories.add(new Category(3, "AI / Machine Learning", "cpu", "Machine Learning Models, NLP, and Computer Vision"));
        mockCategories.add(new Category(4, "Cloud Computing", "cloud", "AWS, Azure, DevOps, and Infrastructure Systems"));
        mockCategories.add(new Category(5, "Cyber Security", "shield", "Information Security, Ethical Hacking, and Auditing"));
        mockCategories.add(new Category(6, "Web Development", "globe", "HTML, CSS, JavaScript, React, and Java Web Applications"));
        mockCategories.add(new Category(7, "Mobile Development", "smartphone", "Android, iOS, Flutter, and React Native"));
        mockCategories.add(new Category(8, "UI UX Design", "layout", "Product Design, Wireframing, and User Experience"));
        mockCategories.add(new Category(9, "Digital Marketing", "trending-up", "SEO, Content Strategy, and Growth Marketing"));
        mockCategories.add(new Category(10, "Finance", "dollar-sign", "Financial Planning, Investment, and Accounting"));
        mockCategories.add(new Category(11, "HR", "users", "Talent Acquisition, Placement, and Campus Relations"));
        mockCategories.add(new Category(12, "Sales", "shopping-bag", "B2B Sales, Business Development, and Account Management"));
    }

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM job_categories ORDER BY name ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapCategory(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        return new ArrayList<>(mockCategories);
    }

    public boolean addCategory(Category category) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO job_categories (name, icon, description) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, category.getName());
                pstmt.setString(2, category.getIcon() != null ? category.getIcon() : "code");
                pstmt.setString(3, category.getDescription());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) category.setId(rs.getInt(1));
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        category.setId(mockCategories.size() + 1);
        mockCategories.add(category);
        return true;
    }

    public boolean deleteCategory(int id) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "DELETE FROM job_categories WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        return mockCategories.removeIf(c -> c.getId() == id);
    }

    private Category mapCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setIcon(rs.getString("icon"));
        c.setDescription(rs.getString("description"));
        return c;
    }
}
