package com.careerconnect.controller;

import com.careerconnect.dao.CompanyDAO;
import com.careerconnect.dao.RecruiterDAO;
import com.careerconnect.dao.UserDAO;
import com.careerconnect.model.Company;
import com.careerconnect.model.Recruiter;
import com.careerconnect.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/auth/*")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserDAO userDAO = new UserDAO();
    private RecruiterDAO recruiterDAO = new RecruiterDAO();
    private CompanyDAO companyDAO = new CompanyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/login";

        switch (action) {
            case "/logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/login.jsp?msg=logged_out");
                break;
            case "/forgot-password":
                request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        if (action == null) action = "/login";

        switch (action) {
            case "/login":
                handleLogin(request, response);
                break;
            case "/register-user":
                handleUserRegistration(request, response);
                break;
            case "/register-recruiter":
                handleRecruiterRegistration(request, response);
                break;
            case "/reset-password":
                handleResetPassword(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                break;
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Please provide both email and password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        User user = userDAO.authenticateUser(email.trim(), password.trim());
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin-dashboard.jsp");
            } else if ("RECRUITER".equals(user.getRole())) {
                Recruiter recruiter = recruiterDAO.getRecruiterByUserId(user.getId());
                if (recruiter != null) {
                    session.setAttribute("recruiterId", recruiter.getId());
                    session.setAttribute("companyId", recruiter.getCompanyId());
                }
                response.sendRedirect(request.getContextPath() + "/recruiter-dashboard.jsp");
            } else {
                response.sendRedirect(request.getContextPath() + "/user-dashboard.jsp");
            }
        } else {
            request.setAttribute("error", "Invalid email or password. Please try again.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void handleUserRegistration(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String bio = request.getParameter("bio");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole("JOB_SEEKER");
        user.setBio(bio);

        if (userDAO.registerUser(user)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=registered");
        } else {
            request.setAttribute("error", "Registration failed. Email may already be in use.");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    private void handleRecruiterRegistration(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String companyName = request.getParameter("companyName");
        String position = request.getParameter("position");
        String website = request.getParameter("website");
        String industry = request.getParameter("industry");
        String location = request.getParameter("location");

        // 1. Create User
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole("RECRUITER");
        user.setBio("Recruiter at " + companyName);

        if (userDAO.registerUser(user)) {
            // 2. Create Company
            Company company = new Company();
            company.setName(companyName);
            company.setWebsite(website);
            company.setIndustry(industry);
            company.setLocation(location);
            company.setDescription("Innovating in " + industry);
            int companyId = companyDAO.addCompany(company);

            // 3. Create Recruiter
            Recruiter recruiter = new Recruiter();
            recruiter.setUserId(user.getId());
            recruiter.setCompanyId(companyId);
            recruiter.setPosition(position);
            recruiter.setStatus("APPROVED"); // Auto approve for smooth demo
            recruiterDAO.registerRecruiter(recruiter);

            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=recruiter_registered");
        } else {
            request.setAttribute("error", "Recruiter registration failed. Email may be already registered.");
            request.getRequestDispatcher("/register.jsp?tab=recruiter").forward(request, response);
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String newPassword = request.getParameter("newPassword");

        if (userDAO.resetPassword(email, newPassword)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=password_reset");
        } else {
            request.setAttribute("error", "Email address not found.");
            request.getRequestDispatcher("/forgot-password.jsp").forward(request, response);
        }
    }
}
