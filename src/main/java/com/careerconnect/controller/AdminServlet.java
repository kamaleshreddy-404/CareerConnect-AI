package com.careerconnect.controller;

import com.careerconnect.dao.AdminDAO;
import com.careerconnect.dao.CategoryDAO;
import com.careerconnect.dao.JobDAO;
import com.careerconnect.dao.RecruiterDAO;
import com.careerconnect.model.Category;
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
import java.util.Map;

@WebServlet("/admin/*")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private AdminDAO adminDAO = new AdminDAO();
    private RecruiterDAO recruiterDAO = new RecruiterDAO();
    private JobDAO jobDAO = new JobDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if ("/stats".equalsIgnoreCase(path)) {
            sendStatsJson(response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) path = "/approve-recruiter";

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        switch (path) {
            case "/approve-recruiter":
                int rId = Integer.parseInt(request.getParameter("recruiterId"));
                String status = request.getParameter("status"); // APPROVED / REJECTED
                recruiterDAO.updateRecruiterStatus(rId, status);
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?msg=recruiter_updated");
                break;
            case "/flag-job":
                int jobId = Integer.parseInt(request.getParameter("jobId"));
                String jobStatus = request.getParameter("status"); // FLAGGED / ACTIVE
                jobDAO.updateJobStatus(jobId, jobStatus);
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?msg=job_flagged");
                break;
            case "/add-category":
                String name = request.getParameter("name");
                String icon = request.getParameter("icon");
                String description = request.getParameter("description");
                Category cat = new Category(0, name, icon, description);
                categoryDAO.addCategory(cat);
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?msg=category_added");
                break;
            case "/delete-category":
                int catId = Integer.parseInt(request.getParameter("categoryId"));
                categoryDAO.deleteCategory(catId);
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp?msg=category_deleted");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp");
                break;
        }
    }

    private void sendStatsJson(HttpServletResponse response) throws IOException {
        Map<String, Integer> stats = adminDAO.getPlatformStats();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(stats));
        out.flush();
    }
}
