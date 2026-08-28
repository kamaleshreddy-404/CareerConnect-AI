package com.careerconnect.controller;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AuthServletTest {
    private static final String CONTEXT_PATH = "/CareerConnectAI";

    @Test
    void logoutInvalidatesSessionAndRedirects() throws Exception {
        SessionCapture session = new SessionCapture();
        RequestCapture request = new RequestCapture("/logout", session.proxy());
        ResponseCapture response = new ResponseCapture();

        new AuthServlet().doGet(request.proxy(), response.proxy());

        assertTrue(session.invalidated);
        assertEquals(CONTEXT_PATH + "/login.jsp?msg=logged_out", response.redirect);
    }

    @Test
    void forgotPasswordForwardsToForgotPasswordPage() throws Exception {
        RequestCapture request = new RequestCapture("/forgot-password", null);
        ResponseCapture response = new ResponseCapture();

        new AuthServlet().doGet(request.proxy(), response.proxy());

        assertEquals("/forgot-password.jsp", request.forwardedPath);
        assertFalse(response.redirect != null);
    }

    @Test
    void invalidLoginForwardsWithValidationError() throws Exception {
        RequestCapture request = new RequestCapture("/login", null);
        request.parameters.put("email", " ");
        request.parameters.put("password", "password123");
        ResponseCapture response = new ResponseCapture();

        new AuthServlet().doPost(request.proxy(), response.proxy());

        assertEquals("Please provide both email and password.", request.attributes.get("error"));
        assertEquals("/login.jsp", request.forwardedPath);
    }

    @Test
    void validAdminLoginRedirectsToAdminDashboard() throws Exception {
        SessionCapture session = new SessionCapture();
        RequestCapture request = new RequestCapture("/login", session.proxy());
        request.parameters.put("email", "admin@careerconnect.ai");
        request.parameters.put("password", "password123");
        ResponseCapture response = new ResponseCapture();

        new AuthServlet().doPost(request.proxy(), response.proxy());

        assertEquals(CONTEXT_PATH + "/admin-dashboard.jsp", response.redirect);
        assertEquals("ADMIN", session.attributes.get("userRole"));
        assertEquals(1, session.attributes.get("userId"));
    }

    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        @SuppressWarnings("unchecked")
        T value = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
        return value;
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

    private static class RequestCapture {
        private final String pathInfo;
        private final HttpSession session;
        private final Map<String, String> parameters = new HashMap<>();
        private final Map<String, Object> attributes = new HashMap<>();
        private String forwardedPath;

        private RequestCapture(String pathInfo, HttpSession session) {
            this.pathInfo = pathInfo;
            this.session = session;
        }

        private HttpServletRequest proxy() {
            return AuthServletTest.proxy(HttpServletRequest.class, (object, method, args) -> {
                return switch (method.getName()) {
                    case "getPathInfo" -> pathInfo;
                    case "getContextPath" -> CONTEXT_PATH;
                    case "getSession" -> session;
                    case "getParameter" -> parameters.get((String) args[0]);
                    case "setAttribute" -> {
                        attributes.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "getRequestDispatcher" -> {
                        forwardedPath = (String) args[0];
                        yield AuthServletTest.proxy(RequestDispatcher.class, (dispatcher, dispatchMethod, dispatchArgs) -> null);
                    }
                    default -> defaultValue(method.getReturnType());
                };
            });
        }
    }

    private static class ResponseCapture {
        private String redirect;

        private HttpServletResponse proxy() {
            return AuthServletTest.proxy(HttpServletResponse.class, (object, method, args) -> {
                if ("sendRedirect".equals(method.getName())) {
                    redirect = (String) args[0];
                }
                return defaultValue(method.getReturnType());
            });
        }
    }

    private static class SessionCapture {
        private final Map<String, Object> attributes = new HashMap<>();
        private boolean invalidated;

        private HttpSession proxy() {
            return AuthServletTest.proxy(HttpSession.class, (object, method, args) -> {
                return switch (method.getName()) {
                    case "setAttribute" -> {
                        attributes.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "invalidate" -> {
                        invalidated = true;
                        yield null;
                    }
                    default -> defaultValue(method.getReturnType());
                };
            });
        }
    }
}
