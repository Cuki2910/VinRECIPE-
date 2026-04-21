package com.vinrecipe.dao;

import com.vinrecipe.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entities.
 * All DB operations use PreparedStatement to prevent SQL injection.
 */
public class UserDAO {

    /** Insert a new user and return the generated user_id. */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, role) VALUES (?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    /** Find a user by username. Returns null if not found. */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    /** Authenticate: returns User if credentials match, null otherwise. */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    /** Return all users. */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) users.add(mapRow(rs));
        }
        return users;
    }

    /** Find user by ID. */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapRow(rs);
        }
        return null;
    }

    /**
     * Find all users belonging to a specific room.
     * Used by MyRoomController to list room members for the Room Leader.
     */
    public List<User> findByRoomId(int roomId) throws SQLException {
        List<User> members = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE room_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) members.add(mapRow(rs));
        }
        return members;
    }

    /**
     * Map a ResultSet row to the appropriate User subclass.
     * Demonstrates Polymorphism — returns different subclasses based on role.
     */
    private User mapRow(ResultSet rs) throws SQLException {
        int userId     = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email    = rs.getString("email");
        String role     = rs.getString("role");
        int roomId      = rs.getInt("room_id");

        switch (role) {
            case "ADMIN":
                return new Admin(userId, username, password, email);
            case "ROOM_LEADER":
                return new RoomLeader(userId, username, password, email, roomId);
            default:
                return new NormalStudent(userId, username, password, email, roomId);
        }
    }
}
