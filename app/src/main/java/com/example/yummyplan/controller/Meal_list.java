package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.databinding.ActivityMealListBinding;
import com.example.yummyplan.model.Meal;
import com.example.yummyplan.view.ExploreMealsAdapter;

import java.util.ArrayList;

public class Meal_list extends AppCompatActivity {

    private ActivityMealListBinding binding;
    private DatabaseHelper db_helper;
    private ExploreMealsAdapter adapter;
    private String userRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMealListBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);


        db_helper = new DatabaseHelper(this);

        //بستقبل نوع المستخدم
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userRole = preferences.getString("user_role", "user");

        //طريقة العرض عمودية
        binding.rvExploreMeals.setLayoutManager(new LinearLayoutManager(this));

        binding.tvChipGeneral.setOnClickListener(v -> filterMealsByDiet("All"));
        binding.tvChipVegetarian.setOnClickListener(v -> filterMealsByDiet("Vegetarian"));
        binding.tvChipVegan.setOnClickListener(v -> filterMealsByDiet("Vegan"));
        binding.tvChipHighProtein.setOnClickListener(v -> filterMealsByDiet("High Protein"));
        binding.tvChipLowCarb.setOnClickListener(v -> filterMealsByDiet("Low Carb"));
        binding.tvChipKeto.setOnClickListener(v -> filterMealsByDiet("Keto"));
        binding.tvChipGlutenFree.setOnClickListener(v -> filterMealsByDiet("Gluten-Free"));


        //زر الdashboard في البار الي تحت
        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;

                if (userRole != null && userRole.equalsIgnoreCase("admin")) {
                    intent = new Intent(Meal_list.this, Admin_Dashboard.class);
                } else {
                    intent = new Intent(Meal_list.this, User_dashboard.class);
                }
                startActivity(intent);
                finish();
            }
        });


        //زر الprofile في البار الي تحت
        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Meal_list.this, Profile.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        filterMealsByDiet("All");
    }
    //دالة هتجبلي الوجبات المرتبطة بنوع الدايت
    private void filterMealsByDiet(String dietType) {

        ArrayList<Meal> resultList;

        if (dietType.equals("All")) {
            // جيب كل الوجبات
            resultList = db_helper.getAllMeals();
        } else {
            // جيب الوجبات حسب نوع الدايت من الداتابيز
            resultList = db_helper.getMealsByDietType(dietType);
        }

       // مررنا الuser role لان في عناصر هظرهها او اخفيها في كارد الريسايكل حسب الuser role
        adapter = new ExploreMealsAdapter(this, resultList, userRole);
        binding.rvExploreMeals.setAdapter(adapter);
    }

}


