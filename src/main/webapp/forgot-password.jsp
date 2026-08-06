<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Reset Password - CareerConnect AI</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <jsp:include page="/includes/navbar.jsp" />

  <div class="container" style="max-width: 440px; margin: 60px auto;">
    <div class="card" style="padding: 36px;">
      <div style="text-align: center; margin-bottom: 24px;">
        <h2 style="font-size: 24px;">Reset Password</h2>
        <p style="color: var(--text-muted); font-size: 14px; margin-top: 4px;">Enter registered email to reset account access</p>
      </div>

      <% String error = (String) request.getAttribute("error"); %>
      <% if (error != null) { %>
        <div style="background: #fee2e2; color: #dc2626; padding: 12px; border-radius: 8px; font-size: 14px; margin-bottom: 20px;">
          <%= error %>
        </div>
      <% } %>

      <form action="${pageContext.request.contextPath}/auth/reset-password" method="POST">
        <div class="form-group">
          <label class="form-label">Registered Email</label>
          <input type="email" name="email" class="form-control" placeholder="alex.student@college.edu" required>
        </div>
        <div class="form-group">
          <label class="form-label">New Password</label>
          <input type="password" name="newPassword" class="form-control" placeholder="Enter new password" required>
        </div>
        <button type="submit" class="btn btn-primary btn-lg" style="width: 100%;">Reset Password</button>
      </form>

      <div style="text-align: center; margin-top: 20px; font-size: 14px;">
        <a href="${pageContext.request.contextPath}/login.jsp">← Back to Login</a>
      </div>
    </div>
  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
