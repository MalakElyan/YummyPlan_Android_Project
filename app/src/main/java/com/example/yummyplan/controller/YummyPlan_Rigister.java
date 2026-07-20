package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.R;
import com.example.yummyplan.databinding.ActivityYummyPlanRigisterBinding;
import com.example.yummyplan.model.User;

public class YummyPlan_Rigister extends AppCompatActivity {

    private ActivityYummyPlanRigisterBinding binding;
    private DatabaseHelper db_helper;
    // هان عرفت متغير عشان يخزن داخل كأي مستخدم والافتراضي انه مستخدم عادي
    private String selectedRole = "user";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityYummyPlanRigisterBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);
        // أنشأنا نسخة للهيلبر مررنا الشاشة الحالية (Context) كإذن للوصول لملفات الذاكرة
        db_helper = new DatabaseHelper(this);

        if (savedInstanceState != null) {
            // بنجيب الدور الي انحفظ لما سجلنا
            selectedRole = savedInstanceState.getString("saved_role", "user");
        }

        // هان بنفحص شو الدور ونلون الأزرار بناءً عليه عشان لو لفينا الشاشة يرجع اللون صح
        if (selectedRole.equals("admin")) {
                binding.btnAdmin.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.secondary_green));
                binding.btnAdmin.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, android.R.color.white));

                binding.btnRegular.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.light_gray));
                binding.btnRegular.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.dark_gray));
        } else {
            binding.btnRegular.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.secondary_green));
            binding.btnRegular.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, android.R.color.white));

            binding.btnAdmin.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.light_gray));
            binding.btnAdmin.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.dark_gray));
        }

        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String draftName = preferences.getString("draft_name", "");
        String draftEmail = preferences.getString("draft_email", "");
        String draftBirth = preferences.getString("draft_birth", "");

        if (!draftName.isEmpty()) binding.etFullName.setText(draftName);
        if (!draftEmail.isEmpty()) binding.etEmail.setText(draftEmail);
        if (!draftBirth.isEmpty()) binding.etBirthDate.setText(draftBirth);

        editor = preferences.edit();

        // هان بنفحص هل المستخدم مسجل دخول
        boolean isLoggedIn = preferences.getBoolean("is_logged_in", false);

        if (isLoggedIn) {
            String role = preferences.getString("user_role", "user");
            Intent intent;

            // هان بنوجهه مباشرة للداشبورد المناسبة إله بدون ما يشوف شاشة الـ Login
            if (role.equals("admin")) {
                intent = new Intent(YummyPlan_Rigister.this, Admin_Dashboard.class);
            } else {
                intent = new Intent(YummyPlan_Rigister.this, User_dashboard.class);
            }
            startActivity(intent);
            finish(); // بنسكر المين عشان لو ضغط رجوع ما يرجع للـ login
            return;  // بنوقف تنفيذ باقي أسطر الدالة لأنه خلص انتقل
        }

        // هاد الأكواد هان بس عشان نغير الون الازرار لما نضغط عليههم
        binding.btnRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "user";
                // استخدمنا ContextCompat لانه دالة ال get color صارت Deprecated
                binding.btnRegular.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.secondary_green));
                binding.btnRegular.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, android.R.color.white));

                binding.btnAdmin.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.light_gray));
                binding.btnAdmin.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.dark_gray));
            }
        });

        binding.btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRole = "admin";
                binding.btnAdmin.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.secondary_green));
                binding.btnAdmin.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, android.R.color.white));

                binding.btnRegular.setBackgroundColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.light_gray));
                binding.btnRegular.setTextColor(ContextCompat.getColor(YummyPlan_Rigister.this, R.color.dark_gray));
            }
        });

        // هان عملت انتنت عشان ينقلني لل login لما اضغط على نص ال login
        binding.tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YummyPlan_Rigister.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = binding.etFullName.getText().toString().trim();
                String email = binding.etEmail.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                String birthDate = binding.etBirthDate.getText().toString().trim();

                //هان عملت تحقق انه كل الحقول معبية عشان أكون ماشية صح وما ينهار البرنامج
                if (fullName.isEmpty()) {
                    binding.etFullName.setError("Full name is required!");
                    binding.etFullName.requestFocus();
                    return;
                }

                //  بنتحقق من صيغة الإيميل يكون بيحتوي @ و .com
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (email.isEmpty()) {
                    binding.etEmail.setError("Email is required!");
                    binding.etEmail.requestFocus();
                    return;
                } else if (!email.matches(emailPattern)) {
                    binding.etEmail.setError("Please enter a valid email address! (e.g., example@mail.com)");
                    binding.etEmail.requestFocus();
                    return;
                }

                //  بنتحقق من قوة الباسوورد ما يكون اقل من 6 خانات
                if (password.isEmpty()) {
                    binding.etPassword.setError("Password is required!");
                    binding.etPassword.requestFocus();
                    return;
                } else if (password.length() < 6) {
                    binding.etPassword.setError("Password must be at least 6 characters long!");
                    binding.etPassword.requestFocus();
                    return;
                }

                // بنتحقق من صيغة التاريخ اذا النمط بيدعم صيغ مثل 1999-12-30 أو 30/12/1999 أو 30-12-1999
                String datePattern = "^\\d{2}[/.-]\\d{2}[/.-]\\d{4}$|^\\d{4}[/.-]\\d{2}[/.-]\\d{2}$";
                if (birthDate.isEmpty()) {
                    binding.etBirthDate.setError("Birth date is required!");
                    binding.etBirthDate.requestFocus();
                    return;
                } else if (!birthDate.matches(datePattern)) {
                    binding.etBirthDate.setError("Enter a valid date format! (e.g., DD/MM/YYYY)");
                    binding.etBirthDate.requestFocus();
                    return;
                }
                // هان بجمع كل البيانات الي انكتبت فوق وبخزنها في اوبجكت واحد
                User newUser = new User(fullName, email, password, birthDate, selectedRole);
                // هان بنحفظ الاوبجكت عن طريق دالة الinsert الي كتبناها مسبقا وبحفظ النتيجة الي بترجعها الدالة في long لانها اصلا بترجع long عشان اعمل التحقق الي تحت
                long userId = db_helper.insertUser(newUser);

                if (userId != -1) {
                    // بنمسح بيانات المسودة لأن التسجيل نجح خلص فليش تضل معروضة
                    editor.remove("draft_name");
                    editor.remove("draft_email");
                    editor.remove("draft_birth");
                    editor.apply();

                    // بحفظ بيانات تسجيل الدخول في الشيرد
                    editor.putInt("user_id", (int) userId);
                    editor.putString("user_name", fullName);
                    editor.putString("user_email", email);
                    editor.putString("user_role", selectedRole);
                    editor.putBoolean("is_logged_in", true);
                    editor.apply();

                    // بنتقل بناء عالدور المختار
                    Intent intent;
                    if (selectedRole.equals("admin")) {
                        intent = new Intent(YummyPlan_Rigister.this,  Admin_Dashboard.class);
                    } else {
                        intent = new Intent(YummyPlan_Rigister.this, User_dashboard.class);
                    }
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(YummyPlan_Rigister.this, "Registration Failed!", Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(YummyPlan_Rigister.this, "This feature is under development and will be available in future updates.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnApple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(YummyPlan_Rigister.this, "This feature is under development and will be available in future updates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // هان بنقله إذا كانت الشاشة بتسكر نهائياً زي بعد الساين اب ما تحفظ شي
        if (!isFinishing()) {
            editor.putString("draft_name", binding.etFullName.getText().toString());
            editor.putString("draft_email", binding.etEmail.getText().toString());
            editor.putString("draft_birth", binding.etBirthDate.getText().toString());
            editor.apply();
        }
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // هان بنجفظ نوع الحساب المختار حالياً
        outState.putString("saved_role", selectedRole);
    }
}






