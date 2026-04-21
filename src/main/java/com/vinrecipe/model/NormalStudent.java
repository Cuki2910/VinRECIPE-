package com.vinrecipe.model;

/**
 * Normal student user — can view shared list in their room.
 * Demonstrates Inheritance from User.
 */
public class NormalStudent extends User {

    private int roomId; // 0 means not assigned to any room

    public NormalStudent() {
        super();
        setRole("NORMAL_STUDENT");
    }

    public NormalStudent(int userId, String username, String password, String email, int roomId) {
        super(userId, username, password, email, "NORMAL_STUDENT");
        this.roomId = roomId;
    }

    /** NormalStudents have permission level 1. — Polymorphism */
    @Override
    public int getPermissionLevel() {
        return 1;
    }

    /** NormalStudent-specific: view the shared shopping list in their room. */
    public ShoppingList viewSharedList() {
        System.out.println("[NormalStudent] " + getUsername() + " viewing shared list for room " + roomId);
        return new ShoppingList();
    }

    // ---- Getters / Setters ----
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) {
        if (roomId < 0) throw new IllegalArgumentException("roomId must be non-negative");
        this.roomId = roomId;
    }
}
