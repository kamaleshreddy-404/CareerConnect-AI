package com.careerconnect.filter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.careerconnect.model.User;

class AuthFilterTest {
    private static final String CONTEXT_PATH = "/CareerConnectAI";

    @Test
    void redirectsUnauthenticatedRequestToLogin() throws Exception {
        RedirectCapture response = new RedirectCapture();
        ChainCapture chain = new ChainCapture();

        new AuthFilter().doFilter(requestFor("/user-dashboard.jsp", null), response.proxy(), chain.proxy());

        assertEquals(CONTEXT_PATH + "/login.jsp?msg=login_required", response.location);
        assertFalse(chain.called);
    }

    @Test
    void allowsUserRoleOnUserDashboard() throws Exception {
        RedirectCapture response = new RedirectCapture();
        ChainCapture chain = new ChainCapture();

        new AuthFilter().doFilter(requestFor("/user-dashboard.jsp", "JOB_SEEKER"), response.proxy(), chain.proxy());

        assertTrue(chain.called);
        assertEquals(null, response.location);
    }

    @Test
    void allowsRecruiterRoleOnRecruiterDashboard() throws Exception {
        RedirectCapture response = new RedirectCapture();
        ChainCapture chain = new ChainCapture();

        new AuthFilter().doFilter(requestFor("/recruiter-dashboard.jsp", "RECRUITER"), response.proxy(), chain.proxy());

        assertTrue(chain.called);
        assertEquals(null, response.location);
    }

    @Test
    void allowsAdminRoleOnAdminDashboard() throws Exception {
        RedirectCapture response = new RedirectCapture();
        ChainCapture chain = new ChainCapture();

        new AuthFilter().doFilter(requestFor("/admin-dashboard.jsp", "ADMIN"), response.proxy(), chain.proxy());

        assertTrue(chain.called);
        assertEquals(null, response.location);
    }

    @Test
    void redirectsUserWithWrongRole() throws Exception {
        RedirectCapture response = new RedirectCapture();
        ChainCapture chain = new ChainCapture();

        new AuthFilter().doFilter(requestFor("/admin-dashboard.jsp", "JOB_SEEKER"), response.proxy(), chain.proxy());

        assertEquals(CONTEXT_PATH + "/login.jsp?msg=unauthorized", response.location);
        assertFalse(chain.called);
    }

    private static HttpServletRequest requestFor(String uri, String role) {
        User user = role == null ? null : userWithRole(role);
        HttpSession session = user == null ? null : proxy(HttpSession.class, (method, args) ->
                "getAttribute".equals(method.getName()) ? user : defaultValue(method.getReturnType()));

        return proxy(HttpServletRequest.class, (method, args) -> {
            if ("getSession".equals(method.getName())) {
                return session;
            }
            if ("getRequestURI".equals(method.getName())) {
                return uri;
            }
            if ("getContextPath".equals(method.getName())) {
                return CONTEXT_PATH;
            }
            return defaultValue(method.getReturnType());
        });
    }

    private static User userWithRole(String role) {
        User user = new User();
        user.setRole(role);
        return user;
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, Invocation invocation) {
        InvocationHandler handler = (object, method, args) -> invocation.call(method, args);
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == char.class) {
            return '\0';
        }
        if (type == byte.class || type == short.class || type == int.class || type == long.class) {
            return 0;
        }
        if (type == float.class) {
            return 0.0f;
        }
        if (type == double.class) {
            return 0.0d;
        }
        return null;
    }

    @FunctionalInterface
    private interface Invocation {
        Object call(java.lang.reflect.Method method, Object[] args);
    }

    private static class RedirectCapture {
        private String location;

        private HttpServletResponse proxy() {
            return AuthFilterTest.proxy(HttpServletResponse.class, (method, args) -> {
                if ("sendRedirect".equals(method.getName())) {
                    location = (String) args[0];
                }
                return defaultValue(method.getReturnType());
            });
        }
    }

    private static class ChainCapture {
        private boolean called;

        private FilterChain proxy() {
            return AuthFilterTest.proxy(FilterChain.class, (method, args) -> {
                if ("doFilter".equals(method.getName())) {
                    called = true;
                }
                return defaultValue(method.getReturnType());
            });
        }
    }
}
