
package com.example.smartpantry;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Locale;

public class PantryActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private IngredientAdapter adapter;
    private EditText search;
    private TextView empty;
    private ArrayList<Ingredient> all = new ArrayList<>();

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_pantry);
        db = new DatabaseHelper(this);
        search=findViewById(R.id.searchIngredients); empty=findViewById(R.id.emptyPantry);
        RecyclerView list=findViewById(R.id.ingredientList); list.setLayoutManager(new LinearLayoutManager(this));
        adapter=new IngredientAdapter(new IngredientAdapter.Listener() {
            @Override public void onEdit(Ingredient item) {
                Intent i=new Intent(PantryActivity.this, AddEditIngredientActivity.class);
                i.putExtra("ingredient_id", item.id); startActivity(i);
            }
            @Override public void onDelete(Ingredient item) {
                new AlertDialog.Builder(PantryActivity.this).setTitle("Delete ingredient?")
                    .setMessage("Remove " + item.name + " from your pantry?")
                    .setNegativeButton("Cancel", null).setPositiveButton("Delete", (d,w)->{db.deleteIngredient(item.id); load();})
                    .show();
            }
        });
        list.setAdapter(adapter);
        findViewById(R.id.addIngredient).setOnClickListener(v -> startActivity(new Intent(this, AddEditIngredientActivity.class)));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int before,int count){ filter(s.toString()); }
            public void afterTextChanged(Editable e){}
        });
        findViewById(R.id.navHome).setOnClickListener(v -> {startActivity(new Intent(this, MainActivity.class)); finish();});
        findViewById(R.id.navRecipes).setOnClickListener(v -> startActivity(new Intent(this, SuggestedRecipesActivity.class)));
        findViewById(R.id.navSettings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
    @Override protected void onResume(){super.onResume(); if(db!=null)load();}
    private void load(){all=db.getAllIngredients();filter(search.getText().toString());}
    private void filter(String q){
        ArrayList<Ingredient> shown=new ArrayList<>(); String needle=q.toLowerCase(Locale.ROOT).trim();
        for(Ingredient i:all)if(i.name.toLowerCase(Locale.ROOT).contains(needle))shown.add(i);
        adapter.setItems(shown); empty.setVisibility(shown.isEmpty()?TextView.VISIBLE:TextView.GONE);
        empty.setText(all.isEmpty()?"Your pantry is empty. Add ingredients to get started.":"No ingredients match your search.");
    }
    @Override protected void onDestroy(){if(db!=null)db.close();super.onDestroy();}
}
