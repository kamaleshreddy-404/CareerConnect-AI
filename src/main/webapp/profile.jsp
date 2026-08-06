<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.model.User, com.careerconnect.model.Education, com.careerconnect.model.Experience, com.careerconnect.dao.UserDAO, java.util.List"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    String update = request.getParameter("update");
    if ("true".equals(update)) {
        user.setName(request.getParameter("name"));
        user.setPhone(request.getParameter("phone"));
        user.setBio(request.getParameter("bio"));
        userDAO.updateUser(user);
        session.setAttribute("user", user);
        session.setAttribute("userName", user.getName());
        response.sendRedirect("profile.jsp?msg=profile_updated");
        return;
    }

    List<Education> eduList = userDAO.getUserEducation(user.getId());
    List<Experience> expList = userDAO.getUserExperience(user.getId());
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Candidate Profile - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="padding: 40px 24px;">
    
    <div class="dashboard-layout">
      <!-- Sidebar -->
      <jsp:include page="/includes/sidebar.jsp" />

      <!-- Main Profile Editor -->
      <main>
        
        <div style="margin-bottom: 28px;">
          <h1 style="font-size: 28px;">Candidate Profile & Resume Management</h1>
          <p style="color: var(--text-muted);">Keep your contact info, education, and resume up to date for campus recruiters</p>
        </div>

        <!-- Personal Info -->
        <div class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            👤 Personal & Contact Information
          </h3>

          <form action="profile.jsp" method="POST">
            <input type="hidden" name="update" value="true">

            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">Full Name</label>
                <input type="text" name="name" value="<%= user.getName() %>" class="form-control" required>
              </div>
              <div class="form-group">
                <label class="form-label">Email Address (Read-only)</label>
                <input type="email" value="<%= user.getEmail() %>" class="form-control" readonly>
              </div>
            </div>

            <div class="grid grid-2">
              <div class="form-group">
                <label class="form-label">Phone Number</label>
                <input type="tel" name="phone" value="<%= user.getPhone() != null ? user.getPhone() : "" %>" class="form-control">
              </div>
              <div class="form-group">
                <label class="form-label">Account Role</label>
                <input type="text" value="<%= user.getRole() %>" class="form-control" readonly>
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Professional Summary / Bio</label>
              <textarea name="bio" class="form-control" rows="3"><%= user.getBio() != null ? user.getBio() : "" %></textarea>
            </div>

            <button type="submit" class="btn btn-primary">Save Profile Changes</button>
          </form>
        </div>

        <!-- Resume PDF Manager -->
        <div class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            📄 Resume PDF Manager
          </h3>

          <div style="display: flex; justify-content: space-between; align-items: center; background: var(--bg-subtle); padding: 20px; border-radius: var(--radius-md);">
            <div>
              <h4 style="font-size: 16px;"><%= user.getName().toLowerCase().replace(" ", "_") %>_resume.pdf</h4>
              <p style="font-size: 13px; color: var(--text-muted);">PDF Document • ATS Score 88%</p>
            </div>
            <a href="download-resume?file=<%= user.getName().toLowerCase().replace(" ", "_") %>_resume.pdf" target="_blank" class="btn btn-outline">
              Preview Resume PDF
            </a>
          </div>
        </div>

        <!-- Education & Experience -->
        <div class="card">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            🎓 Education & Internships
          </h3>

          <div style="margin-bottom: 24px;">
            <h4 style="font-size: 16px; margin-bottom: 12px; color: var(--primary);">Academic Degree</h4>
            <% for(Education ed : eduList) { %>
              <div style="padding: 12px; border-left: 3px solid var(--primary); background: var(--bg-subtle); margin-bottom: 8px; border-radius: 0 var(--radius-sm) var(--radius-sm) 0;">
                <div style="font-weight: 600;"><%= ed.getDegree() %> — <%= ed.getGrade() %></div>
                <div style="font-size: 13px; color: var(--text-muted);"><%= ed.getInstitution() %> (<%= ed.getStartYear() %> - <%= ed.getEndYear() %>)</div>
              </div>
            <% } %>
          </div>

          <div>
            <h4 style="font-size: 16px; margin-bottom: 12px; color: var(--primary);">Internship & Work Experience</h4>
            <% for(Experience ex : expList) { %>
              <div style="padding: 12px; border-left: 3px solid var(--success); background: var(--bg-subtle); margin-bottom: 8px; border-radius: 0 var(--radius-sm) var(--radius-sm) 0;">
                <div style="font-weight: 600;"><%= ex.getTitle() %> at <%= ex.getCompanyName() %></div>
                <div style="font-size: 13px; color: var(--text-muted);"><%= ex.getLocation() %> (<%= ex.getStartDate() %> to <%= ex.getEndDate() %>)</div>
                <p style="font-size: 13px; margin-top: 4px;"><%= ex.getDescription() %></p>
              </div>
            <% } %>
          </div>
        </div>

      </main>
    </div>

  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
</body>
</html>
