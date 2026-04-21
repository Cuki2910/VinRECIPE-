package com.vinrecipe.dao;

import com.vinrecipe.model.Ingredient;
import com.vinrecipe.model.Recipe;
import com.vinrecipe.model.Tag;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Recipe entities. Handles all recipe CRUD operations.
 * Ingredients and tags are loaded via IngredientDAO / TagDAO.
 */
public class RecipeDAO {

    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private final TagDAO tagDAO               = new TagDAO();

    /** Insert a new recipe and return its generated recipe_id. */
    public int insert(Recipe recipe) throws SQLException {
        String sql = "INSERT INTO recipes (title, description, instructions, prep_time, cook_time, rating, servings, image_url, author_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, recipe.getTitle());
            stmt.setString(2, recipe.getDescription());
            stmt.setString(3, recipe.getInstructions());
            stmt.setInt(4, recipe.getPrepTime());
            stmt.setInt(5, recipe.getCookTime());
            stmt.setDouble(6, recipe.getRating());
            stmt.setInt(7, recipe.getServings());
            stmt.setString(8, recipe.getImageUrl());
            if (recipe.getAuthor() != null) stmt.setInt(9, recipe.getAuthor().getUserId());
            else stmt.setNull(9, Types.INTEGER);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    /** Find a recipe by ID, loading its ingredients and tags. */
    public Recipe findById(int recipeId) throws SQLException {
        String sql = "SELECT * FROM recipes WHERE recipe_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Recipe r = mapRow(rs);
                r.setIngredients(ingredientDAO.findByRecipeId(recipeId));
                r.setTags(tagDAO.findByRecipeId(recipeId));
                return r;
            }
        }
        return null;
    }

    /** Return all recipes with their ingredients and tags. */
    public List<Recipe> findAll() throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes ORDER BY created_at DESC";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recipe r = mapRow(rs);
                r.setIngredients(ingredientDAO.findByRecipeId(r.getRecipeId()));
                r.setTags(tagDAO.findByRecipeId(r.getRecipeId()));
                recipes.add(r);
            }
        }
        return recipes;
    }

    /** Search recipes by title (partial match, case-insensitive). */
    public List<Recipe> searchByTitle(String keyword) throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT * FROM recipes WHERE LOWER(title) LIKE ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recipe r = mapRow(rs);
                r.setIngredients(ingredientDAO.findByRecipeId(r.getRecipeId()));
                r.setTags(tagDAO.findByRecipeId(r.getRecipeId()));
                recipes.add(r);
            }
        }
        return recipes;
    }

    /** Search recipes by tag name. */
    public List<Recipe> searchByTag(String tagName) throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT r.* FROM recipes r "
                   + "JOIN recipe_tags rt ON r.recipe_id = rt.recipe_id "
                   + "JOIN tags t ON rt.tag_id = t.tag_id "
                   + "WHERE LOWER(t.name) = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tagName.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Recipe r = mapRow(rs);
                r.setIngredients(ingredientDAO.findByRecipeId(r.getRecipeId()));
                r.setTags(tagDAO.findByRecipeId(r.getRecipeId()));
                recipes.add(r);
            }
        }
        return recipes;
    }

    /** Update an existing recipe. */
    public void update(Recipe recipe) throws SQLException {
        String sql = "UPDATE recipes SET title=?, description=?, instructions=?, prep_time=?, "
                   + "cook_time=?, rating=?, servings=?, image_url=? WHERE recipe_id=?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, recipe.getTitle());
            stmt.setString(2, recipe.getDescription());
            stmt.setString(3, recipe.getInstructions());
            stmt.setInt(4, recipe.getPrepTime());
            stmt.setInt(5, recipe.getCookTime());
            stmt.setDouble(6, recipe.getRating());
            stmt.setInt(7, recipe.getServings());
            stmt.setString(8, recipe.getImageUrl());
            stmt.setInt(9, recipe.getRecipeId());
            stmt.executeUpdate();
        }
    }

    /** Delete a recipe by ID (ingredients cascade-delete via FK). */
    public void delete(int recipeId) throws SQLException {
        String sql = "DELETE FROM recipes WHERE recipe_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, recipeId);
            stmt.executeUpdate();
        }
    }

    /** Map ResultSet row to Recipe (without ingredients/tags). */
    private Recipe mapRow(ResultSet rs) throws SQLException {
        Recipe r = new Recipe();
        r.setRecipeId(rs.getInt("recipe_id"));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setInstructions(rs.getString("instructions"));
        r.setPrepTime(rs.getInt("prep_time"));
        r.setCookTime(rs.getInt("cook_time"));
        r.setRating(rs.getDouble("rating"));
        r.setServings(rs.getInt("servings"));
        r.setImageUrl(rs.getString("image_url"));
        return r;
    }
}
