package com.vinrecipe.dao;

import com.vinrecipe.model.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Ingredient entities.
 */
public class IngredientDAO {

    /** Insert a list of ingredients for a given recipe. */
    public void insertForRecipe(int recipeId, List<Ingredient> ingredients) throws SQLException {
        String sql = "INSERT INTO ingredients (recipe_id, name, quantity, unit, price_estimate) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Ingredient ing : ingredients) {
                stmt.setInt(1, recipeId);
                stmt.setString(2, ing.getName());
                stmt.setDouble(3, ing.getQuantity());
                stmt.setString(4, ing.getUnit());
                stmt.setDouble(5, ing.getPriceEstimate());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /** Find all ingredients for a recipe. */
    public List<Ingredient> findByRecipeId(int recipeId) throws SQLException {
        List<Ingredient> list = new ArrayList<>();
        String sql = "SELECT * FROM ingredients WHERE recipe_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ingredient ing = new Ingredient();
                ing.setIngredientId(rs.getInt("ingredient_id"));
                ing.setName(rs.getString("name"));
                ing.setQuantity(rs.getDouble("quantity"));
                ing.setUnit(rs.getString("unit"));
                ing.setPriceEstimate(rs.getDouble("price_estimate"));
                list.add(ing);
            }
        }
        return list;
    }

    /** Delete all ingredients for a recipe (used before re-inserting on update). */
    public void deleteByRecipeId(int recipeId) throws SQLException {
        String sql = "DELETE FROM ingredients WHERE recipe_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.executeUpdate();
        }
    }
}
