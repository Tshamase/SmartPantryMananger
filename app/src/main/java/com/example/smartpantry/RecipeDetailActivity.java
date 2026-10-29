
package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {
    private DatabaseHelper db;
    @Override protected void onCreate(Bundle state){
        super.onCreate(state);setContentView(R.layout.activity_recipe_detail);db=new DatabaseHelper(this);
        long id=getIntent().getLongExtra("recipe_id",-1);
        Recipe r=db.getRecipe(id);
        if(r==null){finish();return;}
        ((TextView)findViewById(R.id.detailCuisine)).setText(r.cuisine.toUpperCase());
        ((TextView)findViewById(R.id.detailName)).setText(r.name);
        ((TextView)findViewById(R.id.detailDescription)).setText(r.description);
        ((TextView)findViewById(R.id.detailMeta)).setText(r.prepTimeMinutes+" minutes · "+r.servings+" servings");
        ArrayList<RecipeIngredient> ingredients=db.getRecipeIngredients(id);
        StringBuilder b=new StringBuilder();
        for(RecipeIngredient x:ingredients)b.append("• ").append(format(x.quantity)).append(" ").append(x.unit).append(" ").append(x.name).append("\n");
        ((TextView)findViewById(R.id.detailIngredients)).setText(b.toString().trim());
        ((TextView)findViewById(R.id.detailInstructions)).setText(r.instructions.replace("\\n","\n"));
        findViewById(R.id.backButton).setOnClickListener(v->finish());
    }
    private String format(double n){return n==Math.rint(n)?String.valueOf((long)n):String.valueOf(n);}
    @Override protected void onDestroy(){if(db!=null)db.close();super.onDestroy();}
}
