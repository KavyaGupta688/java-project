package com.clinic.controller;

import com.clinic.dao.IDoctorDAO;
import com.clinic.dao.IPatientDAO;
import com.clinic.dao.IUserDAO;
import com.clinic.dao.impl.DoctorDAOImpl;
import com.clinic.dao.impl.PatientDAOImpl;
import com.clinic.dao.impl.UserDAOImpl;
import com.clinic.model.Role;
import com.clinic.model.User;
import com.clinic.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final IUserDAO userDAO = new UserDAOImpl();
    private final IPatientDAO patientDAO = new PatientDAOImpl();
    private final IDoctorDAO doctorDAO = new DoctorDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            Optional<User> userOpt = userDAO.findByEmail(email);
            if (userOpt.isEmpty() || !PasswordUtil.matches(password, userOpt.get().getPasswordHash())) {
                req.setAttribute("error", "Invalid email or password.");
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
                return;
            }

            User user = userOpt.get();
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("name", user.getName());
            session.setAttribute("role", user.getRole());

            if (user.getRole() == Role.PATIENT) {
                patientDAO.findByUserId(user.getUserId())
                        .ifPresent(p -> session.setAttribute("patientId", p.getPatientId()));
                resp.sendRedirect(req.getContextPath() + "/patient/dashboard");
            } else {
                doctorDAO.findByUserId(user.getUserId())
                        .ifPresent(d -> session.setAttribute("doctorId", d.getDoctorId()));
                resp.sendRedirect(req.getContextPath() + "/doctor/dashboard");
            }
        } catch (SQLException e) {
            throw new ServletException("Database error during login", e);
        }
    }
}
