package com.example.smart_pantry_manager.database;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "smart_pantry.db";
    private static final int DB_VERSION = 1;

    // Tables
    public static final String TABLE_PANTRY = "pantry";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Create Pantry Table
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        // 2. Create Recipes Table
        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "cuisine TEXT, " +
                "instructions TEXT)");

        // 3. Create Recipe Ingredients Table (Junction Table)
        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER, " +
                "ingredient_name TEXT, " +
                "required_quantity REAL, " +
                "unit TEXT, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id))");

        // 4. Seed the 15 Recipes
        seedRecipes(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        // Example: Seeding Recipe 1 (Bobotie)
        long recipeId = insertRecipe(db, "Classic Bobotie", "South African", "1. Sauté onion... 2. Bake at 180C...");
        insertRecipeIngredient(db, recipeId, "minced beef", 500, "g");
        insertRecipeIngredient(db, recipeId, "onion", 1, "pcs");
        insertRecipeIngredient(db, recipeId, "bread", 2, "slices");
        insertRecipeIngredient(db, recipeId, "milk", 125, "ml");
        insertRecipeIngredient(db, recipeId, "curry powder", 15, "ml");
        insertRecipeIngredient(db, recipeId, "fruit chutney", 30, "ml");
        insertRecipeIngredient(db, recipeId, "egg", 2, "pcs");
        insertRecipeIngredient(db, recipeId, "bay leaves", 3, "pcs");
        insertRecipeIngredient(db, recipeId, "salt", 5, "ml");

        // Repeat insertRecipe() and insertRecipeIngredient() for the remaining 14 recipes

    }

    // Helper methods for inserting data
    private long insertRecipe(SQLiteDatabase db, String name, String cuisine, String instructions) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("cuisine", cuisine);
        values.put("instructions", instructions);
        return db.insert(TABLE_RECIPES, null, values);
    }

    private void insertRecipeIngredient(SQLiteDatabase db, long recipeId, String name, float qty, String unit) {
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", name);
        values.put("required_quantity", qty);
        values.put("unit", unit);
        db.insert(TABLE_RECIPE_INGREDIENTS, null, values);
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PANTRY + " ORDER BY name ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PantryItem item = new PantryItem();
                item.setId(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setQuantity(cursor.getDouble(2));
                item.setUnit(cursor.getString(3));
                item.setExpiryDate(cursor.getString(4));
                list.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close(); // close database
        return list;
    }

    // Delete functionality. Remove an item by ID
    public boolean deletePantryItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_PANTRY, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0; // Returns true if successfully deleted
    }


}