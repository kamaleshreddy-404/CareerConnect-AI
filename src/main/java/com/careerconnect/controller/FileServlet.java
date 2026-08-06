package com.careerconnect.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/download-resume")
public class FileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String fileName = request.getParameter("file");
        if (fileName == null || fileName.isEmpty()) {
            fileName = "sample_resume.pdf";
        }

        // Prevent Directory Traversal
        fileName = new File(fileName).getName();

        String uploadPath = getServletContext().getRealPath("/uploads");
        File file = new File(uploadPath, fileName);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        if (file.exists() && file.isFile()) {
            try (FileInputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            }
        } else {
            // Generate dynamic sample PDF output stream for demo when physical file is not uploaded
            response.setContentType("text/html");
            response.getWriter().println("<div style='font-family:sans-serif; padding:40px; text-align:center;'>"
                + "<h2 style='color:#2563eb;'>CareerConnect AI - Candidate Resume</h2>"
                + "<p>Resume File: <strong>" + fileName + "</strong></p>"
                + "<hr style='max-width:500px; margin:20px auto;'/>"
                + "<p style='color:#64748b;'>This is a sample resume document generated for demonstration purposes.</p>"
                + "<a href='javascript:history.back()' style='display:inline-block; padding:10px 20px; background:#2563eb; color:#fff; text-decoration:none; border-radius:6px; margin-top:20px;'>Go Back</a>"
                + "</div>");
        }
    }
}
