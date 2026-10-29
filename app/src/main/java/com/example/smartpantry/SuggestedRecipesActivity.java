
package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SuggestedRecipesActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView empty;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);setContentView(R.layout.activity_recipes);
        db=new DatabaseHelper(this);empty=findViewById(R.id.emptyRecipes);
        RecyclerView list=findViewById(R.id.recipeList);list.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RecipeAdapter(recipe->{Intent i=new Intent(this,RecipeDetailActivity.class);i.putExtra("recipe_id",recipe.id);startActivity(i);});
        list.setAdapter(adapter);
        findViewById(R.id.navHome).setOnClickListener(v->{startActivity(new Intent(this,MainActivity.class));finish();});
        findViewById(R.id.navPantry).setOnClickListener(v->startActivity(new Intent(this,PantryActivity.class)));
        findViewById(R.id.navSettings).setOnClickListener(v->startActivity(new Intent(this,SettingsActivity.class)));
    }
    @Override protected void onResume(){super.onResume();if(db!=null){java.util.ArrayList<Recipe> matches=new RecipeMatchingService(db).getStrictMatches();adapter.setItems(matches);empty.setVisibility(matches.isEmpty()?View.VISIBLE:View.GONE);}}
    @Override protected void onDestroy(){if(db!=null)db.close();super.onDestroy();}
}
