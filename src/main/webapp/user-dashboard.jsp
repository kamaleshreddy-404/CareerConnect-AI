<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.model.User, com.careerconnect.model.Application, com.careerconnect.model.Notification, com.careerconnect.dao.ApplicationDAO, com.careerconnect.dao.NotificationDAO, java.util.List"%>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    ApplicationDAO appDAO = new ApplicationDAO();
    NotificationDAO notifDAO = new NotificationDAO();

    List<Application> myApps = appDAO.getApplicationsByUser(user.getId());
    List<Notification> notifications = notifDAO.getNotificationsByUser(user.getId());
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Candidate Dashboard - CareerConnect AI</title>
  <link rel="stylesheet" href="css/style.css">

  <script src="https://unpkg.com/react@17/umd/react.production.min.js" crossorigin></script>
  <script src="https://unpkg.com/react-dom@17/umd/react-dom.production.min.js" crossorigin></script>
  <script src="https://unpkg.com/@babel/standalone/babel.min.js"></script>
</head>
<body>

  <!-- Navbar -->
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="padding: 40px 24px;">
    
    <div class="dashboard-layout">
      <!-- Sidebar -->
      <jsp:include page="/includes/sidebar.jsp" />

      <!-- Main Portal Content -->
      <main>
        
        <!-- Welcome Banner -->
        <div class="card" style="background: linear-gradient(135deg, var(--primary), #1d4ed8); color: white; margin-bottom: 32px; padding: 32px;">
          <h2 style="font-size: 26px; color: white; margin-bottom: 8px;">Welcome back, <%= user.getName() %>! 👋</h2>
          <p style="opacity: 0.9; font-size: 15px;">Track your campus applications, review recruiter notifications, and optimize your resume with AI.</p>
        </div>

        <!-- Quick Stats Cards -->
        <div class="grid grid-3" style="margin-bottom: 32px;">
          <div class="stat-card">
            <div class="stat-icon">📄</div>
            <div>
              <div class="stat-val"><%= myApps.size() %></div>
              <div class="stat-label">Total Applications</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #d1fae5; color: #059669;">✨</div>
            <div>
              <div class="stat-val">88%</div>
              <div class="stat-label">Avg AI ATS Score</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon" style="background: #fef3c7; color: #d97706;">🔔</div>
            <div>
              <div class="stat-val"><%= notifications.size() %></div>
              <div class="stat-label">Notifications</div>
            </div>
          </div>
        </div>

        <!-- Applications Table -->
        <div id="applications" class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            📋 My Applications
          </h3>

          <% if (!myApps.isEmpty()) { %>
            <div style="overflow-x: auto;">
              <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
                <thead>
                  <tr style="border-bottom: 2px solid var(--border-color); color: var(--text-muted);">
                    <th style="padding: 12px;">Job Title</th>
                    <th style="padding: 12px;">Company</th>
                    <th style="padding: 12px;">Applied Date</th>
                    <th style="padding: 12px;">AI Score</th>
                    <th style="padding: 12px;">Status</th>
                  </tr>
                </thead>
                <tbody>
                  <% for(Application app : myApps) { %>
                    <tr style="border-bottom: 1px solid var(--border-color);">
                      <td style="padding: 14px 12px; font-weight: 600;">
                        <a href="job-detail.jsp?id=<%= app.getJobId() %>"><%= app.getJobTitle() %></a>
                      </td>
                      <td style="padding: 14px 12px; color: var(--text-muted);"><%= app.getCompanyName() %></td>
                      <td style="padding: 14px 12px;"><%= app.getAppliedAt().toString().substring(0, 10) %></td>
                      <td style="padding: 14px 12px;">
                        <span class="tag tag-primary"><%= app.getAiScore() %>/100</span>
                      </td>
                      <td style="padding: 14px 12px;">
                        <span class="badge badge-<%= app.getStatus().toLowerCase() %>"><%= app.getStatus() %></span>
                      </td>
                    </tr>
                  <% } %>
                </tbody>
              </table>
            </div>
          <% } else { %>
            <p style="color: var(--text-muted); text-align: center; padding: 24px;">You haven't applied to any job openings yet. <a href="jobs.jsp">Browse Jobs</a></p>
          <% } %>
        </div>

        <!-- Notifications -->
        <div class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            🔔 Recruiter & Campus Updates
          </h3>

          <% if (!notifications.isEmpty()) { %>
            <div style="display: flex; flex-direction: column; gap: 12px;">
              <% for(Notification n : notifications) { %>
                <div style="padding: 12px 16px; background: var(--bg-subtle); border-radius: var(--radius-md); display: flex; justify-content: space-between; align-items: center;">
                  <div>
                    <h5 style="font-size: 15px; margin-bottom: 2px;"><%= n.getTitle() %></h5>
                    <p style="font-size: 13px; color: var(--text-muted);"><%= n.getMessage() %></p>
                  </div>
                  <span style="font-size: 12px; color: var(--text-light);"><%= n.getCreatedAt().toString().substring(0, 10) %></span>
                </div>
              <% } %>
            </div>
          <% } else { %>
            <p style="color: var(--text-muted);">No new notifications.</p>
          <% } %>
        </div>

        <!-- React AI Resume Optimizer -->
        <div id="ai-score">
          <div id="dashboard-ai-resume-root"></div>
        </div>

      </main>
    </div>

  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
  <script type="text/babel" src="js/components.js"></script>
  <script type="text/babel">
    window.addEventListener('load', () => {
      if (window.renderAIResumeAnalyzer) {
        window.renderAIResumeAnalyzer('dashboard-ai-resume-root');
      }
    });
  </script>
</body>
</html>
