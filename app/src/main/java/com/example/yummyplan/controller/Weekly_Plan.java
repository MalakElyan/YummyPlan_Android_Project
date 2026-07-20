package com.example.yummyplan.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.databinding.ActivityWeeklyPlanBinding;
import com.example.yummyplan.model.Meal;
import com.example.yummyplan.view.WeeklyMealAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Weekly_Plan extends AppCompatActivity {

    private ActivityWeeklyPlanBinding binding;
    private DatabaseHelper db_helper;
    private int userId;
    private String currentDayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyPlanBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db_helper = new DatabaseHelper(this);

        // بنستقبل الـ id للمستخدم الحالي
        SharedPreferences preferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        // هاد عشان اجيب تاريخ اليوم في الجهاز
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        currentDayName = sdf.format(new Date());

        // طريقة العرض عمودية لكل الأقسام الأربعة
        binding.rvBreakFast.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLunch.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDinner.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSnacks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDesserts.setLayoutManager(new LinearLayoutManager(this));


        binding.cvSun.setOnClickListener(v -> filterMealsByDay("Sunday"));
        binding.cvMon.setOnClickListener(v -> filterMealsByDay("Monday"));
        binding.cvTue.setOnClickListener(v -> filterMealsByDay("Tuesday"));
        binding.cvWed.setOnClickListener(v -> filterMealsByDay("Wednesday"));
        binding.cvThu.setOnClickListener(v -> filterMealsByDay("Thursday"));
        binding.cvFri.setOnClickListener(v -> filterMealsByDay("Friday"));
        binding.cvSat.setOnClickListener(v -> filterMealsByDay("Saturday"));

        // زر العودة للخلف
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // زر الـ FAB بنقلني لشاشة الميلز عشان اضيف وجبات
        binding.fabAddWeeklyMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Weekly_Plan.this, Meal_list.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterMealsByDay(currentDayName);    }

    // دالة بتجيب الوجبات المرتبطة باليوم المحدد وبتفلترها للأقسام الأربعة
    private void filterMealsByDay(String dayName) {
        // هان جيب كل وجبات هاد اليوم لهاد المستخدم من الداتابيز
        ArrayList<Meal> resultList = db_helper.getMealsByUserAndDay(userId, dayName);

        // هان بنحمي اللستة إذا رجعت فارغة
        if (resultList == null) {
            resultList = new ArrayList<>();
        }

        // لستات فرعية عشان نقسم الوجبات حسب القسم
        ArrayList<Meal> breakfastMeals = new ArrayList<>();
        ArrayList<Meal> lunchMeals = new ArrayList<>();
        ArrayList<Meal> dinnerMeals = new ArrayList<>();
        ArrayList<Meal> snacksMeals = new ArrayList<>();
        ArrayList<Meal> dessertsMeals = new ArrayList<>();

        // بنفلتر اللستة وبنوزعها حسب الـ Category
        for (int i = 0; i < resultList.size(); i++) {
            Meal meal = resultList.get(i);
            if (meal.getCategory().equals("Breakfast")) {
                breakfastMeals.add(meal);
            } else if (meal.getCategory().equals("Lunch")) {
                lunchMeals.add(meal);
            } else if (meal.getCategory().equals("Dinner")) {
                dinnerMeals.add(meal);
            } else if (meal.getCategory().equals("Snacks")) {
                snacksMeals.add(meal);
            } else if (meal.getCategory().equals("Desserts")) {
                dessertsMeals.add(meal);
            }
        }

        int defaultCardColor = android.graphics.Color.parseColor("#FFFFFF");

        binding.cvSun.setCardBackgroundColor(defaultCardColor);
        binding.cvMon.setCardBackgroundColor(defaultCardColor);
        binding.cvTue.setCardBackgroundColor(defaultCardColor);
        binding.cvWed.setCardBackgroundColor(defaultCardColor);
        binding.cvThu.setCardBackgroundColor(defaultCardColor);
        binding.cvFri.setCardBackgroundColor(defaultCardColor);
        binding.cvSat.setCardBackgroundColor(defaultCardColor);

        int activeColor = android.graphics.Color.parseColor("#27ae60");
        switch (dayName) {
            case "Sunday": binding.cvSun.setCardBackgroundColor(activeColor); break;
            case "Monday": binding.cvMon.setCardBackgroundColor(activeColor); break;
            case "Tuesday": binding.cvTue.setCardBackgroundColor(activeColor); break;
            case "Wednesday": binding.cvWed.setCardBackgroundColor(activeColor); break;
            case "Thursday": binding.cvThu.setCardBackgroundColor(activeColor); break;
            case "Friday": binding.cvFri.setCardBackgroundColor(activeColor); break;
            case "Saturday": binding.cvSat.setCardBackgroundColor(activeColor); break;
        }

        // بنركب الأدابتر وبنمرر البيانات المفلترة بالزبط لكل ريسايكلر فيو
        binding.rvBreakFast.setAdapter(new WeeklyMealAdapter(this, breakfastMeals, userId, dayName));
        binding.rvLunch.setAdapter(new WeeklyMealAdapter(this, lunchMeals, userId, dayName));
        binding.rvDinner.setAdapter(new WeeklyMealAdapter(this, dinnerMeals, userId, dayName));
        binding.rvSnacks.setAdapter(new WeeklyMealAdapter(this, snacksMeals, userId, dayName));
        binding.rvDesserts.setAdapter(new WeeklyMealAdapter(this, dessertsMeals, userId, dayName));

    }
}
