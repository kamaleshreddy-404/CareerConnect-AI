<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<footer class="footer">
  <div class="container">
    <div class="footer-content">
      <div>
        <div class="footer-logo">CareerConnect AI</div>
        <p style="color: #94a3b8; font-size: 14px; margin-bottom: 20px; max-width: 340px;">
          Next-generation AI-powered placement and recruitment platform connecting college talent with top enterprise recruiters.
        </p>
        <div style="display: flex; gap: 8px; flex-wrap: wrap;">
          <span class="tag tag-primary">Java Servlets</span>
          <span class="tag tag-primary">JSP & MVC</span>
          <span class="tag tag-primary">React UI</span>
          <span class="tag tag-primary">MySQL</span>
        </div>
      </div>

      <div class="footer-col">
        <h4>Job Seekers</h4>
        <ul class="footer-links">
          <li><a href="jobs.jsp">Browse Jobs</a></li>
          <li><a href="user-dashboard.jsp">AI Resume Optimizer</a></li>
          <li><a href="jobs.jsp?type=INTERNSHIP">Campus Internships</a></li>
          <li><a href="register.jsp">Create Account</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Recruiters</h4>
        <ul class="footer-links">
          <li><a href="register.jsp?tab=recruiter">Post Openings</a></li>
          <li><a href="recruiter-dashboard.jsp">Applicant Dashboard</a></li>
          <li><a href="recruiter-dashboard.jsp">Candidate Search</a></li>
          <li><a href="login.jsp">Employer Login</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>Platform & Tech</h4>
        <ul class="footer-links">
          <li><a href="index.jsp">MVC Architecture</a></li>
          <li><a href="index.jsp">JDBC Connection</a></li>
          <li><a href="index.jsp">Apache Tomcat</a></li>
          <li><a href="index.jsp">Maven Build</a></li>
        </ul>
      </div>
    </div>

    <div class="footer-bottom">
      <p>© <%= java.time.Year.now().getValue() %> CareerConnect AI. Full Stack Java MVC Recruitment Portal.</p>
    </div>
  </div>
</footer>
