package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.databinding.ActivityAdminDashboardBinding;
import com.example.yummyplan.model.User;
import com.example.yummyplan.view.RecentUsersAdapter;

import java.util.ArrayList;

public class Admin_Dashboard extends AppCompatActivity {

    private ActivityAdminDashboardBinding binding;
    private RecyclerView rv_RecentUsers;
    private DatabaseHelper db_helper;
    private RecentUsersAdapter adapter;
    private TextView tv_TotalUsersCount, tv_TotalMealsCount;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        rv_RecentUsers = binding.rvRecentUsers;
        tv_TotalUsersCount = binding.tvTotalUsersCount;
        tv_TotalMealsCount = binding.tvTotalMealsCount;

        db_helper = new DatabaseHelper(this);

        //جبت الid من الشيرد عشان اجيب صورة البروفايل
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);

        // هان جبنا اخر 5 مستخدمين واعطيناهم للادابتر وحددنا طريقة العرض انه عمودي بعدين اعطينا الادابتر للريسايكل فيو تعرض بياناته
        ArrayList<User> recentList = (ArrayList<User>) db_helper.getRecentUsers();
        adapter = new RecentUsersAdapter(this, recentList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv_RecentUsers.setLayoutManager(lm);
        rv_RecentUsers.setAdapter(adapter);


        // زر ال Add
        binding.btnAddMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Dashboard.this, Add_or_Edit_Meal.class);
                intent.putExtra("action_type", "add");
                startActivity(intent);
                }
        });


        // زر ال viewAll
        binding.tvViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Dashboard.this, Activity_All_Users.class);
                startActivity(intent);
            }
        });

         //زر ال meals في البار الي تحت
        binding.btnMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Dashboard.this, Meal_list.class);
                startActivity(intent);
            }
        });

        //زر الprofile في البار الي تحت
        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Admin_Dashboard.this, Profile.class);
                startActivity(intent);
            }
        });
    }
    //كتبنا هاد الدالة عشان تحدث عدد الوجبات لما نرجع من شاشة اضافة وجبة
    @Override
    protected void onResume() {
        super.onResume();
       //جبنا عدد المستخدمين وعدد الوجبات باستخدام الدوال الي كتبناها في الهلبر
        int totalUsers = db_helper.getTotalUsersCount();
        int totalMeals = db_helper.getTotalMealsCount();

        tv_TotalUsersCount.setText(String.valueOf(totalUsers));
        tv_TotalMealsCount.setText(String.valueOf(totalMeals));

        // بجيب الصورة وبعرضها
        User currentAdmin = db_helper.getUserById(userId);
        if (currentAdmin != null && currentAdmin.getUser_img() != null ) {
            binding.imgAvtarPicture.setImageURI(Uri.parse(currentAdmin.getUser_img()));
        }
    }
}
