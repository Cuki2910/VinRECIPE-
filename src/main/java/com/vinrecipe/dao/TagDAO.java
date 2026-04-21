package com.vinrecipe.dao;

import com.vinrecipe.model.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Tag entities.
 */
public class TagDAO {

    /** Insert a new tag and return its ID. Ignores if name already exists. */
    public int insert(Tag tag) throws SQLException {
        String sql = "INSERT IGNORE INTO tags (name) VALUES (?)";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, tag.getName());
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        // If INSERT IGNORE skipped (duplicate), find existing
        Tag existing = findByName(tag.getName());
        return existing != null ? existing.getTagId() : -1;
    }

    /** Return all tags (used for ComboBox filtering). */
    public List<Tag> findAll() throws SQLException {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM tags ORDER BY name";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tags.add(new Tag(rs.getInt("tag_id"), rs.getString("name")));
            }
        }
        return tags;
    }

    /** Find tag by name. */
    public Tag findByName(String name) throws SQLException {
        String sql = "SELECT * FROM tags WHERE name = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new Tag(rs.getInt("tag_id"), rs.getString("name"));
        }
        return null;
    }

    /** Find all tags for a given recipe. */
    public List<Tag> findByRecipeId(int recipeId) throws SQLException {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t JOIN recipe_tags rt ON t.tag_id = rt.tag_id WHERE rt.recipe_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tags.add(new Tag(rs.getInt("tag_id"), rs.getString("name")));
            }
        }
        return tags;
    }

    /** Link a tag to a recipe in the junction table. */
    public void addTagToRecipe(int recipeId, int tagId) throws SQLException {
        String sql = "INSERT IGNORE INTO recipe_tags (recipe_id, tag_id) VALUES (?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.setInt(2, tagId);
            stmt.executeUpdate();
        }
    }

    /** Remove all tags from a recipe (before re-assigning on update). */
    public void removeTagsFromRecipe(int recipeId) throws SQLException {
        String sql = "DELETE FROM recipe_tags WHERE recipe_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.executeUpdate();
        }
    }
}
