package com.vinrecipe.model;

/**
 * Represents a single ingredient in a recipe.
 * POJO with encapsulated fields and getters/setters.
 */
public class Ingredient {

    private int ingredientId;
    private String name;
    private double quantity;
    private String unit;
    private double priceEstimate; // in VND or USD

    public Ingredient() {}

    public Ingredient(int ingredientId, String name, double quantity, String unit, double priceEstimate) {
        this.ingredientId = ingredientId;
        setName(name);
        setQuantity(quantity);
        this.unit = unit;
        setPriceEstimate(priceEstimate);
    }

    // ---- Getters / Setters with validation ----
    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Ingredient name must not be blank");
        this.name = name.trim().toLowerCase();
    }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity must be non-negative");
        this.quantity = quantity;
    }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPriceEstimate() { return priceEstimate; }
    public void setPriceEstimate(double priceEstimate) {
        if (priceEstimate < 0) throw new IllegalArgumentException("Price estimate must be non-negative");
        this.priceEstimate = priceEstimate;
    }

    @Override
    public String toString() {
        return quantity + " " + unit + " " + name;
    }
}
