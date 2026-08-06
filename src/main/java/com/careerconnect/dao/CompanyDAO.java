package com.careerconnect.dao;

import com.careerconnect.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    private static final List<Company> mockCompanies = new ArrayList<>();

    static {
        mockCompanies.add(new Company(1, "TechCorp Innovations", "https://techcorp.com", "techcorp_logo.png", "Leading Cloud & Enterprise Software Solutions Company", "Bangalore, India", "IT & Software", new Timestamp(System.currentTimeMillis())));
        mockCompanies.add(new Company(2, "GlobalSoft Solutions", "https://globalsoft.com", "globalsoft_logo.png", "Global Product Engineering & Digital Transformation Leader", "Hyderabad, India", "Information Technology", new Timestamp(System.currentTimeMillis())));
        mockCompanies.add(new Company(3, "Nexus AI Labs", "https://nexusai.com", "nexus_logo.png", "Cutting edge Artificial Intelligence & Machine Learning Research", "Pune, India", "Artificial Intelligence", new Timestamp(System.currentTimeMillis())));
    }

    public List<Company> getAllCompanies() {
        List<Company> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM companies ORDER BY name ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapCompany(rs));
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        return new ArrayList<>(mockCompanies);
    }

    public Company getCompanyById(int id) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM companies WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return mapCompany(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }
        for (Company c : mockCompanies) {
            if (c.getId() == id) return c;
        }
        return mockCompanies.isEmpty() ? null : mockCompanies.get(0);
    }

    public int addCompany(Company company) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "INSERT INTO companies (name, website, logo, description, location, industry) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, company.getName());
                pstmt.setString(2, company.getWebsite());
                pstmt.setString(3, company.getLogo() != null ? company.getLogo() : "default_company.png");
                pstmt.setString(4, company.getDescription());
                pstmt.setString(5, company.getLocation());
                pstmt.setString(6, company.getIndustry());
                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    ResultSet rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        company.setId(mockCompanies.size() + 1);
        mockCompanies.add(company);
        return company.getId();
    }

    public boolean updateCompany(Company company) {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "UPDATE companies SET name = ?, website = ?, description = ?, location = ?, industry = ? WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, company.getName());
                pstmt.setString(2, company.getWebsite());
                pstmt.setString(3, company.getDescription());
                pstmt.setString(4, company.getLocation());
                pstmt.setString(5, company.getIndustry());
                pstmt.setInt(6, company.getId());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                DBConnection.closeConnection(conn);
            }
        }

        for (int i = 0; i < mockCompanies.size(); i++) {
            if (mockCompanies.get(i).getId() == company.getId()) {
                mockCompanies.set(i, company);
                return true;
            }
        }
        return false;
    }

    private Company mapCompany(ResultSet rs) throws SQLException {
        Company c = new Company();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setWebsite(rs.getString("website"));
        c.setLogo(rs.getString("logo"));
        c.setDescription(rs.getString("description"));
        c.setLocation(rs.getString("location"));
        c.setIndustry(rs.getString("industry"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }
}
