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
import com.example.yummyplan.databinding.ActivityAddOrEditMealBinding;
import com.example.yummyplan.model.Meal;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Add_or_Edit_Meal extends AppCompatActivity {

    private ActivityAddOrEditMealBinding binding;
    private DatabaseHelper db_helper;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String actionType;
    private int mealIdToEdit = -1;
    private String selectedImagePath = "image_path";
    private boolean isMealSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrEditMealBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        db_helper = new DatabaseHelper(this);

        // هان بنفحص إذا كان في بيانات محفوظة من قبل (زي لما نلف الشاشة)
        if (savedInstanceState != null) {
            selectedImagePath = savedInstanceState.getString("saved_image_path");
            actionType = savedInstanceState.getString("saved_action_type");
            mealIdToEdit = savedInstanceState.getInt("saved_meal_id");

            // إذا كان في صورة مختارة، بنرجع نعرضها ونخفي النص
            if (selectedImagePath != null && !selectedImagePath.equals("image_path")) {
                binding.imgUploadImg.setImageURI(Uri.parse(selectedImagePath));
                binding.tvTapUpload.setVisibility(View.GONE);
            }
        } else {
            // هان انتنت بستقبل من الداشبورد اني داخل اعدل ولا اضيف في أول مرة تفتح الشاشة بس مش لما تدور كمان
            actionType = getIntent().getStringExtra("action_type");
            mealIdToEdit = getIntent().getIntExtra("meal_id", -1);
        }


        if (actionType != null && actionType.equals("add")) {

            // هان بنبي الشيرد
            preferences = getSharedPreferences("MealDraft", MODE_PRIVATE);
            editor = preferences.edit();

            if (actionType != null && actionType.equals("add")) {
                binding.tvEditMeal.setText("Add Meal");
                binding.btnSaveChanges.setText("Add Meal");

                // هاااان بسترجع المسودة لو ضغطت على زر الرجوع بالغلط
                if (savedInstanceState == null) {
                    binding.etMealTitle.setText(preferences.getString("draft_title", ""));
                    binding.etIngredients.setText(preferences.getString("draft_ingredients", ""));
                    binding.etInstructions.setText(preferences.getString("draft_instructions", ""));
                    binding.etCookTime.setText(preferences.getString("draft_cook", ""));
                    binding.etCalories.setText(preferences.getString("draft_calories", ""));
                    binding.etProtein.setText(preferences.getString("draft_protein", ""));
                    binding.etCarbs.setText(preferences.getString("draft_carbs", ""));
                }
            }

            binding.tvEditMeal.setText("Add Meal");
            binding.btnSaveChanges.setText("Add Meal");
        } else {
            binding.tvEditMeal.setText("Edit Meal");
            binding.btnSaveChanges.setText("Save Changes");

            // إذا كنا في حالة تعديل والجهاز لسا ما لف بنعبي البيانات من قاعدة البيانات
            if (mealIdToEdit != -1 && savedInstanceState == null) {
                fillMealDataForEdit(mealIdToEdit);
            }
        }

        //  زر الرجوع
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // نقرة مربع الصورة عشان نفتح الalert dialog
        binding.clMealPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //بعطيه المكان الي بدي أبني فيه التنبيه
                AlertDialog.Builder builder = new AlertDialog.Builder(Add_or_Edit_Meal.this);
                builder.setCancelable(false);

                // بعمل inflater عشان يعرضلي تصميمي الي عملته للتنبيه
                LayoutInflater inflater = LayoutInflater.from(Add_or_Edit_Meal.this);
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
        //  زر الحفظ والإضافة
        binding.btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveMeal();
            }
        });

        // تفعيل زر الـ AI لقراءة المكونات
        binding.btnScanAi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                launcherOcrGallery.launch(intent);
            }
        });
    }

    // هاد الدالة بعطيها الId وبتجبلي الوجبة وبياناتها وبتعبيها في الواجهة عشان أعدل عليها
    private void fillMealDataForEdit(int id) {
        // هان بعمل اوبجكت وبستقبل الوجبة فيه من قاعدة البيانات عشان افرغ بياناتها في الواجهة
        Meal meal = db_helper.getMealById(id);
        if (meal != null) {
            binding.etMealTitle.setText(meal.getTitle());
            binding.etIngredients.setText(meal.getIngredients());
            binding.etInstructions.setText(meal.getInstructions());
            binding.etCookTime.setText(String.valueOf(meal.getCookTime()));
            binding.etCalories.setText(String.valueOf(meal.getCalories()));
            binding.etProtein.setText(String.valueOf(meal.getProtein()));
            binding.etCarbs.setText(String.valueOf(meal.getCarbs()));

            // هان بجيب صورة الوجبة من قاعدة البيانات
            selectedImagePath = meal.getImagePath();
            if (selectedImagePath != null && !selectedImagePath.equals("image_path")) {
                try {
                    Uri imageUri = Uri.parse(selectedImagePath);
                    binding.imgUploadImg.setImageURI(imageUri);
                    //هاد عشان يخفي نص tap to upload الي في مكان الصورة
                    binding.tvTapUpload.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // هاد اللوب عشان اعبي السبنر بالخيار الي جبناه من قاعدة البيانات
            String savedCategory = meal.getCategory();
            for (int i = 0; i < binding.spCategory.getCount(); i++) {
                // نقارن النص الموجود في السبنر عند الموقع i بالاسم المخزن
                if (binding.spCategory.getItemAtPosition(i).toString().equals(savedCategory)) {
                    binding.spCategory.setSelection(i); //
                    break;
                }
            }

            String savedDiet = meal.getDietType();
            for (int i = 0; i < binding.spDietType.getCount(); i++) {
                if (binding.spDietType.getItemAtPosition(i).toString().equals(savedDiet)) {
                    binding.spDietType.setSelection(i);
                    break;
                }
            }
        }
    }


    // دالةالحفظ سواء بعد الاضافة او التعديل
    private void saveMeal() {
        // هان بجيب القيم من عناصر ال xml
        String title = binding.etMealTitle.getText().toString();
        String category = binding.spCategory.getSelectedItem().toString();
        String dietType = binding.spDietType.getSelectedItem().toString();
        String ingredients = binding.etIngredients.getText().toString();
        String instructions = binding.etInstructions.getText().toString();
        String cookTimeStr = binding.etCookTime.getText().toString();
        String caloriesStr = binding.etCalories.getText().toString();
        String proteinStr = binding.etProtein.getText().toString();
        String carbsStr = binding.etCarbs.getText().toString();


        // هان بتاكد انه كل ال edit text معبية
        if (title.isEmpty()) {
            binding.etMealTitle.setError("Meal title is required");
            binding.etMealTitle.requestFocus();
            return;
        }
        if (ingredients.isEmpty()) {
            binding.etIngredients.setError("Ingredients are required");
            binding.etIngredients.requestFocus();
            return;
        }
        if (instructions.isEmpty()) {
            binding.etInstructions.setError("Instructions are required");
            binding.etInstructions.requestFocus();
            return;
        }

        // وهان بحول النصوص تبعات الأرقام لأرقام
        int cookTime = 0;
        if (!cookTimeStr.isEmpty()) {
            cookTime = Integer.parseInt(cookTimeStr);
        }

        int calories = 0;
        if (!caloriesStr.isEmpty()) {
            calories = Integer.parseInt(caloriesStr);
        }

        int protein = 0;
        if (!proteinStr.isEmpty()) {
            protein = Integer.parseInt(proteinStr);
        }

        int carbs = 0;
        if (!carbsStr.isEmpty()) {
            carbs = Integer.parseInt(carbsStr);
        }


        // هان بأنشئ كائن وبعبي فيه البيانات
        Meal meal = new Meal(mealIdToEdit, title, category, dietType, ingredients, instructions, cookTime, calories, protein,
                carbs, selectedImagePath);

        // هان بفحص نوع العملية
        if ("add".equals(actionType)) {
            long result = db_helper.insertMeal(meal);
            if (result != -1) {
                isMealSaved = true;
                // بمسح المسودة لأن الوجبة انحفظت  خلص في الداتابيز
                editor.clear().apply();
                finish();
            }
        } else if ("edit".equals(actionType)) {
            int result = db_helper.updateMeal(meal);
            if (result > 0) {
                isMealSaved = true;
                finish();
            }
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
                            binding.imgUploadImg.setImageURI(uri);
                            //هاد عشان يخفي نص tap to upload الي في مكان الصورة
                            binding.tvTapUpload.setVisibility(View.GONE);

                            // هان بنطلب من نظام الأندرويد صلاحية قراءة دائمة لرابط الصورة عشان ما يرجعلي صورة الأندرويد الافتراضية
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
                            binding.imgUploadImg.setImageBitmap(bitmap);
                            //هاد عشان يخفي نص tap to upload الي في مكان الصورة
                            binding.tvTapUpload.setVisibility(View.GONE);
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

    // ARL الخاص بمعرض الـ AI لاستخراج نصوص المكونات
    private final ActivityResultLauncher<Intent> launcherOcrGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            // تشغيل الـ AI لقراءة النص مباشرة من الـ Uri
                            runTextRecognitionFromUri(uri);
                        }
                    }
                }
            });

    // دالة معالجة الصورة واستخراج النصوص أوفلاين
    private void runTextRecognitionFromUri(Uri uri) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(this, uri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

            Toast.makeText(this, "Analyzing ingredients...", Toast.LENGTH_SHORT).show();

            recognizer.process(image)
                    .addOnSuccessListener(visionText -> {
                        String processedText = visionText.getText();
                        if (!processedText.isEmpty()) {
                            // بنعبي النص المقروء داخل الحقل تبع للمكونات
                            binding.etIngredients.setText(processedText);
                            Toast.makeText(Add_or_Edit_Meal.this, "Ingredients scanned successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Add_or_Edit_Meal.this, "No text found in the image.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Add_or_Edit_Meal.this, "Failed to scan text: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // بنحفظ رابط الصورة المختار حالياً عشان ما يضيع عند التدوير
        outState.putString("saved_image_path", selectedImagePath);
        //بنحفظ نوع العملية إضافة ولا تعديل
        outState.putString("saved_action_type", actionType);
        outState.putInt("saved_meal_id", mealIdToEdit);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // إذا كنا في حالة إضافة بنحفظ اللي انكتب كمسودة بالشيرد
        if ("add".equals(actionType) && editor != null && !isMealSaved) {
            editor.putString("draft_title", binding.etMealTitle.getText().toString());
            editor.putString("draft_ingredients", binding.etIngredients.getText().toString());
            editor.putString("draft_instructions", binding.etInstructions.getText().toString());
            editor.putString("draft_cook", binding.etCookTime.getText().toString());
            editor.putString("draft_calories", binding.etCalories.getText().toString());
            editor.putString("draft_protein", binding.etProtein.getText().toString());
            editor.putString("draft_carbs", binding.etCarbs.getText().toString());
            editor.apply();
        }
    }
}




