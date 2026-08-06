<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.dao.JobDAO, com.careerconnect.dao.CategoryDAO, com.careerconnect.model.Job, com.careerconnect.model.Category, java.util.List"%>
<%
    JobDAO jobDAO = new JobDAO();
    CategoryDAO categoryDAO = new CategoryDAO();

    String keyword = request.getParameter("keyword");
    String categoryStr = request.getParameter("category");
    String location = request.getParameter("location");
    String type = request.getParameter("type");

    int categoryId = 0;
    if (categoryStr != null && !categoryStr.trim().isEmpty()) {
        try { categoryId = Integer.parseInt(categoryStr); } catch (Exception ignored) {}
    }

    if (keyword == null) keyword = "";
    if (location == null) location = "";
    if (type == null) type = "ALL";

    List<Job> jobs = jobDAO.searchJobs(keyword, categoryId, location, type);
    List<Category> categories = categoryDAO.getAllCategories();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Browse Jobs & Placement Opportunities - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>

  <!-- Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="padding: 40px 24px;">
    
    <div style="margin-bottom: 32px;">
      <h1 style="font-size: 32px;">Explore Placement Opportunities</h1>
      <p style="color: var(--text-muted);">Find entry-level roles, internships, and campus drives matching your skill set</p>
    </div>

    <div class="dashboard-layout" style="padding: 0;">
      
      <!-- Filter Sidebar -->
      <aside class="sidebar">
        <h3 style="font-size: 18px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
          🔍 Filter Openings
        </h3>

        <form action="jobs.jsp" method="GET">
          <div class="form-group">
            <label class="form-label">Keyword Search</label>
            <input type="text" name="keyword" value="<%= keyword %>" class="form-control" placeholder="Job title or skill (e.g. Java, React)...">
          </div>

          <div class="form-group">
            <label class="form-label">Category</label>
            <select name="category" class="form-control">
              <option value="0">All Categories</option>
              <% if (categories != null) { for (Category cat : categories) { %>
                <option value="<%= cat.getId() %>" <%= (categoryId == cat.getId()) ? "selected" : "" %>><%= cat.getName() %></option>
              <% } } %>
            </select>
          </div>

          <div class="form-group">
            <label class="form-label">Location</label>
            <input type="text" name="location" value="<%= location %>" class="form-control" placeholder="City or Remote">
          </div>

          <div class="form-group">
            <label class="form-label">Employment Type</label>
            <select name="type" class="form-control">
              <option value="ALL" <%= "ALL".equals(type) ? "selected" : "" %>>All Types</option>
              <option value="FULL_TIME" <%= "FULL_TIME".equals(type) ? "selected" : "" %>>Full Time</option>
              <option value="INTERNSHIP" <%= "INTERNSHIP".equals(type) ? "selected" : "" %>>Internship</option>
              <option value="REMOTE" <%= "REMOTE".equals(type) ? "selected" : "" %>>Remote</option>
              <option value="CONTRACT" <%= "CONTRACT".equals(type) ? "selected" : "" %>>Contract</option>
            </select>
          </div>

          <button type="submit" class="btn btn-primary" style="width: 100%;">Apply Filters</button>
          <a href="jobs.jsp" class="btn btn-outline" style="width: 100%; margin-top: 8px;">Clear Filters</a>
        </form>
      </aside>

      <!-- Jobs Grid -->
      <main>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
          <h3 style="font-size: 18px;"><%= (jobs != null) ? jobs.size() : 0 %> Jobs Available</h3>
        </div>

        <% if (jobs != null && !jobs.isEmpty()) { %>
          <div class="grid grid-2">
            <% for (Job j : jobs) { %>
              <div class="card job-card">
                <div>
                  <div class="job-card-header">
                    <div class="company-logo">
                      <%= (j.getCompanyName() != null && !j.getCompanyName().isEmpty()) ? j.getCompanyName().substring(0,1) : "C" %>
                    </div>
                    <div class="job-info">
                      <h3><a href="job-detail.jsp?id=<%= j.getId() %>"><%= j.getTitle() %></a></h3>
                      <div class="company-name"><%= j.getCompanyName() %> • <%= j.getLocation() %></div>
                    </div>
                  </div>
                  <p style="font-size: 14px; color: var(--text-muted); margin-bottom: 12px; line-clamp: 2; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                    <%= j.getDescription() %>
                  </p>
                  <div class="job-tags">
                    <span class="tag tag-primary"><%= j.getCategoryName() %></span>
                    <span class="tag"><%= j.getJobType() %></span>
                    <span class="tag tag-success"><%= j.getApplicantCount() %> Applicants</span>
                  </div>
                </div>

                <div class="job-card-footer">
                  <div class="salary"><%= j.getSalaryRange() %></div>
                  <a href="job-detail.jsp?id=<%= j.getId() %>" class="btn btn-outline btn-sm">View & Apply</a>
                </div>
              </div>
            <% } %>
          </div>
        <% } else { %>
          <div class="card" style="text-align: center; padding: 60px 20px;">
            <h3>No jobs found matching your search criteria.</h3>
            <p style="color: var(--text-muted); margin-top: 8px;">Try clearing filters or searching for different keywords.</p>
            <a href="jobs.jsp" class="btn btn-primary" style="margin-top: 16px;">View All Jobs</a>
          </div>
        <% } %>
      </main>

    </div>
  </div>

  <!-- Footer -->
  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
</body>
</html>
