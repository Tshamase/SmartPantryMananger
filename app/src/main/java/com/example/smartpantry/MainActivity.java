
package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper db;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        Button pantry=findViewById(R.id.openPantry), recipes=findViewById(R.id.openRecipes), settings=findViewById(R.id.openSettings);
        pantry.setOnClickListener(v -> startActivity(new Intent(this, PantryActivity.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        settings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
    @Override protected void onResume() {
        super.onResume();
        if (db != null) {
            ((TextView)findViewById(R.id.itemCount)).setText(String.valueOf(db.countIngredients()));
            ((TextView)findViewById(R.id.recipeCount)).setText(String.valueOf(new RecipeMatchingService(db).getStrictMatches().size()));
            int expiring = db.countExpiringSoon();
            ((TextView)findViewById(R.id.expirySummary)).setText(expiring == 0 ? "No ingredients expiring in the next 3 days." : expiring + " ingredient(s) expiring in the next 3 days.");
        }
    }
    @Override protected void onDestroy() { if (db != null) db.close(); super.onDestroy(); }
}
