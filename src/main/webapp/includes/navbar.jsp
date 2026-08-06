<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.model.User"%>
<%
    User currentUser = (User) session.getAttribute("user");
    String logoutParam = request.getParameter("logout");
    if ("true".equals(logoutParam)) {
        session.invalidate();
        currentUser = null;
    }
%>
<nav class="navbar">
  <div class="container nav-container">
    <a href="index.jsp" class="logo">
      <svg width="30" height="30" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
        <path d="M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
        <rect x="2" y="6" width="20" height="14" rx="2"/>
      </svg>
      CareerConnect <span class="logo-badge">AI</span>
    </a>

    <ul class="nav-links">
      <li><a href="index.jsp" class="nav-link">Home</a></li>
      <li><a href="jobs.jsp" class="nav-link">Browse Jobs</a></li>
      <% if (currentUser != null) { %>
        <% if ("ADMIN".equals(currentUser.getRole())) { %>
          <li><a href="admin-dashboard.jsp" class="nav-link">Admin Portal</a></li>
        <% } else if ("RECRUITER".equals(currentUser.getRole())) { %>
          <li><a href="recruiter-dashboard.jsp" class="nav-link">Recruiter Dashboard</a></li>
        <% } else { %>
          <li><a href="user-dashboard.jsp" class="nav-link">Candidate Dashboard</a></li>
        <% } %>
      <% } %>
    </ul>

    <div class="nav-actions">
      <button class="theme-toggle" onclick="toggleTheme()" title="Toggle Dark/Light Mode">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>
      </button>

      <% if (currentUser == null) { %>
        <a href="login.jsp" class="btn btn-outline">Log In</a>
        <a href="register.jsp" class="btn btn-primary">Register</a>
      <% } else { %>
        <div style="display: flex; align-items: center; gap: 12px;">
          <a href="profile.jsp" style="display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 14px;">
            <div style="width: 36px; height: 36px; border-radius: 50%; background: var(--primary-light); color: var(--primary); display: flex; align-items: center; justify-content: center; font-weight: 700;">
              <%= (currentUser.getName() != null && !currentUser.getName().isEmpty()) ? currentUser.getName().substring(0, 1).toUpperCase() : "U" %>
            </div>
            <%= currentUser.getName() %>
          </a>
          <a href="login.jsp?logout=true" class="btn btn-outline btn-sm">Logout</a>
        </div>
      <% } %>
    </div>
  </div>
</nav>
