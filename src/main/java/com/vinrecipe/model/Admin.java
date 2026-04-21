package com.vinrecipe.model;

/**
 * Admin user — has highest permission level.
 * Demonstrates Inheritance from User.
 */
public class Admin extends User {

    public Admin() {
        super();
        setRole("ADMIN");
    }

    public Admin(int userId, String username, String password, String email) {
        super(userId, username, password, email, "ADMIN");
    }

    /** Admins have the highest permission level (3). — Polymorphism */
    @Override
    public int getPermissionLevel() {
        return 3;
    }

    /** Admin-specific: manage all users in the system. */
    public void manageUsers() {
        System.out.println("[Admin] " + getUsername() + " is managing users.");
    }

    /** Admin-specific: delete any recipe regardless of author. */
    public void deleteAnyRecipe(int recipeId) {
        System.out.println("[Admin] " + getUsername() + " deleted recipe id=" + recipeId);
    }
}
