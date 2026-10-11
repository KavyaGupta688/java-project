package com.clinic.dao;

import com.clinic.model.Role;
import com.clinic.model.User;

import java.sql.SQLException;
import java.util.Optional;

public interface IUserDAO {

    Optional<User> findByEmail(String email) throws SQLException;

    Optional<User> findById(int userId) throws SQLException;

    boolean emailExists(String email) throws SQLException;

    int insertUser(String name, String email, String passwordHash, Role role) throws SQLException;
}
