package com.careerconnect.filter;

import com.careerconnect.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter({"/user-dashboard.jsp", "/recruiter-dashboard.jsp", "/admin-dashboard.jsp", "/profile.jsp"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String uri = request.getRequestURI();

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=login_required");
            return;
        }

        // Role-Based Authorization enforcement
        if (uri.contains("admin-dashboard.jsp") && !"ADMIN".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=unauthorized");
            return;
        }

        if (uri.contains("recruiter-dashboard.jsp") && !"RECRUITER".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=unauthorized");
            return;
        }

        if (uri.contains("user-dashboard.jsp") && !"JOB_SEEKER".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?msg=unauthorized");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {}
}
