package com.vinrecipe.model;

/**
 * Abstract base class representing a system user.
 * Demonstrates OOP concepts: Abstraction, Encapsulation.
 * All subclasses (Admin, NormalStudent, RoomLeader) inherit from this class.
 */
public abstract class User {

    // All fields private — Encapsulation
    private int userId;
    private String username;
    private String password;
    private String email;
    private String role; // "ADMIN", "NORMAL_STUDENT", "ROOM_LEADER"

    // ---- Constructors ----
    public User() {}

    public User(int userId, String username, String password, String email, String role) {
        this.userId = userId;
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.role = role;
    }

    // ---- Abstract method — Abstraction ----
    /**
     * Returns the permission level of this user type.
     * Must be overridden by each subclass — Polymorphism.
     */
    public abstract int getPermissionLevel();

    /**
     * Simulates login logic: validate credentials.
     * Subclasses may override for additional checks — Polymorphism.
     */
    public boolean login(String inputUsername, String inputPassword) {
        return this.username != null
                && this.username.equals(inputUsername)
                && this.password != null
                && this.password.equals(inputPassword);
    }

    /** Clears session data on logout. */
    public void logout() {
        System.out.println(username + " has logged out.");
    }

    // ---- Getters / Setters with validation — Encapsulation ----
    public int getUserId() { return userId; }
    public void setUserId(int userId) {
        if (userId < 0) throw new IllegalArgumentException("userId must be non-negative");
        this.userId = userId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username must not be blank");
        this.username = username.trim();
    }

    public String getPassword() { return password; }
    public void setPassword(String password) {
        if (password == null || password.length() < 4)
            throw new IllegalArgumentException("Password must be at least 4 characters");
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("Email is invalid");
        this.email = email.trim();
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "', role='" + role + "'}";
    }
}
