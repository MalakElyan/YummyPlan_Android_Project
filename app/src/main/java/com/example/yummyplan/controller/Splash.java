package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.yummyplan.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // هان بفحص هل المستخدم مسجل دخول ولا لا من الشيرد طبعا
                SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);
                String role = preferences.getString("user_role", "user");

                Intent intent;

                if (isLoggedIn) {
                    // إذا كان مسجل دخول بنروح للداشبورد تبعته مباشرة
                    if (role.equals("admin")) {
                        intent = new Intent(Splash.this, Admin_Dashboard.class);
                    } else {
                        intent = new Intent(Splash.this, User_dashboard.class);
                    }
                } else {
                    // إذا كان مستخدم جديد بنروح للرجستر
                    intent = new Intent(Splash.this, YummyPlan_Rigister.class);
                }

                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}