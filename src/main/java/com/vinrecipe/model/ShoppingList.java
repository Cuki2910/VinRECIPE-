package com.vinrecipe.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shopping list that aggregates ingredients from selected recipes.
 * Uses HashMap to combine duplicate ingredients — key data structure per requirements.
 */
public class ShoppingList {

    private int listId;
    private User owner;
    private List<Recipe> selectedRecipes;             // selected recipes
    private Map<String, Double> aggregatedItems;      // ingredient name → total quantity (HashMap)
    private Map<String, String> itemUnits;            // ingredient name → unit

    public ShoppingList() {
        this.selectedRecipes = new ArrayList<>();
        this.aggregatedItems = new HashMap<>();
        this.itemUnits = new HashMap<>();
    }

    public ShoppingList(int listId, User owner) {
        this.listId = listId;
        this.owner = owner;
        this.selectedRecipes = new ArrayList<>();
        this.aggregatedItems = new HashMap<>();
        this.itemUnits = new HashMap<>();
    }

    /** Add a recipe to the selection. */
    public void addRecipe(Recipe recipe) {
        if (recipe != null && !selectedRecipes.contains(recipe)) {
            selectedRecipes.add(recipe);
        }
    }

    /** Remove a recipe from the selection. */
    public void removeRecipe(Recipe recipe) {
        selectedRecipes.remove(recipe);
    }

    /**
     * Generate the aggregated shopping list from all selected recipes.
     * Uses HashMap.merge to sum quantities of same-named ingredients.
     *
     * @return Map of ingredient name → total quantity
     */
    public Map<String, Double> generateList() {
        aggregatedItems.clear();
        itemUnits.clear();
        for (Recipe recipe : selectedRecipes) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                String key = ingredient.getName(); // already lowercase from setter
                aggregatedItems.merge(key, ingredient.getQuantity(), Double::sum);
                // store unit (last one wins if mixed — acceptable for this scope)
                itemUnits.put(key, ingredient.getUnit());
            }
        }
        return aggregatedItems;
    }

    /** Clear all selections and aggregated data. */
    public void clearList() {
        selectedRecipes.clear();
        aggregatedItems.clear();
        itemUnits.clear();
    }

    // ---- Getters / Setters ----
    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<Recipe> getSelectedRecipes() { return selectedRecipes; }
    public void setSelectedRecipes(List<Recipe> selectedRecipes) {
        this.selectedRecipes = selectedRecipes != null ? selectedRecipes : new ArrayList<>();
    }

    public Map<String, Double> getAggregatedItems() { return aggregatedItems; }

    public Map<String, String> getItemUnits() { return itemUnits; }
}
