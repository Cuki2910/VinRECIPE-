package com.vinrecipe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dorm room grouping students together.
 * Demonstrates Composition: Room HAS-A RoomLeader and List of NormalStudents.
 */
public class Room {

    private int roomId;
    private String roomName;
    private RoomLeader leader;
    private List<NormalStudent> members; // Composition

    public Room() {
        this.members = new ArrayList<>();
    }

    public Room(int roomId, String roomName, RoomLeader leader) {
        if (roomName == null || roomName.isBlank())
            throw new IllegalArgumentException("Room name must not be blank");
        this.roomId = roomId;
        this.roomName = roomName.trim();
        this.leader = leader;
        this.members = new ArrayList<>();
    }

    /** Add a student to this room. */
    public void addMember(NormalStudent student) {
        if (student == null) throw new IllegalArgumentException("Student must not be null");
        if (!members.contains(student)) {
            members.add(student);
            student.setRoomId(this.roomId);
        }
    }

    /** Remove a student from this room. */
    public void removeMember(NormalStudent student) {
        members.remove(student);
    }

    // ---- Getters / Setters ----
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) {
        if (roomName == null || roomName.isBlank())
            throw new IllegalArgumentException("Room name must not be blank");
        this.roomName = roomName.trim();
    }

    public RoomLeader getLeader() { return leader; }
    public void setLeader(RoomLeader leader) { this.leader = leader; }

    public List<NormalStudent> getMembers() { return members; }
    public void setMembers(List<NormalStudent> members) { this.members = members; }

    @Override
    public String toString() {
        return "Room{id=" + roomId + ", name='" + roomName + "', members=" + members.size() + "}";
    }
}
