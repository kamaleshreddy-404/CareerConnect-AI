package com.careerconnect.controller;

import com.careerconnect.dao.ApplicationDAO;
import com.careerconnect.dao.NotificationDAO;
import com.careerconnect.dao.UserDAO;
import com.careerconnect.model.Application;
import com.careerconnect.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/seeker/*")
public class JobSeekerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getPathInfo();
        if (path == null) path = "/apply";

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        switch (path) {
            case "/apply":
                handleApply(request, response, user);
                break;
            case "/profile/update":
                handleProfileUpdate(request, response, user);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/user-dashboard.jsp");
                break;
        }
    }

    private void handleApply(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        int jobId = Integer.parseInt(request.getParameter("jobId"));
        String coverLetter = request.getParameter("coverLetter");
        String resumePath = request.getParameter("resumePath");
        if (resumePath == null || resumePath.isEmpty()) {
            resumePath = user.getName().toLowerCase().replace(" ", "_") + "_resume.pdf";
        }

        if (applicationDAO.hasUserApplied(user.getId(), jobId)) {
            response.sendRedirect(request.getContextPath() + "/jobs/detail?id=" + jobId + "&msg=already_applied");
            return;
        }

        Application app = new Application();
        app.setJobId(jobId);
        app.setUserId(user.getId());
        app.setResumePath(resumePath);
        app.setCoverLetter(coverLetter);
        app.setStatus("APPLIED");
        app.setAiScore(85); // Standard candidate ATS match score

        if (applicationDAO.applyForJob(app)) {
            notificationDAO.createNotification(user.getId(), "Application Submitted", 
                "Your job application has been successfully submitted!");
            response.sendRedirect(request.getContextPath() + "/user-dashboard.jsp?msg=applied_success");
        } else {
            response.sendRedirect(request.getContextPath() + "/jobs/detail?id=" + jobId + "&msg=apply_error");
        }
    }

    private void handleProfileUpdate(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String bio = request.getParameter("bio");

        user.setName(name);
        user.setPhone(phone);
        user.setBio(bio);

        if (userDAO.updateUser(user)) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userName", name);
            response.sendRedirect(request.getContextPath() + "/profile.jsp?msg=profile_updated");
        } else {
            response.sendRedirect(request.getContextPath() + "/profile.jsp?msg=update_failed");
        }
    }
}
