package com.example.yummyplan.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.R;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    Context context;
    private ArrayList<String> ingredientsList;

    public IngredientsAdapter(Context context,ArrayList<String> ingredientsList) {
        this.context = context;
        this.ingredientsList = ingredientsList;
    }

    @NonNull
    @Override
    public IngredientsAdapter.IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // انفلاتينق للتصميم تبعي في الxml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ingredient, parent, false);
        IngredientViewHolder holder = new IngredientViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.IngredientViewHolder holder, int position) {

        String ingredient = ingredientsList.get(position).trim();
        // عرض المكونات في التشيك بوكس
        holder.cb_ingredient_name.setText(ingredient);
        holder.cb_ingredient_name.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return ingredientsList.size();
    }

    // هان بعمل تعريف وانفلاتينق لعناصر الريسايكل فيو الي في ال xml ً
    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        CheckBox cb_ingredient_name;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_ingredient_name = itemView.findViewById(R.id.cb_ingredient_name);
        }
    }
}