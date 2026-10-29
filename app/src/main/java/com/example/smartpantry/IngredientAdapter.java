
package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.Holder> {
    public interface Listener { void onEdit(Ingredient item); void onDelete(Ingredient item); }
    private final ArrayList<Ingredient> items = new ArrayList<>();
    private final Listener listener;

    public IngredientAdapter(Listener listener) { this.listener = listener; }
    public void setItems(ArrayList<Ingredient> next) { items.clear(); items.addAll(next); notifyDataSetChanged(); }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        Ingredient item = items.get(position);
        h.name.setText(item.name);
        h.meta.setText(format(item.quantity) + " " + item.unit);
        h.expiry.setText(item.expiryDate == null || item.expiryDate.isEmpty() ? "" : "Expiry: " + item.expiryDate);
        h.edit.setOnClickListener(v -> listener.onEdit(item));
        h.delete.setOnClickListener(v -> listener.onDelete(item));
    }
    private String format(double n) { return n == Math.rint(n) ? String.valueOf((long)n) : String.valueOf(n); }
    @Override public int getItemCount() { return items.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, meta, expiry; Button edit, delete;
        Holder(View v) { super(v); name=v.findViewById(R.id.ingredientName); meta=v.findViewById(R.id.ingredientMeta);
            expiry=v.findViewById(R.id.ingredientExpiry); edit=v.findViewById(R.id.editIngredient); delete=v.findViewById(R.id.deleteIngredient); }
    }
}
