<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.dao.JobDAO, com.careerconnect.dao.ApplicationDAO, com.careerconnect.dao.NotificationDAO, com.careerconnect.model.Job, com.careerconnect.model.User, com.careerconnect.model.Application"%>
<%
    Job job = (Job) request.getAttribute("job");
    if (job == null) {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                JobDAO jobDAO = new JobDAO();
                job = jobDAO.getJobById(Integer.parseInt(idStr));
            } catch (Exception ignored) {}
        }
    }

    User user = (User) session.getAttribute("user");

    String submitApp = request.getParameter("submitApp");
    String appMsg = null;
    if ("true".equals(submitApp) && user != null && job != null) {
        ApplicationDAO appDAO = new ApplicationDAO();
        NotificationDAO notifDAO = new NotificationDAO();
        if (!appDAO.hasUserApplied(user.getId(), job.getId())) {
            Application app = new Application();
            app.setJobId(job.getId());
            app.setUserId(user.getId());
            app.setResumePath(user.getName().toLowerCase().replace(" ", "_") + "_resume.pdf");
            app.setCoverLetter(request.getParameter("coverLetter"));
            app.setStatus("APPLIED");
            app.setAiScore(85);
            appDAO.applyForJob(app);
            notifDAO.createNotification(user.getId(), "Application Submitted", "Successfully applied for " + job.getTitle() + " at " + job.getCompanyName());
            response.sendRedirect("user-dashboard.jsp?msg=applied_success");
            return;
        } else {
            appMsg = "You have already submitted an application for this position.";
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><%= (job != null) ? job.getTitle() : "Job Details" %> - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- Header Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <% if (job != null) { %>
    <div class="container" style="max-width: 900px; margin: 40px auto;">
      
      <% if (appMsg != null) { %>
        <div style="background: #fef3c7; color: #d97706; padding: 12px 16px; border-radius: var(--radius-md); margin-bottom: 24px; font-weight: 500;">
          ⚠️ <%= appMsg %>
        </div>
      <% } %>

      <div class="card" style="padding: 36px; margin-bottom: 32px;">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 20px; margin-bottom: 24px;">
          <div style="display: flex; gap: 20px; align-items: center;">
            <div style="width: 64px; height: 64px; font-size: 28px; border-radius: 16px; background: var(--primary-light); color: var(--primary); display: flex; align-items: center; justify-content: center; font-weight: 800;">
              <%= (job.getCompanyName() != null && !job.getCompanyName().isEmpty()) ? job.getCompanyName().substring(0,1) : "C" %>
            </div>
            <div>
              <h1 style="font-size: 28px; margin-bottom: 4px;"><%= job.getTitle() %></h1>
              <p style="color: var(--text-muted); font-size: 16px;"><%= job.getCompanyName() %> • <%= job.getLocation() %></p>
            </div>
          </div>

          <% if (user == null || "JOB_SEEKER".equals(user.getRole())) { %>
            <button onclick="openModal('applyModal')" class="btn btn-primary btn-lg">🚀 Apply Now</button>
          <% } %>
        </div>

        <div class="job-tags" style="margin-bottom: 24px;">
          <span class="tag tag-primary"><%= job.getCategoryName() %></span>
          <span class="tag"><%= job.getJobType() %></span>
          <span class="tag tag-success"><%= job.getSalaryRange() %></span>
          <span class="tag"><%= job.getExperienceLevel() %></span>
        </div>

        <div style="border-top: 1px solid var(--border-color); padding-top: 24px; margin-top: 24px;">
          <h3 style="font-size: 20px; margin-bottom: 12px;">Job Role Overview</h3>
          <p style="font-size: 15px; color: var(--text-main); white-space: pre-line; margin-bottom: 24px; line-height: 1.7;">
            <%= job.getDescription() %>
          </p>

          <h3 style="font-size: 20px; margin-bottom: 12px;">Requirements & Key Qualifications</h3>
          <p style="font-size: 15px; color: var(--text-main); white-space: pre-line; line-height: 1.7;">
            <%= job.getRequirements() %>
          </p>
        </div>
      </div>

    </div>

    <!-- Apply Modal Dialog -->
    <div id="applyModal" class="modal-overlay">
      <div class="modal">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
          <h3 style="font-size: 20px;">Apply for <%= job.getTitle() %></h3>
          <button onclick="closeModal('applyModal')" style="background: none; border: none; font-size: 24px; cursor: pointer; color: var(--text-muted);">&times;</button>
        </div>

        <% if (user == null) { %>
          <div style="text-align: center; padding: 24px;">
            <p style="margin-bottom: 20px; font-size: 15px; color: var(--text-muted);">Please log in or register to submit your placement application.</p>
            <div style="display: flex; gap: 12px; justify-content: center;">
              <a href="login.jsp" class="btn btn-primary">Log In to Apply</a>
              <a href="register.jsp" class="btn btn-outline">Register Account</a>
            </div>
          </div>
        <% } else { %>
          <form action="job-detail.jsp" method="POST">
            <input type="hidden" name="id" value="<%= job.getId() %>">
            <input type="hidden" name="submitApp" value="true">

            <div class="form-group">
              <label class="form-label">Candidate Name</label>
              <input type="text" class="form-control" value="<%= user.getName() %>" readonly>
            </div>

            <div class="form-group">
              <label class="form-label">Attached Resume PDF</label>
              <input type="text" class="form-control" value="<%= user.getName().toLowerCase().replace(" ", "_") %>_resume.pdf" readonly>
              <small style="color: var(--text-muted); font-size: 12px;">Pre-loaded from profile</small>
            </div>

            <div class="form-group">
              <label class="form-label">Note to Recruiter / Cover Letter</label>
              <textarea name="coverLetter" class="form-control" rows="4" placeholder="Briefly state why your skills match this role..." required></textarea>
            </div>

            <div style="display: flex; gap: 12px; justify-content: flex-end;">
              <button type="button" onclick="closeModal('applyModal')" class="btn btn-outline">Cancel</button>
              <button type="submit" class="btn btn-primary">Submit Application</button>
            </div>
          </form>
        <% } %>
      </div>
    </div>
  <% } else { %>
    <div class="container" style="text-align: center; padding: 80px 20px;">
      <h2>Job Opening Not Found</h2>
      <a href="jobs.jsp" class="btn btn-primary" style="margin-top: 16px;">Browse All Jobs</a>
    </div>
  <% } %>

  <!-- Footer -->
  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
</body>
</html>
