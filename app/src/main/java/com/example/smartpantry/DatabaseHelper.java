
package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, normalized_name TEXT NOT NULL, quantity REAL NOT NULL CHECK(quantity > 0), unit TEXT NOT NULL, expiry_date TEXT, created_at TEXT DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("CREATE INDEX idx_ingredients_normalized ON ingredients(normalized_name)");
        db.execSQL("CREATE TABLE recipes (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, cuisine TEXT NOT NULL, description TEXT NOT NULL, prep_time_minutes INTEGER NOT NULL, servings INTEGER NOT NULL, instructions TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredients (id INTEGER PRIMARY KEY AUTOINCREMENT, recipe_id INTEGER NOT NULL, ingredient_name TEXT NOT NULL, normalized_name TEXT NOT NULL, required_quantity REAL NOT NULL CHECK(required_quantity > 0), unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");
        db.execSQL("CREATE INDEX idx_recipe_ingredients_recipe ON recipe_ingredients(recipe_id)");
        seedRecipes(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add non-destructive migrations here as the schema evolves.
    }

    private static String norm(String name) {
        String n = name == null ? "" : name.trim().toLowerCase(Locale.ROOT);
        if (n.endsWith("ies") && n.length() > 4) n = n.substring(0, n.length()-3) + "y";
        else if (n.endsWith("oes") && n.length() > 4) n = n.substring(0, n.length()-2);
        else if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 3) n = n.substring(0, n.length()-1);
        return n;
    }

    public long addIngredient(String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("normalized_name", norm(name));
        v.put("quantity", quantity); v.put("unit", unit.trim().toLowerCase(Locale.ROOT));
        v.put("expiry_date", expiry == null || expiry.trim().isEmpty() ? null : expiry.trim());
        return getWritableDatabase().insertOrThrow("ingredients", null, v);
    }

    public int updateIngredient(long id, String name, double quantity, String unit, String expiry) {
        ContentValues v = new ContentValues();
        v.put("name", name.trim()); v.put("normalized_name", norm(name));
        v.put("quantity", quantity); v.put("unit", unit.trim().toLowerCase(Locale.ROOT));
        v.put("expiry_date", expiry == null || expiry.trim().isEmpty() ? null : expiry.trim());
        return getWritableDatabase().update("ingredients", v, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteIngredient(long id) {
        return getWritableDatabase().delete("ingredients", "id=?", new String[]{String.valueOf(id)});
    }

    public Ingredient getIngredient(long id) {
        try (Cursor c = getReadableDatabase().query("ingredients", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) return readIngredient(c);
        }
        return null;
    }

    public ArrayList<Ingredient> getAllIngredients() {
        ArrayList<Ingredient> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("ingredients", null, null, null, null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) list.add(readIngredient(c));
        }
        return list;
    }

    public int countIngredients() {
        try (Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM ingredients", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    public int countExpiringSoon() {
        try (Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM ingredients WHERE expiry_date IS NOT NULL AND expiry_date<>'' AND date(expiry_date) BETWEEN date('now') AND date('now','+3 day')", null)) {
            return c.moveToFirst() ? c.getInt(0) : 0;
        }
    }

    private Ingredient readIngredient(Cursor c) {
        return new Ingredient(c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry_date")));
    }

    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> out = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("recipes", null, null, null, null, null, "name COLLATE NOCASE")) {
            while (c.moveToNext()) out.add(readRecipe(c));
        }
        return out;
    }

    public Recipe getRecipe(long id) {
        try (Cursor c = getReadableDatabase().query("recipes", null, "id=?", new String[]{String.valueOf(id)}, null, null, null)) {
            if (c.moveToFirst()) return readRecipe(c);
        }
        return null;
    }

    private Recipe readRecipe(Cursor c) {
        return new Recipe(c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getString(c.getColumnIndexOrThrow("cuisine")),
                c.getString(c.getColumnIndexOrThrow("description")),
                c.getInt(c.getColumnIndexOrThrow("prep_time_minutes")),
                c.getInt(c.getColumnIndexOrThrow("servings")),
                c.getString(c.getColumnIndexOrThrow("instructions")));
    }

    public ArrayList<RecipeIngredient> getRecipeIngredients(long recipeId) {
        ArrayList<RecipeIngredient> out = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("recipe_ingredients", null, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, "id ASC")) {
            while (c.moveToNext()) out.add(new RecipeIngredient(
                    c.getString(c.getColumnIndexOrThrow("ingredient_name")),
                    c.getDouble(c.getColumnIndexOrThrow("required_quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit"))));
        }
        return out;
    }

    private void seedRecipes(SQLiteDatabase db) {
        // Ingredient strings use: name|quantity|unit. Quantities are for the stated recipe serving size.
        seed(db, "Pap and Chakalaka", "South African", "A comforting maize-meal staple served with spicy tomato and vegetable relish.", 40, 4,
            "1. Bring water and a little salt to a boil.\\n2. Gradually whisk in maize meal, reduce heat and stir until thick and cooked.\\n3. For chakalaka, heat oil and cook onion and green pepper until softened.\\n4. Add carrot, tomatoes, curry powder and baked beans. Simmer until tender.\\n5. Serve the chakalaka with the pap.",
            "maize meal|300|g", "water|750|ml", "salt|5|g", "cooking oil|30|ml", "onion|1|piece", "tomato|3|piece", "green pepper|1|piece", "carrot|1|piece", "baked beans|410|g", "curry powder|5|g");
        seed(db, "Bobotie", "South African", "A fragrant baked minced-beef dish topped with a savoury egg custard.", 65, 4,
            "1. Heat the oven to 180°C.\\n2. Soak the bread in milk and mash it.\\n3. Cook onion in a little oil, then add minced beef, curry powder, turmeric, chutney and raisins.\\n4. Stir in the soaked bread and season with salt. Place in a baking dish.\\n5. Beat the egg with remaining milk, pour over the meat and add bay leaves.\\n6. Bake until the topping is set and the meat is cooked through.",
            "minced beef|500|g", "onion|1|piece", "bread|2|slice", "milk|250|ml", "egg|1|piece", "curry powder|10|g", "turmeric|3|g", "chutney|30|g", "raisins|40|g", "bay leaves|2|piece");
        seed(db, "Boerewors Rolls", "South African", "Grilled boerewors in soft rolls with caramelised onion and condiments.", 25, 4,
            "1. Cook the boerewors on a grill or in a pan until fully cooked.\\n2. Slice the onion and cook in oil until soft and golden.\\n3. Warm the rolls.\\n4. Place boerewors in each roll, top with onion, tomato sauce and mustard.",
            "boerewors|600|g", "hot dog rolls|4|piece", "onion|1|piece", "cooking oil|15|ml", "tomato sauce|40|g", "mustard|20|g");
        seed(db, "South African Chicken Curry", "South African", "A warming home-style curry with chicken, potatoes and tomatoes.", 50, 4,
            "1. Heat oil in a pot and cook chopped onion and garlic until fragrant.\\n2. Add curry powder and stir briefly.\\n3. Add chicken and brown lightly.\\n4. Add chopped tomatoes, potatoes, water and salt.\\n5. Cover and simmer until the chicken is fully cooked and potatoes are tender.",
            "chicken|600|g", "onion|1|piece", "tomato|3|piece", "cooking oil|30|ml", "curry powder|12|g", "garlic|2|clove", "potato|3|piece", "water|250|ml", "salt|5|g");
        seed(db, "Vetkoek with Minced Beef Filling", "South African", "Golden fried dough filled with a savoury curried mince.", 75, 4,
            "1. Mix flour, yeast, sugar and salt. Add warm water gradually and knead into a soft dough. Let rise until doubled.\\n2. Cook onion in a little oil, add mince, tomatoes and curry powder, then simmer until cooked.\\n3. Shape dough into balls and let rest briefly.\\n4. Fry in hot oil in batches until golden and cooked through. Drain safely.\\n5. Split and fill with the cooked mince.",
            "flour|500|g", "yeast|7|g", "sugar|10|g", "salt|8|g", "warm water|300|ml", "minced beef|400|g", "onion|1|piece", "tomato|2|piece", "cooking oil|500|ml", "curry powder|8|g");
        seed(db, "Classic American Pancakes", "American", "Fluffy breakfast pancakes made from a simple batter.", 20, 4,
            "1. Mix flour, baking powder, sugar and salt.\\n2. Whisk milk and egg together, then stir into the dry ingredients until just combined.\\n3. Melt a little butter in a non-stick pan.\\n4. Pour small rounds of batter and cook until bubbles form. Flip and cook the other side.\\n5. Repeat with remaining batter.",
            "flour|250|g", "milk|300|ml", "egg|2|piece", "baking powder|10|g", "sugar|25|g", "salt|3|g", "butter|30|g");
        seed(db, "Macaroni and Cheese", "American", "Creamy macaroni in a smooth cheddar cheese sauce.", 30, 4,
            "1. Cook macaroni in boiling water until just tender, then drain.\\n2. Melt butter in a saucepan and stir in flour to make a paste.\\n3. Gradually whisk in milk and cook until thickened.\\n4. Stir in grated cheddar and salt until smooth.\\n5. Combine with macaroni and serve.",
            "macaroni|300|g", "milk|500|ml", "butter|40|g", "flour|30|g", "cheddar cheese|200|g", "salt|4|g");
        seed(db, "Classic Cheeseburger", "American", "A juicy beef burger with cheddar and crisp vegetables.", 25, 4,
            "1. Shape ground beef into four patties and season.\\n2. Heat oil in a pan and cook patties thoroughly, turning once.\\n3. Add cheddar during the final minute so it melts.\\n4. Lightly toast burger buns.\\n5. Assemble with lettuce, sliced tomato and onion.",
            "ground beef|500|g", "burger buns|4|piece", "cheddar cheese|4|slice", "onion|1|piece", "lettuce|4|leaf", "tomato|2|piece", "cooking oil|15|ml");
        seed(db, "American-Style Chicken Fried Rice", "American", "A quick skillet meal of rice, chicken, egg and vegetables.", 25, 4,
            "1. Heat oil in a large pan and cook diced chicken until fully cooked.\\n2. Add carrots and peas and cook until tender.\\n3. Push ingredients aside and scramble the egg in the pan.\\n4. Add cooked rice and soy sauce.\\n5. Stir-fry until hot throughout and serve.",
            "cooked rice|600|g", "chicken|300|g", "egg|2|piece", "carrot|2|piece", "peas|100|g", "soy sauce|30|ml", "cooking oil|20|ml");
        seed(db, "Grilled Cheese Sandwich", "American", "A crisp buttery sandwich with melted cheddar cheese.", 10, 2,
            "1. Butter one side of each bread slice.\\n2. Place cheddar between the unbuttered sides.\\n3. Cook in a pan over medium-low heat until the bread is golden.\\n4. Flip carefully and cook until the cheese melts.\\n5. Slice and serve warm.",
            "bread|4|slice", "cheddar cheese|120|g", "butter|30|g");
        seed(db, "Kimchi Fried Rice (Kimchi Bokkeumbap)", "Korean", "Tangy kimchi fried with rice and topped with a cooked egg.", 20, 2,
            "1. Heat oil in a pan and stir-fry chopped kimchi for a few minutes.\\n2. Add cooked rice and soy sauce, breaking up clumps as you stir.\\n3. Cook until hot throughout.\\n4. In a separate part of the pan, cook the egg to your preference.\\n5. Top the rice with sliced spring onion and the egg.",
            "cooked rice|400|g", "kimchi|150|g", "egg|2|piece", "spring onion|2|piece", "soy sauce|15|ml", "cooking oil|15|ml");
        seed(db, "Korean Bulgogi Beef", "Korean", "Thin beef strips marinated in a sweet, savoury soy-garlic sauce.", 35, 3,
            "1. Mix soy sauce, minced garlic, sugar and sesame oil.\\n2. Add beef strips and sliced onion; marinate for at least 15 minutes.\\n3. Heat a pan until hot.\\n4. Cook beef and onion in batches until browned and cooked through.\\n5. Finish with sliced spring onion.",
            "beef strips|450|g", "soy sauce|45|ml", "garlic|3|clove", "sugar|15|g", "sesame oil|10|ml", "onion|1|piece", "spring onion|2|piece");
        seed(db, "Bibimbap", "Korean", "A colourful Korean rice bowl with vegetables, beef, egg and gochujang.", 40, 2,
            "1. Cook the rice and set aside.\\n2. Cook beef in a pan until fully cooked.\\n3. Cook spinach and bean sprouts separately until tender; lightly sauté the carrots.\\n4. Fry the eggs.\\n5. Divide rice between bowls and arrange beef and vegetables on top.\\n6. Serve with gochujang and sesame oil, mixing before eating.",
            "cooked rice|400|g", "beef strips|200|g", "spinach|100|g", "carrot|1|piece", "bean sprouts|100|g", "egg|2|piece", "gochujang|30|g", "sesame oil|10|ml");
        seed(db, "Korean Vegetable Pancakes (Yachaejeon)", "Korean", "Crisp savoury pancakes filled with spring onion and vegetables.", 25, 3,
            "1. Whisk flour, water, egg and salt into a smooth batter.\\n2. Fold in thinly sliced carrots, spring onion and onion.\\n3. Heat oil in a non-stick pan.\\n4. Spread batter into thin pancakes and cook until golden and crisp on both sides.\\n5. Cut into wedges and serve hot.",
            "flour|180|g", "water|180|ml", "egg|1|piece", "carrot|1|piece", "spring onion|3|piece", "onion|1|piece", "salt|4|g", "cooking oil|30|ml");
        seed(db, "Korean Soy Garlic Chicken", "Korean", "Crisp chicken coated in a sticky soy, garlic and honey sauce.", 40, 4,
            "1. Cut chicken into bite-sized pieces. Toss with flour to coat lightly.\\n2. Heat oil in a pan and cook chicken until golden and fully cooked.\\n3. In a small pan, gently warm soy sauce, minced garlic, honey and water.\\n4. Simmer until the sauce is slightly thickened.\\n5. Toss cooked chicken in the sauce and serve.",
            "chicken|600|g", "soy sauce|45|ml", "garlic|4|clove", "honey|30|g", "cooking oil|30|ml", "flour|60|g", "water|60|ml");
    }

    private void seed(SQLiteDatabase db, String name, String cuisine, String description, int prep, int servings,
                      String instructions, String... ingredientSpecs) {
        ContentValues rv = new ContentValues();
        rv.put("name", name); rv.put("cuisine", cuisine); rv.put("description", description);
        rv.put("prep_time_minutes", prep); rv.put("servings", servings); rv.put("instructions", instructions);
        long id = db.insertOrThrow("recipes", null, rv);
        for (String spec : ingredientSpecs) {
            String[] p = spec.split("\\|", -1);
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", id); iv.put("ingredient_name", p[0]); iv.put("normalized_name", norm(p[0]));
            iv.put("required_quantity", Double.parseDouble(p[1])); iv.put("unit", p[2].toLowerCase(Locale.ROOT));
            db.insertOrThrow("recipe_ingredients", null, iv);
        }
    }
}
