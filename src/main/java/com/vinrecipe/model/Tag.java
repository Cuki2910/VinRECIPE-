package com.vinrecipe.model;

/**
 * Represents a tag/category for filtering recipes.
 * POJO — simple encapsulated data class.
 */
public class Tag {

    private int tagId;
    private String name;

    public Tag() {}

    public Tag(int tagId, String name) {
        this.tagId = tagId;
        setName(name);
    }

    // ---- Getters / Setters ----
    public int getTagId() { return tagId; }
    public void setTagId(int tagId) { this.tagId = tagId; }

    public String getName() { return name; }
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Tag name must not be blank");
        this.name = name.trim();
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return tagId == tag.tagId;
    }

    @Override
    public int hashCode() { return Integer.hashCode(tagId); }
}
