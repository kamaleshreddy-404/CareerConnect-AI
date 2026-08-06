<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.dao.JobDAO, com.careerconnect.dao.CategoryDAO, com.careerconnect.dao.CompanyDAO, com.careerconnect.model.Job, com.careerconnect.model.Category, com.careerconnect.model.Company, java.util.List"%>
<%
    JobDAO jobDAO = new JobDAO();
    CategoryDAO categoryDAO = new CategoryDAO();
    CompanyDAO companyDAO = new CompanyDAO();

    List<Job> latestJobs = jobDAO.getRecentJobs(6);
    List<Category> categories = categoryDAO.getAllCategories();
    List<Company> companies = companyDAO.getAllCompanies();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>CareerConnect AI - College Placement & Recruitment Job Portal</title>
  <link rel="stylesheet" href="css/style.css">
  
  <!-- React & Babel Runtime for AI Components -->
  <script src="https://unpkg.com/react@17/umd/react.production.min.js" crossorigin></script>
  <script src="https://unpkg.com/react-dom@17/umd/react-dom.production.min.js" crossorigin></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
</head>
<body>

  <!-- Header Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <!-- Hero Section -->
  <header class="hero">
    <div class="container hero-content">
      <span class="tag tag-primary" style="margin-bottom: 16px; display: inline-block;">✨ AI-Powered Placement Platform</span>
      <h1 class="hero-title">Launch Your Dream Career with <span>CareerConnect AI</span></h1>
      <p class="hero-subtitle">
        Connecting engineering graduates, data scientists, and developers with top enterprise recruiters through smart ATS resume scoring and direct placement hiring.
      </p>

      <!-- Search Jobs Form -->
      <form action="jobs.jsp" method="GET" class="search-box">
        <div class="search-input-group">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#64748b" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input type="text" name="keyword" placeholder="Job title, skill (e.g. Java, React)...">
        </div>
        <div class="search-input-group">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#64748b" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
          <input type="text" name="location" placeholder="Location or Remote...">
        </div>
        <div class="search-input-group">
          <select name="category">
            <option value="0">All Categories</option>
            <% for(Category cat : categories) { %>
              <option value="<%= cat.getId() %>"><%= cat.getName() %></option>
            <% } %>
          </select>
        </div>
        <button type="submit" class="btn btn-primary btn-lg">Search Jobs</button>
      </form>

      <!-- Quick Platform Statistics -->
      <div style="display: flex; justify-content: center; gap: 40px; margin-top: 48px; flex-wrap: wrap;">
        <div><h3 style="font-size: 28px; color: var(--primary);">2,500+</h3><p style="font-size: 14px; color: var(--text-muted);">Active Openings</p></div>
        <div><h3 style="font-size: 28px; color: var(--primary);">450+</h3><p style="font-size: 14px; color: var(--text-muted);">Hiring Companies</p></div>
        <div><h3 style="font-size: 28px; color: var(--primary);">94%</h3><p style="font-size: 14px; color: var(--text-muted);">Placement Success</p></div>
        <div><h3 style="font-size: 28px; color: var(--primary);">88/100</h3><p style="font-size: 14px; color: var(--text-muted);">Avg ATS Match</p></div>
      </div>
    </div>
  </header>

  <!-- Main Content -->
  <main class="container" style="margin-top: 60px;">

    <!-- Popular Categories -->
    <section style="margin-bottom: 60px;">
      <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 32px;">
        <div>
          <h2 style="font-size: 28px;">Popular Placement Categories</h2>
          <p style="color: var(--text-muted);">Explore high-demand engineering & technology domains</p>
        </div>
        <a href="jobs.jsp" class="btn btn-outline">Browse All Categories</a>
      </div>

      <div class="grid grid-4">
        <% for (int i = 0; i < Math.min(8, categories.size()); i++) { 
            Category cat = categories.get(i);
        %>
          <a href="jobs.jsp?category=<%= cat.getId() %>" class="card" style="display: block; text-decoration: none;">
            <div style="width: 44px; height: 44px; border-radius: 12px; background: var(--primary-light); color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 20px; margin-bottom: 16px;">
              💻
            </div>
            <h3 style="font-size: 17px; margin-bottom: 6px;"><%= cat.getName() %></h3>
            <p style="font-size: 13px; color: var(--text-muted);"><%= cat.getDescription() %></p>
          </a>
        <% } %>
      </div>
    </section>

    <!-- Latest Jobs Section -->
    <section style="margin-bottom: 60px;">
      <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 32px;">
        <div>
          <h2 style="font-size: 28px;">Featured Campus & Entry Level Jobs</h2>
          <p style="color: var(--text-muted);">Latest placement drives verified by CareerConnect AI</p>
        </div>
        <a href="jobs.jsp" class="btn btn-primary">Browse All Jobs</a>
      </div>

      <div class="grid grid-2">
        <% for(Job job : latestJobs) { %>
          <div class="card job-card">
            <div>
              <div class="job-card-header">
                <div class="company-logo">
                  <%= (job.getCompanyName() != null && !job.getCompanyName().isEmpty()) ? job.getCompanyName().substring(0,1) : "C" %>
                </div>
                <div class="job-info">
                  <h3><a href="job-detail.jsp?id=<%= job.getId() %>"><%= job.getTitle() %></a></h3>
                  <div class="company-name"><%= job.getCompanyName() %> • <%= job.getLocation() %></div>
                </div>
              </div>
              <p style="font-size: 14px; color: var(--text-muted); line-clamp: 2; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;">
                <%= job.getDescription() %>
              </p>
              <div class="job-tags">
                <span class="tag tag-primary"><%= job.getCategoryName() %></span>
                <span class="tag"><%= job.getJobType() %></span>
                <span class="tag tag-success"><%= job.getExperienceLevel() %></span>
              </div>
            </div>

            <div class="job-card-footer">
              <div class="salary"><%= job.getSalaryRange() %></div>
              <a href="job-detail.jsp?id=<%= job.getId() %>" class="btn btn-outline btn-sm">Apply Now</a>
            </div>
          </div>
        <% } %>
      </div>
    </section>

    <!-- Top Hiring Partners -->
    <section style="margin-bottom: 60px;">
      <h2 style="font-size: 28px; text-align: center; margin-bottom: 12px;">Top Hiring Partners</h2>
      <p style="text-align: center; color: var(--text-muted); margin-bottom: 36px;">Trusted by leading enterprise software & AI research companies</p>
      
      <div class="grid grid-3">
        <% for(Company comp : companies) { %>
          <div class="card" style="text-align: center;">
            <div style="width: 64px; height: 64px; border-radius: 50%; background: var(--primary-light); color: var(--primary); font-size: 24px; font-weight: 800; display: flex; align-items: center; justify-content: center; margin: 0 auto 16px;">
              <%= comp.getName().substring(0,1) %>
            </div>
            <h3 style="font-size: 18px; margin-bottom: 4px;"><%= comp.getName() %></h3>
            <p style="font-size: 13px; color: var(--text-muted); margin-bottom: 16px;"><%= comp.getIndustry() %> • <%= comp.getLocation() %></p>
            <a href="jobs.jsp?keyword=<%= comp.getName() %>" class="btn btn-outline btn-sm">View Openings</a>
          </div>
        <% } %>
      </div>
    </section>

    <!-- AI Resume Match & ATS Optimizer Widget -->
    <section style="margin-bottom: 60px;">
      <div id="ai-resume-root"></div>
    </section>

  </main>

  <!-- Footer -->
  <jsp:include page="/includes/footer.jsp" />

  <script src="js/app.js"></script>
  <script type="text/babel" src="js/components.js"></script>
  <script type="text/babel">
    window.addEventListener('load', () => {
      if (window.renderAIResumeAnalyzer) {
        window.renderAIResumeAnalyzer('ai-resume-root');
      }
    });
  </script>
</body>
</html>
