package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.databinding.ActivityMainBinding;
import com.example.yummyplan.model.User;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper db_helper;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        db_helper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        editor = preferences.edit();


        // هدول عشان لو طلع من الببرنامج ورجع وكان معبي حقول يرجع يلاقيها معبية
        binding.etEmail.setText(preferences.getString("username", ""));
        binding.cbRemember.setChecked(preferences.getBoolean("remember_state", false));

        // هان انتقال عادي لو ضغطت على نص الregister
        binding.tvRigister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, YummyPlan_Rigister.class);
                startActivity(intent);
            }
        });


        // هاد زر ال login
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();

                // هان تحقق انه المستخدم عبى كل الحقول
                if (email.isEmpty()) {
                    binding.etEmail.setError("Email is required!");
                    binding.etEmail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    binding.etPassword.setError("Password is required!");
                    binding.etPassword.requestFocus();
                    return;
                }

                //  ًاستعلام من الداتابيز عشان نعرف هل الإيميل موجود أصلا
                String correctPassword = db_helper.getPasswordByEmail(email);

                if (correctPassword == null) {
                    // الإيميل مش موجود بالمرة في الداتابيز
                    binding.etEmail.setError("This email address is not registered!");
                    binding.etEmail.requestFocus();
                    //  الإيميل موجود هلحين بنفحص هل الباسوورد المكتوب بطابق الباسوورد الصح
                } else if (correctPassword.equals(password)) {
                    // هان استدعينا دالة التحقق الي كتبناها في الهلبر عشان بدنا نتحقققققق
                    User logged_in_user = db_helper.checkUserLogin(email, password);
                    // هاد فحص هل لقينا مستخدم بياناته مطابقة لهاد ولا لا
                    if (logged_in_user != null) {
                        // هان طب هل ضغط على تذكرني اذا اه روح يا editor احفظلي بياناته وحالة الزر كمان
                        if (binding.cbRemember.isChecked()) {
                            editor.putString("username", email);
                            editor.putBoolean("remember_state", true);
                            editor.apply();
                        } else {
                            // اذا ما ضغط بروح بحذف اي بيانات تخزنت لانه ما بده احفظ بياناته
                            editor.remove("username");
                            editor.putBoolean("remember_state", false);
                            editor.apply();
                        }

                        // هان اذا المستخدم سجل دخول وبنجاح بحفظ بياناته لانه بدي اياها في الداشبورد وتسجيل الوجبات وهيك
                        editor.putInt("user_id", logged_in_user.getId());
                        editor.putString("user_name", logged_in_user.getFullName());
                        editor.putString("user_email", logged_in_user.getEmail());
                        editor.putString("user_role", logged_in_user.getRole());
                        editor.putBoolean("is_logged_in", true);
                        editor.apply();

                        // هان انتقال عادي
                        Intent intent;
                        if (logged_in_user.getRole().equals("admin")) {
                            intent = new Intent(MainActivity.this, Admin_Dashboard.class);
                        } else {
                            intent = new Intent(MainActivity.this, User_dashboard.class);
                        }
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // هان حالة لو الإيميل صح بس الباسوورد غلط، بنطلع تنبيه عالحقل
                    binding.etPassword.setError("Incorrect Password!");
                    binding.etPassword.requestFocus();
                }
            }
        });


        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "This feature is under development and will be available in future updates.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.etEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your email address first to retrieve your password.", Toast.LENGTH_SHORT).show();
                } else {
                    // هان بستدعي الدالة الي بتجيب الباسوورد
                    String password = db_helper.getPasswordByEmail(email);

                    if (password != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Password Recovery");
                        builder.setMessage("Your password is: " + password);
                        builder.setPositiveButton("OK", null);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(MainActivity.this, "This email address is not registered!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}

