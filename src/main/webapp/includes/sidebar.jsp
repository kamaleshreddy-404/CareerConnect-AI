<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.model.User"%>
<%
    User sidebarUser = (User) session.getAttribute("user");
    String role = (sidebarUser != null) ? sidebarUser.getRole() : "";
%>
<div class="sidebar">
  <div style="text-align: center; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid var(--border-color);">
    <div style="width: 64px; height: 64px; border-radius: 50%; background: var(--primary-light); color: var(--primary); display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: 800; margin: 0 auto 12px;">
      <%= (sidebarUser != null && sidebarUser.getName() != null && !sidebarUser.getName().isEmpty()) ? sidebarUser.getName().substring(0, 1).toUpperCase() : "U" %>
    </div>
    <h4 style="font-size: 16px;"><%= (sidebarUser != null) ? sidebarUser.getName() : "Guest User" %></h4>
    <span class="badge badge-applied" style="margin-top: 4px;"><%= role %></span>
  </div>

  <ul class="sidebar-menu">
    <% if ("ADMIN".equals(role)) { %>
      <li class="sidebar-item"><a href="admin-dashboard.jsp" class="sidebar-link active">📊 Control Panel</a></li>
      <li class="sidebar-item"><a href="admin-dashboard.jsp#users" class="sidebar-link">👥 User Directory</a></li>
      <li class="sidebar-item"><a href="admin-dashboard.jsp#recruiters" class="sidebar-link">🏢 Recruiter Approvals</a></li>
      <li class="sidebar-item"><a href="admin-dashboard.jsp#categories" class="sidebar-link">📁 Job Categories</a></li>
    <% } else if ("RECRUITER".equals(role)) { %>
      <li class="sidebar-item"><a href="recruiter-dashboard.jsp" class="sidebar-link active">💼 Manage Postings</a></li>
      <li class="sidebar-item"><a href="recruiter-dashboard.jsp#applicants" class="sidebar-link">📄 Review Applicants</a></li>
      <li class="sidebar-item"><a href="#" onclick="openModal('postJobModal'); return false;" class="sidebar-link">➕ Post Opening</a></li>
    <% } else { %>
      <li class="sidebar-item"><a href="user-dashboard.jsp" class="sidebar-link active">🚀 Overview</a></li>
      <li class="sidebar-item"><a href="user-dashboard.jsp#applications" class="sidebar-link">📋 My Applications</a></li>
      <li class="sidebar-item"><a href="user-dashboard.jsp#ai-score" class="sidebar-link">✨ AI Resume Analyzer</a></li>
      <li class="sidebar-item"><a href="profile.jsp" class="sidebar-link">👤 Profile Settings</a></li>
    <% } %>
    <li class="sidebar-item" style="margin-top: 16px; border-top: 1px solid var(--border-color); padding-top: 16px;">
      <a href="login.jsp?logout=true" class="sidebar-link" style="color: var(--danger);">🚪 Sign Out</a>
    </li>
  </ul>
</div>
