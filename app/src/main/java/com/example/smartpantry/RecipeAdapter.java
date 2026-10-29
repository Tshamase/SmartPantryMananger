
package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {
    public interface Listener { void onOpen(Recipe recipe); }
    private final ArrayList<Recipe> items = new ArrayList<>();
    private final Listener listener;
    public RecipeAdapter(Listener listener) { this.listener = listener; }
    public void setItems(ArrayList<Recipe> next) { items.clear(); items.addAll(next); notifyDataSetChanged(); }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Recipe r = items.get(position);
        h.name.setText(r.name); h.cuisine.setText(r.cuisine.toUpperCase());
        h.description.setText(r.description);
        h.time.setText(r.prepTimeMinutes + " minutes  ·  " + r.servings + " servings");
        h.button.setOnClickListener(v -> listener.onOpen(r));
        h.itemView.setOnClickListener(v -> listener.onOpen(r));
    }
    @Override public int getItemCount() { return items.size(); }
    static class Holder extends RecyclerView.ViewHolder {
        TextView name, cuisine, description, time; Button button;
        Holder(View v) { super(v); cuisine=v.findViewById(R.id.recipeCuisine); name=v.findViewById(R.id.recipeName);
            description=v.findViewById(R.id.recipeDescription); time=v.findViewById(R.id.recipeTime); button=v.findViewById(R.id.viewRecipe); }
    }
}
