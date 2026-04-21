package com.vinrecipe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cooking recipe.
 * Demonstrates Encapsulation (private fields + validation) and Composition (HAS-A Ingredient list).
 */
public class Recipe {

    private int recipeId;
    private String title;
    private String description;
    private String instructions;
    private int prepTime;   // in minutes
    private int cookTime;   // in minutes
    private double rating;  // 0.0 – 5.0
    private int servings;
    private User author;
    private List<Ingredient> ingredients; // Composition
    private List<Tag> tags;
    private String imageUrl; // optional image path/url

    public Recipe() {
        this.ingredients = new ArrayList<>();
        this.tags = new ArrayList<>();
    }

    public Recipe(int recipeId, String title, String description, String instructions,
                  int prepTime, int cookTime, int servings, User author) {
        this.recipeId = recipeId;
        setTitle(title);
        this.description = description;
        this.instructions = instructions;
        setPrepTime(prepTime);
        setCookTime(cookTime);
        setServings(servings);
        this.author = author;
        this.ingredients = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.rating = 0.0;
    }

    // ---- Business Methods ----

    /** Total cooking time = prepTime + cookTime. */
    public int getTotalTime() {
        return prepTime + cookTime;
    }

    /** Add ingredient to this recipe. */
    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) throw new IllegalArgumentException("Ingredient must not be null");
        ingredients.add(ingredient);
    }

    /** Remove ingredient from this recipe. */
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
    }

    /** Add tag to this recipe. */
    public void addTag(Tag tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.add(tag);
        }
    }

    /** Calculate estimated total price of all ingredients. */
    public double getTotalPrice() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPriceEstimate)
                .sum();
    }

    // ---- Getters / Setters with validation ----
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title must not be blank");
        this.title = title.trim();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) {
        if (prepTime < 0) throw new IllegalArgumentException("Prep time must be non-negative");
        this.prepTime = prepTime;
    }

    public int getCookTime() { return cookTime; }
    public void setCookTime(int cookTime) {
        if (cookTime < 0) throw new IllegalArgumentException("Cook time must be non-negative");
        this.cookTime = cookTime;
    }

    public double getRating() { return rating; }
    public void setRating(double rating) {
        if (rating < 0.0 || rating > 5.0)
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        this.rating = rating;
    }

    public int getServings() { return servings; }
    public void setServings(int servings) {
        if (servings <= 0) throw new IllegalArgumentException("Servings must be positive");
        this.servings = servings;
    }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public String toString() {
        return "Recipe{id=" + recipeId + ", title='" + title + "', rating=" + rating + "}";
    }
}
