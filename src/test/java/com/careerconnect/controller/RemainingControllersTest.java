package com.careerconnect.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.careerconnect.model.User;

class RemainingControllersTest {
    private static final String CONTEXT_PATH = "/CareerConnectAI";

    @Test
    void adminStatsReturnsJson() throws Exception {
        RequestCapture request = new RequestCapture("/stats", null);
        ResponseCapture response = new ResponseCapture();

        new AdminServlet().doGet(request.proxy(), response.proxy());

        assertEquals("application/json", response.contentType);
        assertTrue(response.body().contains("totalUsers"));
    }

    @Test
    void adminPostRejectsNonAdmin() throws Exception {
        RequestCapture request = new RequestCapture("/add-category", sessionFor("JOB_SEEKER"));
        ResponseCapture response = new ResponseCapture();

        new AdminServlet().doPost(request.proxy(), response.proxy());

        assertEquals(CONTEXT_PATH + "/login.jsp", response.redirect);
    }

    @Test
    void recruiterPostRejectsUnauthenticatedRequest() throws Exception {
        RequestCapture request = new RequestCapture("/update-application", null);
        ResponseCapture response = new ResponseCapture();

        new RecruiterServlet().doPost(request.proxy(), response.proxy());

        assertEquals(CONTEXT_PATH + "/login.jsp", response.redirect);
    }

    @Test
    void jobBrowseForwardsJobsAndCategories() throws Exception {
        RequestCapture request = new RequestCapture("/browse", null);
        request.parameters.put("keyword", "Java");
        request.parameters.put("category", "not-a-number");
        ResponseCapture response = new ResponseCapture();

        new JobServlet().doGet(request.proxy(), response.proxy());

        assertEquals("/jobs.jsp", request.forwardedPath);
        assertTrue(request.attributes.containsKey("jobs"));
        assertTrue(request.attributes.containsKey("categories"));
        assertEquals(0, request.attributes.get("paramCategory"));
    }

    @Test
    void jobDetailWithoutIdRedirectsToBrowse() throws Exception {
        RequestCapture request = new RequestCapture("/detail", null);
        ResponseCapture response = new ResponseCapture();

        new JobServlet().doGet(request.proxy(), response.proxy());

        assertEquals(CONTEXT_PATH + "/jobs/browse", response.redirect);
    }

    @Test
    void jobCreateRejectsUnauthenticatedRequest() throws Exception {
        RequestCapture request = new RequestCapture("/create", null);
        ResponseCapture response = new ResponseCapture();

        new JobServlet().doPost(request.proxy(), response.proxy());

        assertEquals(CONTEXT_PATH + "/login.jsp", response.redirect);
    }

    @Test
    void seekerApplyRejectsUnauthenticatedRequest() throws Exception {
        RequestCapture request = new RequestCapture("/apply", null);
        ResponseCapture response = new ResponseCapture();

        new JobSeekerServlet().doPost(request.proxy(), response.proxy());

        assertEquals(CONTEXT_PATH + "/login.jsp", response.redirect);
    }

    @Test
    void aiResumeReturnsMatchedKeywordsAndRole() throws Exception {
        RequestCapture request = new RequestCapture(null, null);
        request.parameters.put("resumeText", "Java React SQL Git");
        request.parameters.put("targetRole", "Software Engineer");
        ResponseCapture response = new ResponseCapture();

        new AIResumeServlet().doPost(request.proxy(), response.proxy());

        assertEquals("application/json", response.contentType);
        assertTrue(response.body().contains("matchedKeywords"));
        assertTrue(response.body().contains("Software Engineer"));
    }

    @Test
    void fileDownloadSanitizesRequestedFileName() throws Exception {
        RequestCapture request = new RequestCapture(null, null);
        request.parameters.put("file", "..\\private\\resume.pdf");
        ResponseCapture response = new ResponseCapture();
        ServletContext context = proxy(ServletContext.class, (method, args) -> "/missing-uploads");
        FileServlet servlet = new FileServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };

        servlet.doGet(request.proxy(), response.proxy());

        assertEquals("application/pdf", response.contentType);
        assertTrue(response.contentDisposition.contains("resume.pdf"));
        assertFalse(response.contentDisposition.contains("private"));
        assertTrue(response.body().contains("resume.pdf"));
    }

    private static HttpSession sessionFor(String role) {
        User user = new User();
        user.setRole(role);
        return proxy(HttpSession.class, (method, args) ->
                "getAttribute".equals(method.getName()) ? user : defaultValue(method.getReturnType()));
    }

    private static <T> T proxy(Class<T> type, Invocation invocation) {
        @SuppressWarnings("unchecked")
        T value = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                (object, method, args) -> invocation.call(method, args));
        return value;
    }

    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive()) {
            return null;
        }
        if (type == boolean.class) return false;
        if (type == char.class) return '\0';
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0d;
        if (type == byte.class || type == short.class || type == int.class || type == long.class) return 0;
        return null;
    }

    @FunctionalInterface
    private interface Invocation {
        Object call(java.lang.reflect.Method method, Object[] args);
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
            return RemainingControllersTest.proxy(HttpServletRequest.class, (method, args) -> switch (method.getName()) {
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
                    yield RemainingControllersTest.proxy(RequestDispatcher.class, (dispatchMethod, dispatchArgs) -> null);
                }
                default -> defaultValue(method.getReturnType());
            });
        }
    }

    private static class ResponseCapture {
        private String redirect;
        private String contentType;
        private String contentTypeBeforeFallback;
        private String contentDisposition;
        private final StringWriter writer = new StringWriter();

        private HttpServletResponse proxy() {
            return RemainingControllersTest.proxy(HttpServletResponse.class, (method, args) -> {
                switch (method.getName()) {
                    case "sendRedirect" -> redirect = (String) args[0];
                    case "setContentType" -> {
                        if (contentType == null) contentTypeBeforeFallback = (String) args[0];
                        contentType = (String) args[0];
                    }
                    case "setHeader" -> {
                        if ("Content-Disposition".equals(args[0])) contentDisposition = (String) args[1];
                    }
                    case "getWriter" -> { return new PrintWriter(writer); }
                    case "getOutputStream" -> {
                        return new javax.servlet.ServletOutputStream() {
                            @Override
                            public boolean isReady() { return true; }
                            @Override
                            public void setWriteListener(javax.servlet.WriteListener writeListener) {}
                            @Override
                            public void write(int b) { writer.write(b); }
                            @Override
                            public void write(byte[] b, int off, int len) {
                                writer.write(new String(b, off, len, java.nio.charset.StandardCharsets.UTF_8));
                            }
                        };
                    }
                    default -> { }
                }
                return defaultValue(method.getReturnType());
            });
        }

        private String body() {
            return writer.toString();
        }
    }
}
