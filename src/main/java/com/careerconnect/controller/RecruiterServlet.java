package com.careerconnect.controller;

import com.careerconnect.dao.ApplicationDAO;
import com.careerconnect.dao.CompanyDAO;
import com.careerconnect.dao.NotificationDAO;
import com.careerconnect.model.Application;
import com.careerconnect.model.Company;
import com.careerconnect.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/recruiter/*")
public class RecruiterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) path = "/status";

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !"RECRUITER".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        switch (path) {
            case "/update-application":
                updateApplicationStatus(request, response);
                break;
            case "/company/update":
                updateCompany(request, response, session);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp");
                break;
        }
    }

    private void updateApplicationStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int appId = Integer.parseInt(request.getParameter("applicationId"));
        String status = request.getParameter("status"); // ACCEPTED, REJECTED, UNDER_REVIEW
        int applicantUserId = Integer.parseInt(request.getParameter("userId"));
        String jobTitle = request.getParameter("jobTitle");

        if (applicationDAO.updateApplicationStatus(appId, status)) {
            String title = "Application Update: " + status;
            String message = "Your application for '" + jobTitle + "' status has been updated to: " + status;
            notificationDAO.createNotification(applicantUserId, title, message);

            response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp?msg=status_updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp?msg=error");
        }
    }

    private void updateCompany(HttpServletRequest request, HttpServletResponse response, HttpSession session) 
            throws IOException {
        Integer companyId = (Integer) session.getAttribute("companyId");
        if (companyId != null) {
            String name = request.getParameter("name");
            String website = request.getParameter("website");
            String location = request.getParameter("location");
            String industry = request.getParameter("industry");
            String description = request.getParameter("description");

            Company c = companyDAO.getCompanyById(companyId);
            if (c != null) {
                c.setName(name);
                c.setWebsite(website);
                c.setLocation(location);
                c.setIndustry(industry);
                c.setDescription(description);
                companyDAO.updateCompany(c);
            }
        }
        response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp?msg=company_updated");
    }
}
