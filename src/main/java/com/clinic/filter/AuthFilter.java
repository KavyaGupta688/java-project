package com.clinic.filter;

import com.clinic.model.Role;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Protects /patient/* and /doctor/* routes: requires a logged-in
 * session, and enforces that patients cannot hit doctor-only
 * servlets and vice versa.
 */
@WebFilter(urlPatterns = {"/patient/*", "/doctor/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Please+log+in");
            return;
        }

        Role role = (Role) session.getAttribute("role");
        if (path.startsWith("/patient/") && role != Role.PATIENT) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Patient+access+only");
            return;
        }
        if (path.startsWith("/doctor/") && role != Role.DOCTOR) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Doctor+access+only");
            return;
        }

        chain.doFilter(req, res);
    }
}
