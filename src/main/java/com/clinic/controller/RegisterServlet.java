package com.clinic.controller;

import com.clinic.dao.IDoctorDAO;
import com.clinic.dao.IPatientDAO;
import com.clinic.dao.IUserDAO;
import com.clinic.dao.impl.DoctorDAOImpl;
import com.clinic.dao.impl.PatientDAOImpl;
import com.clinic.dao.impl.UserDAOImpl;
import com.clinic.model.Role;
import com.clinic.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final IUserDAO userDAO = new UserDAOImpl();
    private final IPatientDAO patientDAO = new PatientDAOImpl();
    private final IDoctorDAO doctorDAO = new DoctorDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String roleParam = req.getParameter("role");

        if (name == null || name.isBlank() || email == null || email.isBlank()
                || password == null || password.isBlank() || roleParam == null) {
            req.setAttribute("error", "All fields are required.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                req.setAttribute("error", "An account with that email already exists.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }

            Role role = Role.valueOf(roleParam.toUpperCase());
            String passwordHash = PasswordUtil.hash(password);
            int userId = userDAO.insertUser(name, email, passwordHash, role);

            if (role == Role.PATIENT) {
                String bloodGroup = req.getParameter("bloodGroup");
                String emergencyContact = req.getParameter("emergencyContact");
                patientDAO.insertPatientProfile(userId, bloodGroup, emergencyContact);
            } else {
                String specialization = req.getParameter("specialization");
                double fee = 0.0;
                String feeParam = req.getParameter("consultationFee");
                if (feeParam != null && !feeParam.isBlank()) {
                    fee = Double.parseDouble(feeParam);
                }
                doctorDAO.insertDoctorProfile(userId, specialization, fee);
            }

            resp.sendRedirect(req.getContextPath() + "/login.jsp?registered=1");
        } catch (SQLException e) {
            throw new ServletException("Database error during registration", e);
        }
    }
}
