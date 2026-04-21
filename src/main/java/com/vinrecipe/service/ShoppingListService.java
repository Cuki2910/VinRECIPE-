package com.vinrecipe.service;

import com.vinrecipe.model.Ingredient;
import com.vinrecipe.model.Recipe;
import com.vinrecipe.model.ShoppingList;

import java.util.List;
import java.util.Map;

/**
 * Service layer for shopping list generation.
 *
 * Core algorithm (per proposal.md):
 * Uses HashMap<String, Double> to aggregate ingredient quantities from multiple recipes.
 * Identical ingredient names → quantities are summed.
 */
public class ShoppingListService {

    /**
     * Generate an aggregated shopping list from a list of selected recipes.
     *
     * Steps:
     * 1. Create a ShoppingList and add all selected recipes
     * 2. Call generateList() which uses HashMap.merge(name, qty, Double::sum)
     * 3. Return the populated ShoppingList
     *
     * @param selectedRecipes recipes the user has selected
     * @return ShoppingList with aggregated items ready to display
     */
    public ShoppingList generate(List<Recipe> selectedRecipes) {
        ShoppingList shoppingList = new ShoppingList();
        for (Recipe recipe : selectedRecipes) {
            shoppingList.addRecipe(recipe);
        }
        shoppingList.generateList(); // populates aggregatedItems HashMap
        return shoppingList;
    }

    /**
     * Format the aggregated map into display strings.
     * Example: "500.0 g chicken"  →  "chicken: 500.0 g"
     *
     * @param shoppingList the generated shopping list
     * @return list of formatted strings for the ListView
     */
    public List<String> formatForDisplay(ShoppingList shoppingList) {
        Map<String, Double> aggregated = shoppingList.getAggregatedItems();
        Map<String, String> units      = shoppingList.getItemUnits();

        return aggregated.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // alphabetical order
                .map(entry -> {
                    String name = entry.getKey();
                    double qty  = entry.getValue();
                    String unit = units.getOrDefault(name, "");
                    return String.format("%-20s %.1f %s", capitalize(name), qty, unit);
                })
                .toList();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
