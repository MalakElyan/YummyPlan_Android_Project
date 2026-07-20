package com.example.yummyplan.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.R;
import com.example.yummyplan.databinding.ActivityProfileBinding;
import com.example.yummyplan.model.User;

import java.io.File;
import java.io.FileOutputStream;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private DatabaseHelper db_helper;
    private User currentUser;
    private int userId;
    private String userRole;
    private String selectedImagePath = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        db_helper = new DatabaseHelper(this);

        if (savedInstanceState != null) {
            selectedImagePath = savedInstanceState.getString("temp_image_path", "");
            if (!selectedImagePath.isEmpty()) {
                Uri imageUri = Uri.parse(selectedImagePath);
                binding.imgProfilepicture.setImageURI(imageUri);
                binding.imgAvtarPicture.setImageURI(imageUri);
            }
        }

        // بجيب البيانات مباشرة من الشيردبرفرينس
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("user_id", -1);
        userRole = preferences.getString("user_role", "user");

        loadUserData();

        // بجيب الصورة عند الضغط على القلم عن صورة البروفايل
        binding.cardEditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //بعطيه المكان الي بدي أبني فيه التنبيه
                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setCancelable(false);

                // بعمل inflater عشان يعرضلي تصميمي الي عملته للتنبيه
                LayoutInflater inflater = LayoutInflater.from(Profile.this);
                View alertView = inflater.inflate(R.layout.dialog_select_photo, null);
                builder.setView(alertView);


                final AlertDialog alertDialog = builder.create();
                View llCamera = alertView.findViewById(R.id.ll_choose_camera);
                View llGallery = alertView.findViewById(R.id.ll_choose_gallery);

                // نقرة الكاميرا
                llCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        launcherCamera.launch(intent);
                        alertDialog.dismiss();
                    }
                });

                // نقرة المعرض
                llGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        launcherGallery.launch(intent);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        // زر الحفظ
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileData();
            }
        });

        // زر تسجيل الخروج
        binding.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove("user_id");
                editor.remove("user_name");
                editor.remove("user_email");
                editor.remove("user_role");
                editor.putBoolean("is_logged_in", false);
                editor.apply();

                Intent intent = new Intent(Profile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // زر الـ Home
        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (userRole.equals("admin")) {
                    intent = new Intent(Profile.this, Admin_Dashboard.class);
                } else {
                    intent = new Intent(Profile.this, User_dashboard.class);
                }
                startActivity(intent);
                finish();
            }
        });

        // زر الـ Meals
        binding.btnMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Meal_list.class);
                intent.putExtra("user_role", userRole);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadUserData() {
        currentUser = db_helper.getUserById(userId);

        if (currentUser != null) {
            binding.etFullname.setText(currentUser.getFullName());
            binding.etEmail.setText(currentUser.getEmail());
            binding.tvUsername.setText(currentUser.getFullName());
            binding.etPassword.setText("");

            if (currentUser.getUser_img() != null && !currentUser.getUser_img().isEmpty()) {
                Uri imageUri = Uri.parse(currentUser.getUser_img());
                binding.imgProfilepicture.setImageURI(imageUri);
                binding.imgAvtarPicture.setImageURI(imageUri);
                }
            }
        }


    // دالة حفظ التعديلات
    private void updateProfileData() {
        String updatedName = binding.etFullname.getText().toString();
        String updatedEmail = binding.etEmail.getText().toString();
        String newPassword = binding.etPassword.getText().toString();

        if (updatedName.isEmpty() || updatedEmail.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return;
        }
        //هان بحدث الاسم و الايميل
        currentUser.setFullName(updatedName);
        currentUser.setEmail(updatedEmail);

        // اذا غير الصورة بحدثها في الاوبجكت
        if (!selectedImagePath.isEmpty()) {
            currentUser.setUser_img(selectedImagePath);
        }

        //هان بشوف اذا ما غير الباسوورد بخلي القديم اذا غيره بتحدث تحت
        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword);
        }

        int result = db_helper.updateUserProfile(currentUser);
        if (result > 0) {
            //هاد للتكست فيو الي في اعلى صفحة البروفايل
            binding.tvUsername.setText(updatedName);
            // هان بفضي الباسوورد بعد الجفظ
            binding.etPassword.setText("");
            selectedImagePath = "";
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
        }
    }

    //ARL للمعرض
    private final ActivityResultLauncher<Intent> launcherGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            binding.imgProfilepicture.setImageURI(uri);
                            binding.imgAvtarPicture.setImageURI(uri);

                            // هان بنطلب من نظام الأندرويد صلاحية قراءة دائمة لرابط الصورة
                            try {
                                getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } catch (SecurityException e) {
                                e.printStackTrace();
                            }
                            //هان بنحول الرابط تبع الصورة لنص عشان نخزنه في قاعدة البيانات كالعادة
                            selectedImagePath = uri.toString();
                        }
                    }
                }
            }
    );

    // ARL الكاميرا
    private final ActivityResultLauncher<Intent> launcherCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle b = result.getData().getExtras();
                        if (b != null) {
                            Bitmap bitmap = (Bitmap) b.get("data");
                            binding.imgProfilepicture.setImageBitmap(bitmap);
                            binding.imgAvtarPicture.setImageBitmap(bitmap);

                            try {
                                //بننشئ ملف فارغ باسم فريد من نوعه بداخل الكاش تبعت التطبيق
                                File file = new File(getCacheDir(), "meal_cam_" + System.currentTimeMillis() + ".jpg");
                                // بفتح طريق عشان اكتب في هاد الملف
                                FileOutputStream out = new FileOutputStream(file);
                                //  بنضغط الـ Bitmap وبنحفظه داخل الملف
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                out.close();
                                // بنحول الملف لuri ثم لنص عشان نحفظه في قاعدة البيانات
                                selectedImagePath = Uri.fromFile(file).toString();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // بنحفظ مسار الصورة المختارة حالياً والي لسا ما انحفظ في الداتابيز
        outState.putString("temp_image_path", selectedImagePath);
    }

}