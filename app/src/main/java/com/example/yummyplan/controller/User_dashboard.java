package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.databinding.ActivityUserDashboardBinding;
import com.example.yummyplan.model.Meal;
import com.example.yummyplan.model.User;
import com.example.yummyplan.view.HealthyTipsAdapter;
import com.example.yummyplan.view.TodayMealsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class User_dashboard extends AppCompatActivity {

    private ActivityUserDashboardBinding binding;
    private DatabaseHelper db_helper;
    private TodayMealsAdapter adapter;
    private int userId;
    private String currentDayName;
    private ArrayList<Meal> todayMealsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db_helper = new DatabaseHelper(this);

        // بنجيب id المستخدم من السيرد
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        //هاد عشان اجيب تاريخ اليوم في الجهاز
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        currentDayName = sdf.format(new Date());

        // هاد ريسايكل النصايح
        binding.rvHealthyTips.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadHealthyTips();

        //وهاد لريسايكل وجبات اليوم
        binding.rvTodayMeals.setLayoutManager(new LinearLayoutManager(this));
        loadTodayData();

        binding.tvChipAllRecipes.setOnClickListener(v -> filterTodayMeals("All", binding.tvChipAllRecipes));
        binding.tvBreakFast.setOnClickListener(v -> filterTodayMeals("Breakfast", binding.tvBreakFast));
        binding.tvLunch.setOnClickListener(v -> filterTodayMeals("Lunch", binding.tvLunch));
        binding.tvDinner.setOnClickListener(v -> filterTodayMeals("Dinner", binding.tvDinner));
        binding.tvSnacks.setOnClickListener(v -> filterTodayMeals("Snacks", binding.tvSnacks));
        binding.tvDeserts.setOnClickListener(v -> filterTodayMeals("Desserts", binding.tvDeserts));


        binding.btnMeals.setOnClickListener(v -> {
            Intent intent = new Intent(User_dashboard.this, Meal_list.class);
            intent.putExtra("user_role", "user");
            startActivity(intent);
        });

        binding.btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(User_dashboard.this, Profile.class);
            startActivity(intent);
        });

        binding.fabWeeklyMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(User_dashboard.this, Weekly_Plan.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        //هان بنجيب صورة المستخدم وبنرجب فيه
        User currentUser = db_helper.getUserById(userId);
        if (currentUser != null) {
            binding.tvWelcomeUser.setText("Hello, " + currentUser.getFullName() + "!");
            if (currentUser.getUser_img() != null) {
                binding.imgAvtarPicture.setImageURI(Uri.parse(currentUser.getUser_img()));
            }
        }
        //بنستدعي دالة الفلترة تبعت الشيبس
        loadTodayData();
    }

    // دالة لاحصائيات الكروت
    private void updateDashboardStats(ArrayList<Meal> meals) {
        int totalMealsCount = meals.size();
        int totalCalories = 0;

        // حساب مجموع السعرات الحرارية للوجبات المضافة اليوم
        for (Meal meal : meals) {
            totalCalories += meal.getCalories();
        }

        binding.tvTotalMealsCount.setText(String.valueOf(totalMealsCount));
        binding.tvTodayCaloriesCount.setText(totalCalories + " kcal");
    }

    private void loadHealthyTips() {
        ArrayList<String> tipsList = new ArrayList<>();

        tipsList.add("Drink More Water // Try to drink at least 8 glasses of water today to keep your energy levels high and support digestion.");
        tipsList.add("Eat Green Veggies // Add green veggies to your lunch today for extra fiber and essential vitamins.");
        tipsList.add("Less Sugar // Avoid processed sugar today and prefer whole fresh fruits instead.");

        HealthyTipsAdapter tipsAdapter = new HealthyTipsAdapter(this, tipsList);
        binding.rvHealthyTips.setAdapter(tipsAdapter);
    }

    // دالة بتجيب وجبات اليوم هاد للمستخدم من الداتابيز لأول مرة
    private void loadTodayData() {
        // بنجيب كل الوجبات لليوم الحالي
        todayMealsList = db_helper.getMealsByUserAndDay(userId, currentDayName);
        // هاد عشان لو المستخدم كان داخل لاول مرة ومش ضايف وجبات هتكون نتيجة اللست null وهيصير كراش و يسكر التطبيق
        if (todayMealsList == null) {
            todayMealsList = new ArrayList<>();
        }

        // بنعرضهم بشكل افتراضي (All Recipes) أول ما تفتح الشاشة
        filterTodayMeals("All", binding.tvChipAllRecipes);
        updateDashboardStats(todayMealsList);
    }

    private void filterTodayMeals(String category, TextView selectedView) {

        // بلون كل الأزرار بالرمادي الافتراضي
        binding.tvChipAllRecipes.setTextColor(android.graphics.Color.parseColor("#1a1a1a"));
        binding.tvChipAllRecipes.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D7D7D7")));

        binding.tvBreakFast.setTextColor(android.graphics.Color.parseColor("#1a1a1a"));
        binding.tvBreakFast.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D7D7D7")));

        binding.tvLunch.setTextColor(android.graphics.Color.parseColor("#1a1a1a"));
        binding.tvLunch.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D7D7D7")));

        binding.tvDinner.setTextColor(android.graphics.Color.parseColor("#1a1a1a"));
        binding.tvDinner.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D7D7D7")));

        binding.tvSnacks.setTextColor(android.graphics.Color.parseColor("#1a1a1a"));
        binding.tvSnacks.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D7D7D7")));

        binding.tvDeserts.setTextColor(android.graphics.Color.parseColor("#1a1a1a"));
        binding.tvDeserts.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D7D7D7")));

        // بلون الزر والنص الي نقرنا عليهم بالأخضر والأبيض
        selectedView.setTextColor(android.graphics.Color.WHITE);
        selectedView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#27ae60")));

        // لستة بنخزن فيها الوجبات المفلترة لليوم الحالي فقط
        ArrayList<Meal> filteredList = new ArrayList<>();

        if (category.equals("All")) {
            // إذا اختار الكل بنعطيه كل قائمة اليوم
            filteredList = todayMealsList;
        } else {
            // بنمشي على قائمة اليوم وبناخد بس اللي بطابق التصنيف فطور غدا تحلاية وهيك
            for (Meal meal : todayMealsList) {
                if (meal.getCategory().equals(category)) {
                    filteredList.add(meal);
                }
            }
        }

        adapter = new TodayMealsAdapter(this, filteredList);
        binding.rvTodayMeals.setAdapter(adapter);
    }
}


