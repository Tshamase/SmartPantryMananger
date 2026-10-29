
package com.example.smartpantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Strict matcher: every required ingredient must exist in sufficient compatible quantity. */
public class RecipeMatchingService {
    private final DatabaseHelper db;
    public RecipeMatchingService(DatabaseHelper db) { this.db = db; }

    public ArrayList<Recipe> getStrictMatches() {
        ArrayList<Recipe> matches = new ArrayList<>();
        ArrayList<Ingredient> pantry = db.getAllIngredients();
        Map<String, Ingredient> byName = new HashMap<>();
        for (Ingredient i : pantry) {
            String key = normalizeName(i.name);
            Ingredient previous = byName.get(key);
            // Multiple pantry rows for the same ingredient are combined only when their units match.
            if (previous == null) byName.put(key, i);
            else if (canonicalUnit(previous.unit).equals(canonicalUnit(i.unit))) {
                byName.put(key, new Ingredient(previous.id, previous.name,
                        previous.quantity + i.quantity, previous.unit, previous.expiryDate));
            }
        }
        for (Recipe recipe : db.getAllRecipes()) {
            ArrayList<RecipeIngredient> required = db.getRecipeIngredients(recipe.id);
            boolean allPresent = !required.isEmpty();
            for (RecipeIngredient need : required) {
                Ingredient have = byName.get(normalizeName(need.name));
                if (have == null || !enough(have, need)) { allPresent = false; break; }
            }
            if (allPresent) matches.add(recipe);
        }
        return matches;
    }

    public static String normalizeName(String value) {
        String n = value == null ? "" : value.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        // Explicit common food-name aliases and conservative singularisation.
        if (n.equals("tomatoes")) return "tomato";
        if (n.equals("potatoes")) return "potato";
        if (n.equals("carrots")) return "carrot";
        if (n.equals("onions")) return "onion";
        if (n.equals("eggs")) return "egg";
        if (n.equals("peas")) return "pea";
        if (n.equals("leaves")) return "leaf";
        if (n.endsWith("ies") && n.length() > 4) return n.substring(0, n.length() - 3) + "y";
        if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 3) return n.substring(0, n.length() - 1);
        return n;
    }

    private boolean enough(Ingredient have, RecipeIngredient need) {
        String haveUnit = canonicalUnit(have.unit);
        String needUnit = canonicalUnit(need.unit);
        if (haveUnit.equals(needUnit)) return have.quantity + 0.000001 >= need.quantity;
        // Only well-defined mass/volume conversions are supported. Never convert mass to volume.
        if (haveUnit.equals("g") && needUnit.equals("kg")) return have.quantity + 0.000001 >= need.quantity * 1000.0;
        if (haveUnit.equals("kg") && needUnit.equals("g")) return have.quantity * 1000.0 + 0.000001 >= need.quantity;
        if (haveUnit.equals("ml") && needUnit.equals("l")) return have.quantity + 0.000001 >= need.quantity * 1000.0;
        if (haveUnit.equals("l") && needUnit.equals("ml")) return have.quantity * 1000.0 + 0.000001 >= need.quantity;
        return false;
    }

    private String canonicalUnit(String unit) {
        String u = unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT);
        if (u.equals("grams") || u.equals("gram")) return "g";
        if (u.equals("kilograms") || u.equals("kilogram")) return "kg";
        if (u.equals("millilitres") || u.equals("milliliters") || u.equals("millilitre") || u.equals("milliliter")) return "ml";
        if (u.equals("litres") || u.equals("liters") || u.equals("litre") || u.equals("liter")) return "l";
        if (u.equals("pieces") || u.equals("piece") || u.equals("pcs") || u.equals("pc")) return "piece";
        if (u.equals("slices") || u.equals("slice")) return "slice";
        if (u.equals("cloves") || u.equals("clove")) return "clove";
        if (u.equals("leaves") || u.equals("leaf")) return "leaf";
        return u;
    }
}
