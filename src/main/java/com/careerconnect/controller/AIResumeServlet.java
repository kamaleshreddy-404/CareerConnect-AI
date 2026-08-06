package com.careerconnect.controller;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/api/ai-resume")
public class AIResumeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String resumeText = request.getParameter("resumeText");
        String targetRole = request.getParameter("targetRole");

        if (targetRole == null || targetRole.trim().isEmpty()) {
            targetRole = "Software Engineer";
        }

        if (resumeText == null || resumeText.trim().isEmpty()) {
            resumeText = "Java, React, SQL, HTML, CSS, JavaScript, Git, REST API, Data Structures, Servlets, Problem Solving";
        }

        Map<String, Object> result = evaluateResume(resumeText, targetRole);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(gson.toJson(result));
        out.flush();
    }

    private Map<String, Object> evaluateResume(String resumeText, String targetRole) {
        String lowerText = resumeText.toLowerCase();

        // Standard Industry ATS Keyword lists per role
        List<String> coreKeywords;
        if (targetRole.toLowerCase().contains("data") || targetRole.toLowerCase().contains("ai")) {
            coreKeywords = Arrays.asList("python", "sql", "machine learning", "pandas", "tableau", "deep learning", "nlp", "statistics", "git", "scikit-learn");
        } else if (targetRole.toLowerCase().contains("cloud") || targetRole.toLowerCase().contains("devops")) {
            coreKeywords = Arrays.asList("aws", "docker", "kubernetes", "linux", "ci/cd", "terraform", "python", "git", "networking", "bash");
        } else {
            coreKeywords = Arrays.asList("java", "react", "sql", "rest api", "git", "data structures", "servlets", "javascript", "oops", "spring");
        }

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String kw : coreKeywords) {
            if (lowerText.contains(kw.toLowerCase())) {
                matched.add(kw);
            } else {
                missing.add(kw);
            }
        }

        int matchPercentage = (int) Math.round(((double) matched.size() / coreKeywords.size()) * 100);
        matchPercentage = Math.max(55, Math.min(98, matchPercentage + 15)); // Boost score realistically

        List<String> suggestions = new ArrayList<>();
        suggestions.add("Add quantified metric achievements (e.g., 'Optimized query latency by 35%').");
        suggestions.add("Ensure technical skills section lists exact keyword strings matching job descriptions.");
        if (!missing.isEmpty()) {
            suggestions.add("Incorporate missing core industry keywords: " + String.join(", ", missing));
        }
        suggestions.add("Use standard ATS-friendly single-column layout without complex table elements.");

        Map<String, Object> res = new HashMap<>();
        res.put("score", matchPercentage);
        res.put("matchedKeywords", matched);
        res.put("missingKeywords", missing);
        res.put("targetRole", targetRole);
        res.put("suggestions", suggestions);
        res.put("atsStatus", matchPercentage >= 80 ? "EXCELLENT_MATCH" : (matchPercentage >= 65 ? "GOOD_MATCH" : "NEEDS_IMPROVEMENT"));

        return res;
    }
}
