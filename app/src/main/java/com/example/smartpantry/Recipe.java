
package com.example.smartpantry;

public class Recipe {
    public long id;
    public String name, cuisine, description, instructions;
    public int prepTimeMinutes, servings;

    public Recipe(long id, String name, String cuisine, String description,
                  int prepTimeMinutes, int servings, String instructions) {
        this.id = id;
        this.name = name;
        this.cuisine = cuisine;
        this.description = description;
        this.prepTimeMinutes = prepTimeMinutes;
        this.servings = servings;
        this.instructions = instructions;
    }
}
