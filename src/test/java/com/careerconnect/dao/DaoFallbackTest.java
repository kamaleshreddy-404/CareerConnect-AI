package com.careerconnect.dao;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.careerconnect.model.Application;
import com.careerconnect.model.Category;
import com.careerconnect.model.Company;
import com.careerconnect.model.Job;
import com.careerconnect.model.Recruiter;

class DaoFallbackTest {
    @Test
    void searchesJobsByKeywordAndFilters() {
        JobDAO jobDAO = new JobDAO();

        List<Job> javaJobs = jobDAO.searchJobs("Java", 0, null, "ALL");
        List<Job> remoteJobs = jobDAO.searchJobs(null, 0, null, "REMOTE");

        assertFalse(javaJobs.isEmpty());
        assertTrue(javaJobs.stream().allMatch(job ->
                job.getTitle().toLowerCase().contains("java")
                        || job.getDescription().toLowerCase().contains("java")
                        || job.getCompanyName().toLowerCase().contains("java")));
        assertEquals(1, remoteJobs.size());
        assertEquals("REMOTE", remoteJobs.get(0).getJobType());
    }

    @Test
    void returnsJobByIdAndNullForUnknownId() {
        JobDAO jobDAO = new JobDAO();

        assertEquals("Graduate Software Engineer (Java & React)", jobDAO.getJobById(1).getTitle());
        assertEquals(null, jobDAO.getJobById(Integer.MAX_VALUE));
    }

    @Test
    void addsAndDeletesCategory() {
        CategoryDAO categoryDAO = new CategoryDAO();
        Category category = new Category(0, "Test Category", "test", "Test description");

        assertTrue(categoryDAO.addCategory(category));
        assertTrue(category.getId() > 0);
        assertTrue(categoryDAO.deleteCategory(category.getId()));
        assertFalse(categoryDAO.deleteCategory(category.getId()));
    }

    @Test
    void returnsCompanyAndAddsCompany() {
        CompanyDAO companyDAO = new CompanyDAO();
        Company company = new Company();
        company.setName("Test Company");

        assertEquals("TechCorp Innovations", companyDAO.getCompanyById(1).getName());
        int id = companyDAO.addCompany(company);
        assertEquals(id, company.getId());
        assertNotNull(companyDAO.getCompanyById(id));
    }

    @Test
    void returnsRecruiterByUserAndUpdatesStatus() {
        RecruiterDAO recruiterDAO = new RecruiterDAO();
        Recruiter recruiter = recruiterDAO.getRecruiterByUserId(2);

        assertNotNull(recruiter);
        assertEquals("TechCorp Recruiter", recruiter.getUserName());
        assertTrue(recruiterDAO.updateRecruiterStatus(recruiter.getId(), "REJECTED"));
        assertEquals("REJECTED", recruiterDAO.getRecruiterByUserId(2).getStatus());
    }

    @Test
    void findsAndUpdatesApplications() {
        ApplicationDAO applicationDAO = new ApplicationDAO();

        assertTrue(applicationDAO.hasUserApplied(4, 1));
        assertFalse(applicationDAO.hasUserApplied(Integer.MAX_VALUE, Integer.MAX_VALUE));
        List<Application> applications = applicationDAO.getApplicationsByUser(4);
        assertEquals(1, applications.size());
        assertTrue(applicationDAO.updateApplicationStatus(1, "ACCEPTED"));
        assertEquals("ACCEPTED", applicationDAO.getApplicationsByUser(4).get(0).getStatus());
    }

    @Test
    void returnsFallbackPlatformStatistics() {
        Map<String, Integer> stats = new AdminDAO().getPlatformStats();

        assertEquals(5, stats.size());
        assertTrue(stats.get("totalUsers") > 0);
        assertTrue(stats.get("totalApplications") > 0);
    }
}
