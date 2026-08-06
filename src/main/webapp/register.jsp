<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.dao.UserDAO, com.careerconnect.dao.RecruiterDAO, com.careerconnect.dao.CompanyDAO, com.careerconnect.model.User, com.careerconnect.model.Company, com.careerconnect.model.Recruiter"%>
<%
    String formType = request.getParameter("formType");
    String error = null;

    if ("seeker".equals(formType)) {
        User u = new User();
        u.setName(request.getParameter("name"));
        u.setEmail(request.getParameter("email"));
        u.setPassword(request.getParameter("password"));
        u.setPhone(request.getParameter("phone"));
        u.setRole("JOB_SEEKER");
        u.setBio(request.getParameter("bio"));

        UserDAO userDAO = new UserDAO();
        if (userDAO.registerUser(u)) {
            session.setAttribute("user", u);
            session.setAttribute("userId", u.getId());
            session.setAttribute("userName", u.getName());
            session.setAttribute("userRole", u.getRole());
            response.sendRedirect("user-dashboard.jsp?msg=registered");
            return;
        } else {
            error = "Registration failed. Email address may already be in use.";
        }
    } else if ("recruiter".equals(formType)) {
        User u = new User();
        u.setName(request.getParameter("name"));
        u.setEmail(request.getParameter("email"));
        u.setPassword(request.getParameter("password"));
        u.setPhone(request.getParameter("phone"));
        u.setRole("RECRUITER");
        u.setBio("Recruiter at " + request.getParameter("companyName"));

        UserDAO userDAO = new UserDAO();
        if (userDAO.registerUser(u)) {
            CompanyDAO compDAO = new CompanyDAO();
            Company comp = new Company();
            comp.setName(request.getParameter("companyName"));
            comp.setIndustry(request.getParameter("industry"));
            comp.setLocation(request.getParameter("location"));
            comp.setDescription("Leading enterprise in " + request.getParameter("industry"));
            int companyId = compDAO.addCompany(comp);

            RecruiterDAO recDAO = new RecruiterDAO();
            Recruiter r = new Recruiter();
            r.setUserId(u.getId());
            r.setCompanyId(companyId);
            r.setPosition(request.getParameter("position"));
            r.setStatus("APPROVED");
            recDAO.registerRecruiter(r);

            session.setAttribute("user", u);
            session.setAttribute("userId", u.getId());
            session.setAttribute("userName", u.getName());
            session.setAttribute("userRole", u.getRole());
            session.setAttribute("recruiterId", r.getId());
            session.setAttribute("companyId", companyId);

            response.sendRedirect("recruiter-dashboard.jsp?msg=recruiter_registered");
            return;
        } else {
            error = "Recruiter registration failed. Work email may already be registered.";
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Register Account - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="max-width: 540px; margin: 60px auto;">
    <div class="card" style="padding: 36px;">
      <div style="text-align: center; margin-bottom: 24px;">
        <h2 style="font-size: 26px;">Create your Account</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-top: 4px;">Join the CareerConnect AI placement network</p>
      </div>

      <!-- Role Switcher -->
      <div style="display: flex; gap: 8px; margin-bottom: 28px; background: var(--bg-subtle); padding: 4px; border-radius: var(--radius-md);">
        <button type="button" id="tab-seeker" onclick="switchTab('seeker')" class="btn btn-primary btn-sm" style="flex: 1;">Candidate / Job Seeker</button>
        <button type="button" id="tab-recruiter" onclick="switchTab('recruiter')" class="btn btn-outline btn-sm" style="flex: 1;">Recruiter / Employer</button>
      </div>

      <% if (error != null) { %>
        <div style="background: #fee2e2; color: #dc2626; padding: 12px; border-radius: 8px; font-size: 14px; margin-bottom: 20px;">
          ⚠️ <%= error %>
        </div>
      <% } %>

      <!-- Candidate Form -->
      <form id="form-seeker" action="register.jsp" method="POST">
        <input type="hidden" name="formType" value="seeker">
        <div class="form-group">
          <label class="form-label">Full Name</label>
          <input type="text" name="name" class="form-control" placeholder="Alex Johnson" required>
        </div>
        <div class="form-group">
          <label class="form-label">College / Personal Email</label>
          <input type="email" name="email" class="form-control" placeholder="alex@college.edu" required>
        </div>
        <div class="form-group">
          <label class="form-label">Password</label>
          <input type="password" name="password" class="form-control" placeholder="Create password" required>
        </div>
        <div class="form-group">
          <label class="form-label">Phone Number</label>
          <input type="tel" name="phone" class="form-control" placeholder="9876543210">
        </div>
        <div class="form-group">
          <label class="form-label">Bio / Career Goal</label>
          <textarea name="bio" class="form-control" rows="3" placeholder="Final year CS student passionate about Java & React..."></textarea>
        </div>
        <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">Create Candidate Account</button>
      </form>

      <!-- Recruiter Form -->
      <form id="form-recruiter" action="register.jsp" method="POST" style="display: none;">
        <input type="hidden" name="formType" value="recruiter">
        <div class="form-group">
          <label class="form-label">Full Name</label>
          <input type="text" name="name" class="form-control" placeholder="Jane Doe" required>
        </div>
        <div class="form-group">
          <label class="form-label">Work Email</label>
          <input type="email" name="email" class="form-control" placeholder="jane@company.com" required>
        </div>
        <div class="form-group">
          <label class="form-label">Password</label>
          <input type="password" name="password" class="form-control" placeholder="Create password" required>
        </div>
        <div class="form-group">
          <label class="form-label">Phone Number</label>
          <input type="tel" name="phone" class="form-control" placeholder="9876543210">
        </div>
        <div class="form-group">
          <label class="form-label">Company Name</label>
          <input type="text" name="companyName" class="form-control" placeholder="TechCorp Innovations" required>
        </div>
        <div class="form-group">
          <label class="form-label">Designation / Role</label>
          <input type="text" name="position" class="form-control" placeholder="Lead Technical Recruiter" required>
        </div>
        <div class="grid grid-2">
          <div class="form-group">
            <label class="form-label">Industry</label>
            <input type="text" name="industry" class="form-control" placeholder="IT & Software">
          </div>
          <div class="form-group">
            <label class="form-label">Location</label>
            <input type="text" name="location" class="form-control" placeholder="Bangalore, India">
          </div>
        </div>
        <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">Create Recruiter Account</button>
      </form>

      <div style="text-align: center; margin-top: 24px; font-size: 14px; color: var(--text-muted);">
        Already have an account? <a href="login.jsp" style="font-weight: 600;">Sign In</a>
      </div>
    </div>
  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
  <script>
    function switchTab(tab) {
      if (tab === 'seeker') {
        document.getElementById('form-seeker').style.display = 'block';
        document.getElementById('form-recruiter').style.display = 'none';
        document.getElementById('tab-seeker').className = 'btn btn-primary btn-sm';
        document.getElementById('tab-recruiter').className = 'btn btn-outline btn-sm';
      } else {
        document.getElementById('form-seeker').style.display = 'none';
        document.getElementById('form-recruiter').style.display = 'block';
        document.getElementById('tab-seeker').className = 'btn btn-outline btn-sm';
        document.getElementById('tab-recruiter').className = 'btn btn-primary btn-sm';
      }
    }
  </script>
</body>
</html>
