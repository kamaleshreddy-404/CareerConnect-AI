<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.dao.UserDAO, com.careerconnect.dao.RecruiterDAO, com.careerconnect.model.User, com.careerconnect.model.Recruiter"%>
<%
    String logoutParam = request.getParameter("logout");
    if ("true".equals(logoutParam)) {
        session.invalidate();
    }

    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String error = null;

    if (email != null && password != null && !email.trim().isEmpty() && !password.trim().isEmpty()) {
        UserDAO userDAO = new UserDAO();
        User user = userDAO.authenticateUser(email.trim(), password.trim());
        if (user != null) {
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect("admin-dashboard.jsp");
                return;
            } else if ("RECRUITER".equals(user.getRole())) {
                RecruiterDAO recruiterDAO = new RecruiterDAO();
                Recruiter r = recruiterDAO.getRecruiterByUserId(user.getId());
                if (r != null) {
                    session.setAttribute("recruiterId", r.getId());
                    session.setAttribute("companyId", r.getCompanyId());
                }
                response.sendRedirect("recruiter-dashboard.jsp");
                return;
            } else {
                response.sendRedirect("user-dashboard.jsp");
                return;
            }
        } else {
            error = "Invalid credentials. Please check your email and password.";
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Log In - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="max-width: 460px; margin: 60px auto;">
    <div class="card" style="padding: 36px;">
      <div style="text-align: center; margin-bottom: 28px;">
        <h2 style="font-size: 26px;">Welcome Back</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-top: 4px;">Sign in to your CareerConnect AI Portal</p>
      </div>

      <% if (error != null) { %>
        <div style="background: #fee2e2; color: #dc2626; padding: 12px; border-radius: 8px; font-size: 14px; margin-bottom: 20px;">
          ⚠️ <%= error %>
        </div>
      <% } %>

      <form action="login.jsp" method="POST">
        <div class="form-group">
          <label class="form-label">Email Address</label>
          <input type="email" name="email" class="form-control" placeholder="name@college.edu or company.com" required>
        </div>

        <div class="form-group">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
            <label class="form-label" style="margin-bottom: 0;">Password</label>
            <a href="forgot-password.jsp" style="font-size: 13px;">Forgot Password?</a>
          </div>
          <input type="password" name="password" class="form-control" placeholder="Enter password" required>
        </div>

        <button type="submit" class="btn btn-primary btn-lg" style="width: 100%; margin-top: 8px;">Log In</button>
      </form>

      <!-- Quick Demo Accounts for Student Evaluation / Interviews -->
      <div style="margin-top: 28px; padding-top: 20px; border-top: 1px solid var(--border-color); background: var(--bg-subtle); padding: 16px; border-radius: 12px;">
        <h5 style="font-size: 13px; margin-bottom: 10px; color: var(--primary);">🔑 Quick Demo Accounts (One-Click Log In):</h5>
        <div style="display: flex; flex-direction: column; gap: 8px;">
          <button type="button" onclick="loginDemo('alex.student@college.edu', 'password123')" class="btn btn-outline btn-sm" style="justify-content: flex-start;">
            👤 Candidate: alex.student@college.edu
          </button>
          <button type="button" onclick="loginDemo('recruiter@techcorp.com', 'password123')" class="btn btn-outline btn-sm" style="justify-content: flex-start;">
            🏢 Recruiter: recruiter@techcorp.com
          </button>
          <button type="button" onclick="loginDemo('admin@careerconnect.ai', 'password123')" class="btn btn-outline btn-sm" style="justify-content: flex-start;">
            🛡️ Admin: admin@careerconnect.ai
          </button>
        </div>
      </div>

      <div style="text-align: center; margin-top: 24px; font-size: 14px; color: var(--text-muted);">
        Don't have an account? <a href="register.jsp" style="font-weight: 600;">Register here</a>
      </div>
    </div>
  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
  <script>
    function loginDemo(email, pass) {
      document.querySelector('input[name="email"]').value = email;
      document.querySelector('input[name="password"]').value = pass;
      document.querySelector('form').submit();
    }
  </script>
</body>
</html>
