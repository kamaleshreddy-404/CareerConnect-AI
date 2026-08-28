<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.model.User, com.careerconnect.model.Job, com.careerconnect.model.Application, com.careerconnect.model.Category, com.careerconnect.dao.JobDAO, com.careerconnect.dao.ApplicationDAO, com.careerconnect.dao.CategoryDAO, java.util.List, java.util.ArrayList"%>
<%
    User user = (User) session.getAttribute("user");
    int recruiterId = 1;
    int companyId = 1;
    if (user != null) {
        Integer rId = (Integer) session.getAttribute("recruiterId");
        if (rId != null) recruiterId = rId;
        Integer cId = (Integer) session.getAttribute("companyId");
        if (cId != null) companyId = cId;
    }

    JobDAO jobDAO = new JobDAO();
    ApplicationDAO appDAO = new ApplicationDAO();
    CategoryDAO catDAO = new CategoryDAO();

    // Handle Form Actions
    String action = request.getParameter("action");
    if ("postJob".equals(action)) {
        Job job = new Job();
        job.setCompanyId(companyId);
        job.setRecruiterId(recruiterId);
        String catIdStr = request.getParameter("categoryId");
        job.setCategoryId((catIdStr != null && !catIdStr.isEmpty()) ? Integer.parseInt(catIdStr) : 1);
        job.setTitle(request.getParameter("title"));
        job.setDescription(request.getParameter("description"));
        job.setRequirements(request.getParameter("requirements"));
        job.setLocation(request.getParameter("location"));
        job.setJobType(request.getParameter("jobType"));
        job.setSalaryRange(request.getParameter("salaryRange"));
        job.setExperienceLevel(request.getParameter("experienceLevel"));
        job.setStatus("ACTIVE");
        jobDAO.createJob(job);
        response.sendRedirect("recruiter-dashboard.jsp?msg=job_posted");
        return;
    } else if ("deleteJob".equals(action)) {
        int jobId = Integer.parseInt(request.getParameter("jobId"));
        jobDAO.deleteJob(jobId);
        response.sendRedirect("recruiter-dashboard.jsp?msg=job_deleted");
        return;
    } else if ("updateStatus".equals(action)) {
        int appId = Integer.parseInt(request.getParameter("applicationId"));
        String newStatus = request.getParameter("status");
        appDAO.updateApplicationStatus(appId, newStatus);
        response.sendRedirect("recruiter-dashboard.jsp?msg=status_updated");
        return;
    }

    List<Job> myJobs = jobDAO.getJobsByRecruiter(recruiterId);
    List<Application> applicants = appDAO.getApplicationsByRecruiter(recruiterId);
    List<Category> categories = catDAO.getAllCategories();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Recruiter Dashboard - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="padding: 40px 24px;">
    
    <div class="dashboard-layout">
      <!-- Sidebar -->
      <jsp:include page="/includes/sidebar.jsp" />

      <!-- Main Content -->
      <main>
        
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 28px; flex-wrap: wrap; gap: 16px;">
          <div>
            <h1 style="font-size: 28px;">Recruiter Control Center</h1>
            <p style="color: var(--text-muted);">Manage placement job postings, screen candidates, and track applicants</p>
          </div>
          <button onclick="openModal('postJobModal')" class="btn btn-primary btn-lg">➕ Post New Job</button>
        </div>

        <!-- Quick Stats -->
        <div class="grid grid-3" style="margin-bottom: 32px;">
          <div class="stat-card">
            <div class="stat-icon">💼</div>
            <div>
              <div class="stat-val"><%= myJobs != null ? myJobs.size() : 0 %></div>
              <div class="stat-label">Active Job Openings</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #e0f2fe; color: #0284c7;">👥</div>
            <div>
              <div class="stat-val"><%= applicants != null ? applicants.size() : 0 %></div>
              <div class="stat-label">Received Applicants</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #d1fae5; color: #059669;">🎯</div>
            <div>
              <div class="stat-val">85%</div>
              <div class="stat-label">Avg Candidate ATS Match</div>
            </div>
          </div>
        </div>

        <!-- Applicants Section -->
        <div id="applicants" class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            📄 Candidate Applicants Management
          </h3>

          <% if (applicants != null && !applicants.isEmpty()) { %>
            <div style="overflow-x: auto;">
              <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
                <thead>
                  <tr style="border-bottom: 2px solid var(--border-color); color: var(--text-muted);">
                    <th style="padding: 12px;">Candidate</th>
                    <th style="padding: 12px;">Applied For</th>
                    <th style="padding: 12px;">AI Match Score</th>
                    <th style="padding: 12px;">Resume</th>
                    <th style="padding: 12px;">Current Status</th>
                    <th style="padding: 12px;">Action</th>
                  </tr>
                </thead>
                <tbody>
                  <% for(Application app : applicants) { %>
                    <tr style="border-bottom: 1px solid var(--border-color);">
                      <td style="padding: 14px 12px;">
                        <div style="font-weight: 600;"><%= app.getApplicantName() != null ? app.getApplicantName() : "Alex Johnson" %></div>
                        <div style="font-size: 12px; color: var(--text-muted);"><%= app.getApplicantEmail() != null ? app.getApplicantEmail() : "alex@college.edu" %> • <%= app.getApplicantPhone() != null ? app.getApplicantPhone() : "9876543213" %></div>
                      </td>
                      <td style="padding: 14px 12px; font-weight: 500;"><%= app.getJobTitle() != null ? app.getJobTitle() : "Software Engineer" %></td>
                      <td style="padding: 14px 12px;">
                        <span class="tag tag-primary"><%= app.getAiScore() %>/100 ATS Match</span>
                      </td>
                      <td style="padding: 14px 12px;">
                        <a href="download-resume?file=<%= app.getResumePath() %>" target="_blank" class="btn btn-outline btn-sm">
                          📥 Resume PDF
                        </a>
                      </td>
                      <td style="padding: 14px 12px;">
                        <span class="badge badge-<%= app.getStatus() != null ? app.getStatus().toLowerCase() : "applied" %>"><%= app.getStatus() %></span>
                      </td>
                      <td style="padding: 14px 12px;">
                        <div style="display: flex; gap: 6px;">
                          <form action="recruiter-dashboard.jsp" method="POST" style="display: inline;">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="applicationId" value="<%= app.getId() %>">
                            <input type="hidden" name="status" value="ACCEPTED">
                            <button type="submit" class="btn btn-success btn-sm">Accept</button>
                          </form>
                          <form action="recruiter-dashboard.jsp" method="POST" style="display: inline;">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="applicationId" value="<%= app.getId() %>">
                            <input type="hidden" name="status" value="REJECTED">
                            <button type="submit" class="btn btn-danger btn-sm">Reject</button>
                          </form>
                        </div>
                      </td>
                    </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
          <% } else { %>
            <p style="color: var(--text-muted); text-align: center; padding: 24px;">No applications received yet.</p>
          <% } %>
        </div>

        <!-- Active Postings -->
        <div class="card">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            💼 My Active Job Postings
          </h3>

          <div class="grid grid-2">
            <% if (myJobs != null) { for(Job job : myJobs) { %>
              <div class="card job-card" style="background: var(--bg-subtle);">
                <div>
                  <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 12px;">
                    <h4 style="font-size: 17px;"><%= job.getTitle() %></h4>
                    <span class="tag tag-primary"><%= job.getJobType() %></span>
                  </div>
                  <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 12px;"><%= job.getLocation() %> • <%= job.getSalaryRange() %></p>
                </div>

                <div class="job-card-footer">
                  <span style="font-size: 13px; font-weight: 600;"><%= job.getApplicantCount() %> Applicants</span>
                  <form action="recruiter-dashboard.jsp" method="POST" onsubmit="return confirm('Delete this job posting?');">
                    <input type="hidden" name="action" value="deleteJob">
                    <input type="hidden" name="jobId" value="<%= job.getId() %>">
                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                  </form>
                </div>
              </div>
            <% } } %>
          </div>
        </div>

      </main>
    </div>

  </div>

  <!-- Create Job Modal -->
  <div id="postJobModal" class="modal-overlay">
    <div class="modal" style="max-width: 680px;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="font-size: 20px;">Post New Placement Opening</h3>
        <button onclick="closeModal('postJobModal')" style="background: none; border: none; font-size: 24px; cursor: pointer; color: var(--text-muted);">&times;</button>
      </div>

      <form action="recruiter-dashboard.jsp" method="POST">
        <input type="hidden" name="action" value="postJob">

        <div class="form-group">
          <label class="form-label">Job Title</label>
          <input type="text" name="title" class="form-control" placeholder="e.g. Graduate Software Engineer (Java & React)" required>
        </div>

        <div class="grid grid-2">
          <div class="form-group">
            <label class="form-label">Category</label>
            <select name="categoryId" class="form-control" required>
              <% if (categories != null) { for(Category c : categories) { %>
                <option value="<%= c.getId() %>"><%= c.getName() %></option>
              <% } } %>
            </select>
          </div>
          <div class="form-group">
            <label class="form-label">Job Type</label>
            <select name="jobType" class="form-control" required>
              <option value="FULL_TIME">Full Time</option>
              <option value="INTERNSHIP">Internship</option>
              <option value="REMOTE">Remote</option>
              <option value="CONTRACT">Contract</option>
            </select>
          </div>
        </div>

        <div class="grid grid-2">
          <div class="form-group">
            <label class="form-label">Location</label>
            <input type="text" name="location" class="form-control" placeholder="Bangalore, India" required>
          </div>
          <div class="form-group">
            <label class="form-label">Salary Package / Stipend</label>
            <input type="text" name="salaryRange" class="form-control" placeholder="₹8,00,000 - ₹12,00,000 LPA" required>
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">Experience Level</label>
          <input type="text" name="experienceLevel" class="form-control" placeholder="Entry Level (Freshers)" required>
        </div>

        <div class="form-group">
          <label class="form-label">Job Description</label>
          <textarea name="description" class="form-control" rows="3" required></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">Requirements & Eligibility</label>
          <textarea name="requirements" class="form-control" rows="3" required></textarea>
        </div>

        <div style="display: flex; gap: 12px; justify-content: flex-end;">
          <button type="button" onclick="closeModal('postJobModal')" class="btn btn-outline">Cancel</button>
          <button type="submit" class="btn btn-primary">Publish Opening</button>
        </div>
      </form>
    </div>
  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
</body>
</html>
