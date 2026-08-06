<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.careerconnect.model.User, com.careerconnect.model.Recruiter, com.careerconnect.model.Job, com.careerconnect.model.Category, com.careerconnect.dao.UserDAO, com.careerconnect.dao.RecruiterDAO, com.careerconnect.dao.JobDAO, com.careerconnect.dao.CategoryDAO, java.util.List"%>
<%
    User adminUser = (User) session.getAttribute("user");
    if (adminUser == null || !"ADMIN".equals(adminUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    RecruiterDAO recruiterDAO = new RecruiterDAO();
    JobDAO jobDAO = new JobDAO();
    CategoryDAO categoryDAO = new CategoryDAO();

    String action = request.getParameter("action");
    if ("approveRecruiter".equals(action)) {
        int rId = Integer.parseInt(request.getParameter("recruiterId"));
        String status = request.getParameter("status");
        recruiterDAO.updateRecruiterStatus(rId, status);
        response.sendRedirect("admin-dashboard.jsp?msg=recruiter_updated");
        return;
    } else if ("flagJob".equals(action)) {
        int jobId = Integer.parseInt(request.getParameter("jobId"));
        String status = request.getParameter("status");
        jobDAO.updateJobStatus(jobId, status);
        response.sendRedirect("admin-dashboard.jsp?msg=job_flagged");
        return;
    } else if ("addCategory".equals(action)) {
        Category cat = new Category(0, request.getParameter("name"), request.getParameter("icon"), request.getParameter("description"));
        categoryDAO.addCategory(cat);
        response.sendRedirect("admin-dashboard.jsp?msg=category_added");
        return;
    } else if ("deleteCategory".equals(action)) {
        int catId = Integer.parseInt(request.getParameter("categoryId"));
        categoryDAO.deleteCategory(catId);
        response.sendRedirect("admin-dashboard.jsp?msg=category_deleted");
        return;
    }

    List<User> allUsers = userDAO.getAllUsers();
    List<Recruiter> recruiters = recruiterDAO.getAllRecruiters();
    List<Job> allJobs = jobDAO.getAllJobs();
    List<Category> categories = categoryDAO.getAllCategories();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Admin Control Center - CareerConnect AI</title>
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

      <!-- Main Content -->
      <main>
        
        <div style="margin-bottom: 28px;">
          <h1 style="font-size: 28px;">System Administrator Control Center</h1>
          <p style="color: var(--text-muted);">Platform-wide analytics, user management, category controls, and recruiter verification</p>
        </div>

        <!-- React Analytics Mount Point -->
        <div id="admin-analytics-root"></div>

        <!-- Recruiter Approvals -->
        <div id="recruiters" class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            🏢 Recruiter Verification & Approvals
          </h3>

          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
              <thead>
                <tr style="border-bottom: 2px solid var(--border-color); color: var(--text-muted);">
                  <th style="padding: 12px;">Recruiter Name</th>
                  <th style="padding: 12px;">Company</th>
                  <th style="padding: 12px;">Designation</th>
                  <th style="padding: 12px;">Email</th>
                  <th style="padding: 12px;">Verification Status</th>
                  <th style="padding: 12px;">Action</th>
                </tr>
              </thead>
              <tbody>
                <% for(Recruiter r : recruiters) { %>
                  <tr style="border-bottom: 1px solid var(--border-color);">
                    <td style="padding: 14px 12px; font-weight: 600;"><%= r.getUserName() %></td>
                    <td style="padding: 14px 12px;"><%= r.getCompanyName() %></td>
                    <td style="padding: 14px 12px; color: var(--text-muted);"><%= r.getPosition() %></td>
                    <td style="padding: 14px 12px;"><%= r.getUserEmail() %></td>
                    <td style="padding: 14px 12px;">
                      <span class="badge badge-<%= "APPROVED".equals(r.getStatus()) ? "accepted" : ("REJECTED".equals(r.getStatus()) ? "rejected" : "review") %>">
                        <%= r.getStatus() %>
                      </span>
                    </td>
                    <td style="padding: 14px 12px;">
                      <div style="display: flex; gap: 6px;">
                        <form action="admin-dashboard.jsp" method="POST" style="display: inline;">
                          <input type="hidden" name="action" value="approveRecruiter">
                          <input type="hidden" name="recruiterId" value="<%= r.getId() %>">
                          <input type="hidden" name="status" value="APPROVED">
                          <button type="submit" class="btn btn-success btn-sm">Approve</button>
                        </form>
                        <form action="admin-dashboard.jsp" method="POST" style="display: inline;">
                          <input type="hidden" name="action" value="approveRecruiter">
                          <input type="hidden" name="recruiterId" value="<%= r.getId() %>">
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
        </div>

        <!-- Job Postings & Anti-Fraud Control -->
        <div class="card" style="margin-bottom: 32px;">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            💼 Job Postings Directory & Anti-Fraud Control
          </h3>

          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
              <thead>
                <tr style="border-bottom: 2px solid var(--border-color); color: var(--text-muted);">
                  <th style="padding: 12px;">Job Title</th>
                  <th style="padding: 12px;">Company</th>
                  <th style="padding: 12px;">Location</th>
                  <th style="padding: 12px;">Type</th>
                  <th style="padding: 12px;">Status</th>
                  <th style="padding: 12px;">Action</th>
                </tr>
              </thead>
              <tbody>
                <% for(Job j : allJobs) { %>
                  <tr style="border-bottom: 1px solid var(--border-color);">
                    <td style="padding: 14px 12px; font-weight: 600;">
                      <a href="job-detail.jsp?id=<%= j.getId() %>"><%= j.getTitle() %></a>
                    </td>
                    <td style="padding: 14px 12px;"><%= j.getCompanyName() %></td>
                    <td style="padding: 14px 12px; color: var(--text-muted);"><%= j.getLocation() %></td>
                    <td style="padding: 14px 12px;"><span class="tag tag-primary"><%= j.getJobType() %></span></td>
                    <td style="padding: 14px 12px;"><span class="badge badge-<%= "ACTIVE".equals(j.getStatus()) ? "accepted" : "rejected" %>"><%= j.getStatus() %></span></td>
                    <td style="padding: 14px 12px;">
                      <form action="admin-dashboard.jsp" method="POST" style="display: inline;">
                        <input type="hidden" name="action" value="flagJob">
                        <input type="hidden" name="jobId" value="<%= j.getId() %>">
                        <input type="hidden" name="status" value="<%= "ACTIVE".equals(j.getStatus()) ? "FLAGGED" : "ACTIVE" %>">
                        <button type="submit" class="btn btn-outline btn-sm">
                          <%= "ACTIVE".equals(j.getStatus()) ? "🚩 Flag Suspicious" : "✔ Restore Active" %>
                        </button>
                      </form>
                    </td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Category Manager -->
        <div id="categories" class="card" style="margin-bottom: 32px;">
          <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            <h3 style="font-size: 20px;">📁 Job Category Management</h3>
            <button onclick="openModal('addCatModal')" class="btn btn-primary btn-sm">➕ Add Category</button>
          </div>

          <div class="grid grid-3">
            <% for(Category cat : categories) { %>
              <div style="padding: 16px; border: 1px solid var(--border-color); border-radius: var(--radius-md); display: flex; justify-content: space-between; align-items: center;">
                <div>
                  <h4 style="font-size: 15px;"><%= cat.getName() %></h4>
                  <p style="font-size: 12px; color: var(--text-muted);"><%= cat.getDescription() %></p>
                </div>
                <form action="admin-dashboard.jsp" method="POST" onsubmit="return confirm('Delete category?');">
                  <input type="hidden" name="action" value="deleteCategory">
                  <input type="hidden" name="categoryId" value="<%= cat.getId() %>">
                  <button type="submit" class="btn btn-outline btn-sm" style="color: var(--danger); border: none;">🗑️</button>
                </form>
              </div>
            <% } %>
          </div>
        </div>

        <!-- User Directory -->
        <div id="users" class="card">
          <h3 style="font-size: 20px; margin-bottom: 20px; padding-bottom: 12px; border-bottom: 1px solid var(--border-color);">
            👥 User & Candidate Directory
          </h3>

          <div style="overflow-x: auto;">
            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 14px;">
              <thead>
                <tr style="border-bottom: 2px solid var(--border-color); color: var(--text-muted);">
                  <th style="padding: 12px;">ID</th>
                  <th style="padding: 12px;">Full Name</th>
                  <th style="padding: 12px;">Email</th>
                  <th style="padding: 12px;">Role</th>
                  <th style="padding: 12px;">Phone</th>
                </tr>
              </thead>
              <tbody>
                <% for(User u : allUsers) { %>
                  <tr style="border-bottom: 1px solid var(--border-color);">
                    <td style="padding: 14px 12px;"><%= u.getId() %></td>
                    <td style="padding: 14px 12px; font-weight: 600;"><%= u.getName() %></td>
                    <td style="padding: 14px 12px;"><%= u.getEmail() %></td>
                    <td style="padding: 14px 12px;"><span class="badge badge-applied"><%= u.getRole() %></span></td>
                    <td style="padding: 14px 12px; color: var(--text-muted);"><%= u.getPhone() != null ? u.getPhone() : "N/A" %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          </div>
        </div>

      </main>
    </div>

  </div>

  <!-- Add Category Modal -->
  <div id="addCatModal" class="modal-overlay">
    <div class="modal">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
        <h3 style="font-size: 20px;">Add New Job Category</h3>
        <button onclick="closeModal('addCatModal')" style="background: none; border: none; font-size: 24px; cursor: pointer; color: var(--text-muted);">&times;</button>
      </div>

      <form action="admin-dashboard.jsp" method="POST">
        <input type="hidden" name="action" value="addCategory">
        <div class="form-group">
          <label class="form-label">Category Name</label>
          <input type="text" name="name" class="form-control" placeholder="e.g. Blockchain Development" required>
        </div>
        <div class="form-group">
          <label class="form-label">Icon</label>
          <input type="text" name="icon" class="form-control" value="code">
        </div>
        <div class="form-group">
          <label class="form-label">Description</label>
          <textarea name="description" class="form-control" rows="3" required></textarea>
        </div>
        <div style="display: flex; gap: 12px; justify-content: flex-end;">
          <button type="button" onclick="closeModal('addCatModal')" class="btn btn-outline">Cancel</button>
          <button type="submit" class="btn btn-primary">Save Category</button>
        </div>
      </form>
    </div>
  </div>

  <jsp:include page="/includes/footer.jsp" />
  <script src="js/app.js"></script>
  <script type="text/babel" src="js/components.js"></script>
  <script type="text/babel">
    window.addEventListener('load', () => {
      if (window.renderPlatformAnalytics) {
        window.renderPlatformAnalytics('admin-analytics-root');
      }
    });
  </script>
</body>
</html>
