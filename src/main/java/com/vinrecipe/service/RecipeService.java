package com.vinrecipe.service;

import com.vinrecipe.dao.IngredientDAO;
import com.vinrecipe.dao.RecipeDAO;
import com.vinrecipe.dao.TagDAO;
import com.vinrecipe.model.Recipe;
import com.vinrecipe.model.Tag;

import java.sql.SQLException;
import java.util.List;

/**
 * Service layer for recipe management (CRUD).
 * Coordinates between RecipeDAO, IngredientDAO, and TagDAO.
 */
public class RecipeService {

    private final RecipeDAO recipeDAO         = new RecipeDAO();
    private final IngredientDAO ingredientDAO = new IngredientDAO();
    private final TagDAO tagDAO               = new TagDAO();

    /**
     * Create a new recipe with its ingredients and tags (transactional-style).
     * @return the new recipe_id on success, -1 on failure.
     */
    public int createRecipe(Recipe recipe) {
        try {
            int recipeId = recipeDAO.insert(recipe);
            if (recipeId == -1) return -1;

            recipe.setRecipeId(recipeId);

            // Insert ingredients
            if (!recipe.getIngredients().isEmpty()) {
                ingredientDAO.insertForRecipe(recipeId, recipe.getIngredients());
            }

            // Insert and link tags
            for (Tag tag : recipe.getTags()) {
                int tagId = tagDAO.insert(tag);
                if (tagId != -1) {
                    tagDAO.addTagToRecipe(recipeId, tagId);
                }
            }
            invalidateCache();
            return recipeId;
        } catch (SQLException e) {
            System.err.println("[RecipeService] createRecipe error: " + e.getMessage());
            return -1;
        }
    }

    /** Update an existing recipe: update core fields, replace ingredients and tags. */
    public boolean updateRecipe(Recipe recipe) {
        try {
            recipeDAO.update(recipe);
            // Replace ingredients
            ingredientDAO.deleteByRecipeId(recipe.getRecipeId());
            if (!recipe.getIngredients().isEmpty()) {
                ingredientDAO.insertForRecipe(recipe.getRecipeId(), recipe.getIngredients());
            }
            // Replace tags
            tagDAO.removeTagsFromRecipe(recipe.getRecipeId());
            for (Tag tag : recipe.getTags()) {
                int tagId = tagDAO.insert(tag);
                if (tagId != -1) {
                    tagDAO.addTagToRecipe(recipe.getRecipeId(), tagId);
                }
            }
            invalidateCache();
            return true;
        } catch (SQLException e) {
            System.err.println("[RecipeService] updateRecipe error: " + e.getMessage());
            return false;
        }
    }

    /** Delete a recipe and all its ingredients (cascades via FK). */
    public boolean deleteRecipe(int recipeId) {
        try {
            recipeDAO.delete(recipeId);
            invalidateCache();
            return true;
        } catch (SQLException e) {
            System.err.println("[RecipeService] deleteRecipe error: " + e.getMessage());
            return false;
        }
    }

    /** In-memory cache to avoid repeatedly hitting the remote Aiven DB (reduces lag). */
    private List<Recipe> recipeCache = null;

    /** Invalidate the cache (call after any create/update/delete). */
    public void invalidateCache() { recipeCache = null; }

    /**
     * Get all recipes from RAM cache (first call fetches from DB and populates cache).
     * Subsequent calls return instantly without a network round-trip.
     */
    public List<Recipe> getAllRecipes() {
        if (recipeCache == null) {
            try {
                recipeCache = recipeDAO.findAll();
            } catch (SQLException e) {
                System.err.println("[RecipeService] getAllRecipes error: " + e.getMessage());
                return List.of();
            }
        }
        return recipeCache;
    }

    /** Get a recipe by ID. */
    public Recipe getRecipeById(int recipeId) {
        try {
            return recipeDAO.findById(recipeId);
        } catch (SQLException e) {
            System.err.println("[RecipeService] getRecipeById error: " + e.getMessage());
            return null;
        }
    }

    /** Search recipes by title keyword. */
    public List<Recipe> searchByTitle(String keyword) {
        try {
            return recipeDAO.searchByTitle(keyword);
        } catch (SQLException e) {
            System.err.println("[RecipeService] searchByTitle error: " + e.getMessage());
            return List.of();
        }
    }

    /** Search recipes by tag name. */
    public List<Recipe> searchByTag(String tagName) {
        try {
            return recipeDAO.searchByTag(tagName);
        } catch (SQLException e) {
            System.err.println("[RecipeService] searchByTag error: " + e.getMessage());
            return List.of();
        }
    }

    /** Get all available tags (for ComboBox). */
    public List<Tag> getAllTags() {
        try {
            return tagDAO.findAll();
        } catch (SQLException e) {
            System.err.println("[RecipeService] getAllTags error: " + e.getMessage());
            return List.of();
        }
    }
}
