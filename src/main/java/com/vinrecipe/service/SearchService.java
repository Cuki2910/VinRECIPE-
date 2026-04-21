package com.vinrecipe.service;

import com.vinrecipe.model.Ingredient;
import com.vinrecipe.model.Recipe;

import java.util.*;

/**
 * Search service implementing the Inverted Index algorithm.
 *
 * Key data structures (as required by proposal):
 * - HashMap<String, List<Recipe>> — Inverted Index: ingredient name → recipes containing it
 * - HashMap<Recipe, Integer> — matchCount: tracks how many user ingredients each recipe matches
 * - HashSet<String> — ensures unique tag names for ComboBox dropdown
 */
public class SearchService {

    // Inverted index: ingredient name (lowercase) → list of recipes containing it
    private final Map<String, List<Recipe>> invertedIndex = new HashMap<>();

    // All recipes loaded into memory for indexing
    private List<Recipe> allRecipes = new ArrayList<>();

    /**
     * Build the inverted index from a list of recipes.
     * Call this once when data is loaded (or when data changes).
     */
    public void buildIndex(List<Recipe> recipes) {
        this.allRecipes = new ArrayList<>(recipes);
        invertedIndex.clear();

        for (Recipe recipe : recipes) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                String key = ingredient.getName().toLowerCase().trim();
                invertedIndex
                        .computeIfAbsent(key, k -> new ArrayList<>())
                        .add(recipe);
            }
        }
    }

    /**
     * Find recipes that match the user's available ingredients.
     *
     * Algorithm (per implementation_plan.md):
     * 1. For each user ingredient → lookup invertedIndex → get matching recipes
     * 2. Count matches per recipe (matchCount HashMap)
     * 3. Compute completion% = matchCount / recipe.totalIngredients
     * 4. Sort by completion% DESC, then prepTime ASC
     *
     * @param userIngredients list of ingredient names the user has
     * @return sorted list of matching recipes
     */
    public List<Recipe> searchByIngredients(List<String> userIngredients) {
        Map<Recipe, Integer> matchCount = new HashMap<>();

        for (String userIng : userIngredients) {
            String key = userIng.toLowerCase().trim();
            List<Recipe> matches = invertedIndex.getOrDefault(key, Collections.emptyList());
            for (Recipe recipe : matches) {
                matchCount.merge(recipe, 1, Integer::sum);
            }
        }

        // Build result list, compute completion%, sort
        List<Recipe> results = new ArrayList<>(matchCount.keySet());
        results.sort((a, b) -> {
            double completionA = (double) matchCount.get(a) / Math.max(a.getIngredients().size(), 1);
            double completionB = (double) matchCount.get(b) / Math.max(b.getIngredients().size(), 1);
            if (completionB != completionA) {
                return Double.compare(completionB, completionA); // DESC by completion
            }
            return Integer.compare(a.getPrepTime(), b.getPrepTime()); // ASC by prepTime
        });

        return results;
    }

    /**
     * Get unique tag names for the ComboBox filter.
     * Uses HashSet to ensure no duplicates — as required by proposal.
     */
    public Set<String> getUniqueTagNames() {
        Set<String> tagNames = new HashSet<>();
        for (Recipe recipe : allRecipes) {
            recipe.getTags().forEach(tag -> tagNames.add(tag.getName()));
        }
        return tagNames;
    }

    /**
     * Filter recipes by tag name (exact match, case-insensitive).
     */
    public List<Recipe> filterByTag(String tagName) {
        List<Recipe> results = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            boolean hasTag = recipe.getTags().stream()
                    .anyMatch(t -> t.getName().equalsIgnoreCase(tagName));
            if (hasTag) results.add(recipe);
        }
        return results;
    }

    /**
     * Sort all recipes by rating (DESC).
     */
    public List<Recipe> sortByRating(List<Recipe> recipes) {
        List<Recipe> sorted = new ArrayList<>(recipes);
        sorted.sort(Comparator.comparingDouble(Recipe::getRating).reversed());
        return sorted;
    }

    /**
     * Sort all recipes by prep time (ASC).
     */
    public List<Recipe> sortByPrepTime(List<Recipe> recipes) {
        List<Recipe> sorted = new ArrayList<>(recipes);
        sorted.sort(Comparator.comparingInt(Recipe::getPrepTime));
        return sorted;
    }

    /**
     * Sort all recipes by total estimated price (ASC).
     */
    public List<Recipe> sortByPrice(List<Recipe> recipes) {
        List<Recipe> sorted = new ArrayList<>(recipes);
        sorted.sort(Comparator.comparingDouble(Recipe::getTotalPrice));
        return sorted;
    }

    /** Return all recipes currently indexed. */
    public List<Recipe> getAllRecipes() {
        return Collections.unmodifiableList(allRecipes);
    }
}
