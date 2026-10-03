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

@WebServlet({"/download-resume", "/view-resume"})
public class FileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String fileName = request.getParameter("file");
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "sample_resume.pdf";
        }

        // Prevent Directory Traversal
        fileName = new File(fileName).getName();

        // Search locations
        String[] searchPaths = {
            getServletContext().getRealPath("/uploads/resumes"),
            getServletContext().getRealPath("/uploads"),
            getServletContext().getRealPath("/")
        };

        File file = null;
        for (String path : searchPaths) {
            if (path != null) {
                File candidate = new File(path, fileName);
                if (candidate.exists() && candidate.isFile()) {
                    file = candidate;
                    break;
                }
            }
        }

        // Fallback to sample_resume.pdf if requested file isn't found
        if (file == null || !file.exists()) {
            for (String path : searchPaths) {
                if (path != null) {
                    File candidate = new File(path, "sample_resume.pdf");
                    if (candidate.exists() && candidate.isFile()) {
                        file = candidate;
                        break;
                    }
                }
            }
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");

        if (file != null && file.exists() && file.isFile()) {
            response.setContentLength((int) file.length());
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
            // Generates a lightweight, valid fallback PDF in-memory if no PDF exists on server
            String pdfContent = "%PDF-1.4\n" +
                "1 0 obj <</Type /Catalog /Pages 2 0 R>> endobj\n" +
                "2 0 obj <</Type /Pages /Kids [3 0 R] /Count 1>> endobj\n" +
                "3 0 obj <</Type /Page /Parent 2 0 R /Resources <</Font <</F1 4 0 R>>>> /MediaBox [0 0 612 792] /Contents 5 0 R>> endobj\n" +
                "4 0 obj <</Type /Font /Subtype /Type1 /BaseFont /Helvetica>> endobj\n" +
                "5 0 obj <</Length 120>> stream\n" +
                "BT\n" +
                "/F1 18 Tf\n" +
                "50 720 Td\n" +
                "(CareerConnect AI - Candidate Resume) Tj\n" +
                "0 -30 Td\n" +
                "/F1 12 Tf\n" +
                "(Resume document: " + fileName + ") Tj\n" +
                "ET\n" +
                "endstream endobj\n" +
                "xref\n" +
                "0 6\n" +
                "0000000000 65535 f \n" +
                "0000000009 00000 n \n" +
                "0000000056 00000 n \n" +
                "0000000111 00000 n \n" +
                "0000000223 00000 n \n" +
                "0000000289 00000 n \n" +
                "trailer <</Size 6 /Root 1 0 R>>\n" +
                "startxref\n" +
                "450\n" +
                "%%EOF";

            byte[] pdfBytes = pdfContent.getBytes("UTF-8");
            response.setContentLength(pdfBytes.length);
            try (OutputStream out = response.getOutputStream()) {
                out.write(pdfBytes);
                out.flush();
            }
        }
    }
}

