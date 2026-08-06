package com.careerconnect.controller;

import com.careerconnect.dao.CategoryDAO;
import com.careerconnect.dao.CompanyDAO;
import com.careerconnect.dao.JobDAO;
import com.careerconnect.model.Category;
import com.careerconnect.model.Job;
import com.careerconnect.model.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/jobs/*")
public class JobServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private JobDAO jobDAO = new JobDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private CompanyDAO companyDAO = new CompanyDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) path = "/browse";

        switch (path) {
            case "/api":
                sendJobsJson(request, response);
                break;
            case "/detail":
                showJobDetail(request, response);
                break;
            case "/browse":
            default:
                browseJobs(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) path = "/create";

        switch (path) {
            case "/create":
                handleCreateJob(request, response);
                break;
            case "/edit":
                handleEditJob(request, response);
                break;
            case "/delete":
                handleDeleteJob(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/jobs/browse");
                break;
        }
    }

    private void browseJobs(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String categoryStr = request.getParameter("category");
        String location = request.getParameter("location");
        String jobType = request.getParameter("type");

        int categoryId = 0;
        if (categoryStr != null && !categoryStr.isEmpty()) {
            try { categoryId = Integer.parseInt(categoryStr); } catch (NumberFormatException ignored) {}
        }

        List<Job> jobsList = jobDAO.searchJobs(keyword, categoryId, location, jobType);
        List<Category> categoriesList = categoryDAO.getAllCategories();

        request.setAttribute("jobs", jobsList);
        request.setAttribute("categories", categoriesList);
        request.setAttribute("paramKeyword", keyword);
        request.setAttribute("paramCategory", categoryId);
        request.setAttribute("paramLocation", location);
        request.setAttribute("paramType", jobType);

        request.getRequestDispatcher("/jobs.jsp").forward(request, response);
    }

    private void sendJobsJson(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String keyword = request.getParameter("keyword");
        String categoryStr = request.getParameter("category");
        String location = request.getParameter("location");
        String jobType = request.getParameter("type");

        int categoryId = 0;
        if (categoryStr != null && !categoryStr.isEmpty()) {
            try { categoryId = Integer.parseInt(categoryStr); } catch (NumberFormatException ignored) {}
        }

        List<Job> jobs = jobDAO.searchJobs(keyword, categoryId, location, jobType);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(jobs));
        out.flush();
    }

    private void showJobDetail(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr == null) {
            response.sendRedirect(request.getContextPath() + "/jobs/browse");
            return;
        }

        int jobId = Integer.parseInt(idStr);
        Job job = jobDAO.getJobById(jobId);
        if (job == null) {
            response.sendRedirect(request.getContextPath() + "/jobs/browse");
            return;
        }

        request.setAttribute("job", job);
        request.getRequestDispatcher("/job-detail.jsp").forward(request, response);
    }

    private void handleCreateJob(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || !"RECRUITER".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String title = request.getParameter("title");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String description = request.getParameter("description");
        String requirements = request.getParameter("requirements");
        String location = request.getParameter("location");
        String jobType = request.getParameter("jobType");
        String salaryRange = request.getParameter("salaryRange");
        String experienceLevel = request.getParameter("experienceLevel");

        Integer companyId = (Integer) session.getAttribute("companyId");
        Integer recruiterId = (Integer) session.getAttribute("recruiterId");
        if (companyId == null) companyId = 1;
        if (recruiterId == null) recruiterId = 1;

        Job job = new Job();
        job.setCompanyId(companyId);
        job.setRecruiterId(recruiterId);
        job.setCategoryId(categoryId);
        job.setTitle(title);
        job.setDescription(description);
        job.setRequirements(requirements);
        job.setLocation(location);
        job.setJobType(jobType);
        job.setSalaryRange(salaryRange);
        job.setExperienceLevel(experienceLevel);
        job.setStatus("ACTIVE");

        if (jobDAO.createJob(job)) {
            response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp?msg=job_posted");
        } else {
            request.setAttribute("error", "Failed to create job posting.");
            request.getRequestDispatcher("/recruiter-dashboard.jsp").forward(request, response);
        }
    }

    private void handleEditJob(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        int jobId = Integer.parseInt(request.getParameter("jobId"));
        String title = request.getParameter("title");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String description = request.getParameter("description");
        String requirements = request.getParameter("requirements");
        String location = request.getParameter("location");
        String jobType = request.getParameter("jobType");
        String salaryRange = request.getParameter("salaryRange");
        String experienceLevel = request.getParameter("experienceLevel");

        Job job = jobDAO.getJobById(jobId);
        if (job != null) {
            job.setTitle(title);
            job.setCategoryId(categoryId);
            job.setDescription(description);
            job.setRequirements(requirements);
            job.setLocation(location);
            job.setJobType(jobType);
            job.setSalaryRange(salaryRange);
            job.setExperienceLevel(experienceLevel);
            jobDAO.updateJob(job);
        }

        response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp?msg=job_updated");
    }

    private void handleDeleteJob(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int jobId = Integer.parseInt(request.getParameter("jobId"));
        jobDAO.deleteJob(jobId);
        response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp?msg=job_deleted");
    }
}
