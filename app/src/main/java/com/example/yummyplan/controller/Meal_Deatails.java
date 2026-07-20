package com.example.yummyplan.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.R;
import com.example.yummyplan.databinding.ActivityMealDeatailsBinding;
import com.example.yummyplan.model.Meal;
import com.example.yummyplan.view.IngredientsAdapter;

import java.util.ArrayList;

public class Meal_Deatails extends AppCompatActivity {

    private ActivityMealDeatailsBinding binding;
    private DatabaseHelper db_helper;
    private int mealId;
    private Meal meal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMealDeatailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db_helper = new DatabaseHelper(this);

        //بستقبل ال id تبع الوجبة الي على الكرت
        mealId = getIntent().getIntExtra("meal_id", -1);

        if (mealId != -1) {
            // جيب بيانات الوجبة
            meal = db_helper.getMealById(mealId);
            if (meal != null) {
                // اعرضها في عناصر ال xml
                displayMealDetails();
            } else {
                Toast.makeText(this, "Meal details not found!", Toast.LENGTH_SHORT).show();
            }
        }

        // زر الرجوع
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //زر المشاركة
        binding.imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meal != null) {
                    // بجهز الرسالة الي بدي ارسلها
                    String shareMessage = "🍽️ Recipe: " + meal.getTitle() + "\n\n" +
                            "🕒 Time: " + meal.getCookTime() + " mins\n" +
                            "🔥 Calories: " + meal.getCalories() + " kcal\n\n" +
                            "🍎 Ingredients:\n" + meal.getIngredients() + "\n\n" +
                            "📝 Instructions:\n" + meal.getInstructions();

                    //  هاد implicit intent خاص بالشير
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

                    // هان بنفتح فائمة التطبيقات
                    startActivity(Intent.createChooser(shareIntent, "Share recipe via:"));
                }
            }
        });

        binding.fabAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (meal != null) {
                    String[] daysArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                    String[] categoriesArray = {"Breakfast", "Lunch", "Dinner", "Snacks", "Desserts"};
                    showAddToPlanDialog(meal, daysArray, categoriesArray);
                } else {
                    Toast.makeText(Meal_Deatails.this, "No meal data available to add!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        // هاد الدالة الي هتوزع البيانات على عناصر الxml
        private void displayMealDetails() {
            binding.tvMealTitle.setText(meal.getTitle());
            binding.tvMealTime.setText("🕒 " + meal.getCookTime() + " mins");
            binding.tvMealCalori.setText("🔥 " + meal.getCalories() + " kcal");
            binding.tvInstructionsDetails.setText(meal.getInstructions());
            binding.tvProtein.setText(meal.getProtein() + " g");
            binding.tvCarbs.setText(meal.getCarbs() + " g");

            // ️ هان بنعرض صورة الوجبة
            if (meal.getImagePath() != null && !meal.getImagePath().isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(meal.getImagePath());
                    binding.imgMealPic.setImageURI(imageUri);
                } catch (Exception e) {
                    binding.imgMealPic.setImageResource(android.R.drawable.sym_def_app_icon);
                }
            }

            //  هان بنجيب المكونات وبنقطعها ونعرضها في الـ RecyclerView
            String rawIngredients = meal.getIngredients();
            if (rawIngredients != null && !rawIngredients.isEmpty()) {
                // بنقطع النص عند كل فاصلة عشان نعمل لستة بالمكونات
                String[] ingredientsArray = rawIngredients.split(",");
                //بعمل لستة جديدة عشان اخزن فيها المكونات المقصوصة
                ArrayList<String> list = new ArrayList<>();
                for (String item : ingredientsArray) {
                    list.add(item);
                }

                // طريقة العرض عمودية للريسايكل
                binding.rvIngredients.setLayoutManager(new LinearLayoutManager(this));

                IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(this, list);
                binding.rvIngredients.setAdapter(ingredientsAdapter);
            }
        }

    // هاد الديالج الي بسألني في يوم شو اضيف ولاي وجبة
    private void showAddToPlanDialog(Meal meal, String[] daysArray, String[] categoriesArray) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add \"" + meal.getTitle() + "\" to Plan");
        builder.setMessage("Please fill the fields");
        builder.setCancelable(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View alertView = inflater.inflate(R.layout.dialog_add_to_plan, null);
        builder.setView(alertView);

        Spinner spinnerDays = alertView.findViewById(R.id.sp_dialog_days);
        Spinner spinnerCategories = alertView.findViewById(R.id.sp_dialog_categories);

        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysArray);
        spinnerDays.setAdapter(daysAdapter);

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoriesArray);
        spinnerCategories.setAdapter(categoriesAdapter);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selectedDay = spinnerDays.getSelectedItem().toString();
                String selectedCategory = spinnerCategories.getSelectedItem().toString();

                android.content.SharedPreferences preferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                int userId = preferences.getInt("user_id", -1);

                DatabaseHelper db_helper = new DatabaseHelper(Meal_Deatails.this);
                boolean isInserted = db_helper.insertToPlan(userId, meal.getId(), selectedDay, selectedCategory);

                if (isInserted) {
                    Toast.makeText(Meal_Deatails.this, "Added to your weekly plan!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Meal_Deatails.this, "Failed to add to plan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Meal_Deatails.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}