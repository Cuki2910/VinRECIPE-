package com.vinrecipe.service;

import com.vinrecipe.dao.UserDAO;
import com.vinrecipe.model.User;

import java.sql.SQLException;

/**
 * Service layer for user authentication and registration.
 * Delegates to UserDAO for all DB operations.
 */
public class UserService {

    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticate a user with username + password.
     * @return User object on success, null on failure.
     */
    public User login(String username, String password) {
        try {
            return userDAO.authenticate(username, password);
        } catch (SQLException e) {
            System.err.println("[UserService] Login error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Register a new NormalStudent account.
     * @return generated userId on success, -1 on failure or if username taken.
     */
    public int register(String username, String password, String email) {
        // Check if username already exists
        try {
            if (userDAO.findByUsername(username) != null) {
                System.err.println("[UserService] Username '" + username + "' already taken");
                return -1;
            }
            // Build a temporary User to validate fields
            com.vinrecipe.model.NormalStudent newUser =
                    new com.vinrecipe.model.NormalStudent(0, username, password, email, 0);
            return userDAO.insert(newUser);
        } catch (SQLException | IllegalArgumentException e) {
            System.err.println("[UserService] Register error: " + e.getMessage());
            return -1;
        }
    }

    /** Find user by username. */
    public User findByUsername(String username) {
        try {
            return userDAO.findByUsername(username);
        } catch (SQLException e) {
            System.err.println("[UserService] FindByUsername error: " + e.getMessage());
            return null;
        }
    }
}
