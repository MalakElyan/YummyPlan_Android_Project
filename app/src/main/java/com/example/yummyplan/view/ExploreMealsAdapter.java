package com.example.yummyplan.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.controller.Add_or_Edit_Meal;
import com.example.yummyplan.controller.Meal_Deatails;
import com.example.yummyplan.model.Meal;
import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.R;

import java.util.ArrayList;

public class ExploreMealsAdapter extends RecyclerView.Adapter<ExploreMealsAdapter.MealViewHolder> {

    Context context;
    ArrayList<Meal> meals;
    String userRole;

    public ExploreMealsAdapter(Context context, ArrayList<Meal> meals, String userRole) {
        this.context = context;
        this.meals = meals;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public ExploreMealsAdapter.MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // هاد بتحول ملف الxml من كود لview جاهزة للعرض
        View v = LayoutInflater.from(context).inflate(R.layout.item_explore_meal, parent, false);
        // هان بياخد العناصر من ال viewholder وبركبها عالفيو
        MealViewHolder mvh = new MealViewHolder(v);
        return mvh;    }

    @Override
    public void onBindViewHolder(@NonNull ExploreMealsAdapter.MealViewHolder holder, int position) {

        //هاد عشان اتحكم في ظهور واختفاء الايقونات الي على الكارد
        if (userRole != null && userRole.equals("admin")) {
            holder.cv_edit.setVisibility(View.VISIBLE);
            holder.cv_delete.setVisibility(View.VISIBLE);
            holder.cv_add_to_plan.setVisibility(View.GONE);
        } else {
            holder.cv_edit.setVisibility(View.GONE);
            holder.cv_delete.setVisibility(View.GONE);
            holder.cv_add_to_plan.setVisibility(View.VISIBLE);
        }

        //هان بعبي البيانات في الواجهة الي جهزتها باستخدام الهولدر الي بحددلي بالزبط وين اماكن العناصر
        Meal m = meals.get(position);
        holder.tv_meal_title.setText(m.getTitle());
        holder.tv_diet_type.setText(m.getDietType());
        holder.tv_calories.setText("🔥 " + m.getCalories() + " kcal");
        holder.tv_time.setText("🕒 " + m.getCookTime() + " min");


        if (m.getImagePath() != null && !m.getImagePath().isEmpty() && !m.getImagePath().equals("image_path")) {
            try {
                Uri imageUri = Uri.parse(m.getImagePath());
                holder.img_meal_photo.setImageURI(imageUri);
            } catch (Exception e) {
                holder.img_meal_photo.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }

        // هان زر التعديل
        holder.cv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Add_or_Edit_Meal.class);

                intent.putExtra("action_type", "edit");
                intent.putExtra("meal_id", m.getId());
                context.startActivity(intent);
            }
        });

        // هان زر الحذف
        holder.cv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete Alert");
                builder.setMessage(" Are You Sure You Want to Delete ❓");

                builder.setPositiveButton("✅ Yes", new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // هان عملنا أوبجكت من الهلبر عشان نقدر نوصل لعملية الحذف
                                DatabaseHelper db_helper = new DatabaseHelper(context);

                                boolean isDeleted = db_helper.deleteMeal(m.getId());

                                if (isDeleted) {
                                    // بنجيب مكان العنصر بالزبط من الريسايكل فيو
                                    int currentPosition = holder.getAdapterPosition();
                                    meals.remove(currentPosition);

                                    notifyItemRemoved(currentPosition);
                                    //هاد لاعادة ترتيب العناصر في الريسايكل فيو عشان ما يصر كراش
                                    notifyItemRangeChanged(currentPosition, meals.size());
                                }
                            }
                        });

                builder.setNegativeButton("❌ No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // هان زر إضافة الوجبة للخطة
        holder.cv_add_to_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] daysArray = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
                String[] categoriesArray = {"Breakfast", "Lunch", "Dinner", "Snacks", "Desserts"};
                showAddToPlanDialog(m, daysArray, categoriesArray);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Meal_Deatails.class);
                // بمرر الid تبع الوجبة الي في الكرت هاد لشاشة الديتيلز عشان تعرضها
                intent.putExtra("meal_id", m.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    //هان بعمل تعريف وانفلاتينق لعناصر الريسايكل فيو الي في ال xml
    public class MealViewHolder extends RecyclerView.ViewHolder {

        ImageView img_meal_photo;
        TextView tv_meal_title, tv_diet_type, tv_calories, tv_time;
        CardView cv_edit, cv_delete,cv_add_to_plan;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);

            img_meal_photo = itemView.findViewById(R.id.img_meal_photo);
            tv_meal_title = itemView.findViewById(R.id.tv_meal_title);
            tv_diet_type = itemView.findViewById(R.id.tv_diet_type);
            tv_calories = itemView.findViewById(R.id.tv_calories);
            tv_time = itemView.findViewById(R.id.tv_time);
            cv_edit = itemView.findViewById(R.id.cv_edit);
            cv_delete = itemView.findViewById(R.id.cv_delete);
            cv_add_to_plan = itemView.findViewById(R.id.cv_add_to_plan);

        }
    }

    private void showAddToPlanDialog(Meal meal, String[] daysArray, String[] categoriesArray) {

        // هان بنبني التنبيه
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add \"" + meal.getTitle() + "\" to Plan");
        builder.setMessage("Please fill the fields");

        builder.setCancelable(false);
        // انفلاتور لتصميم الديالوج تبعي
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.dialog_add_to_plan, null);
        builder.setView(alertView);
        // ربط السبرنرات عادي بالxml
        Spinner spinnerDays = alertView.findViewById(R.id.sp_dialog_days);
        Spinner spinnerCategories = alertView.findViewById(R.id.sp_dialog_categories);

        ArrayAdapter daysAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, daysArray);
        spinnerDays.setAdapter(daysAdapter);

        ArrayAdapter categoriesAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, categoriesArray);
        spinnerCategories.setAdapter(categoriesAdapter);

        // زر ال yes
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String selectedDay = spinnerDays.getSelectedItem().toString();
                String selectedCategory = spinnerCategories.getSelectedItem().toString();

                android.content.SharedPreferences preferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
                int userId = preferences.getInt("user_id", -1);

                //هان بروح لقاعدة البيانات عشان بعمل اوبجكت من الهلبر عشان يوصلني لعملية الاضافة
                DatabaseHelper db_helper = new DatabaseHelper(context);

                //بستدعي دالة الحفظ وبمررلها البيانات
                boolean isInserted = db_helper.insertToPlan(userId, meal.getId(), selectedDay, selectedCategory);

                if (isInserted) {
                    Toast.makeText(context, " Added to your weekly plan!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, " Failed to add to plan", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // زر ال no
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show();
            }
        });

        //بأنشئ الديالوج وبعرضه
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
