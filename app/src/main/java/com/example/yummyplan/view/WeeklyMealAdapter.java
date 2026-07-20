package com.example.yummyplan.view;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.model.Meal;
import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.R;

import java.util.ArrayList;

public class WeeklyMealAdapter extends RecyclerView.Adapter<WeeklyMealAdapter.MealViewHolder> {

    Context context;
    ArrayList<Meal> meals;
    int userId;
    String selectedDay;

    public WeeklyMealAdapter(Context context, ArrayList<Meal> meals, int userId, String selectedDay) {
        this.context = context;
        this.meals = meals;
        this.userId = userId;
        this.selectedDay = selectedDay;
    }

    @NonNull
    @Override
    public WeeklyMealAdapter.MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_weekly_meal, parent, false);
        MealViewHolder mvh = new MealViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyMealAdapter.MealViewHolder holder, int position) {

        // هان بعبي البيانات في الواجهة الي جهزتها باستخدام الهولدر الي بحددلي بالزبط وين اماكن العناصر
        Meal m = meals.get(position);
        holder.tv_meal_name.setText(m.getTitle());
        holder.tv_calories.setText(m.getCalories() + " kcal");
        holder.tv_time.setText(m.getCookTime() + " min");

        if (m.getImagePath() != null && !m.getImagePath().isEmpty() && !m.getImagePath().equals("image_path")) {
            try {
                Uri imageUri = Uri.parse(m.getImagePath());
                holder.img_meal_image.setImageURI(imageUri);
            } catch (Exception e) {
                holder.img_meal_image.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }

        // هان زر الحذف من الخطة الأسبوعية لليوم المحدد
        holder.img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete Alert");
                builder.setMessage("Are You Sure You Want to Delete ❓");

                builder.setPositiveButton("✅ Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // هان عملنا أوبجكت من الهلبر عشان نقدر نوصل لعملية الحذف من الخطة
                        DatabaseHelper db_helper = new DatabaseHelper(context);

                        // استدعينا دالة الحذف من الخطة مش من قاعدة البيانات كلها
                        boolean isDeleted = db_helper.deleteMealFromPlan(userId, m.getId(), selectedDay, m.getCategory());

                        if (isDeleted) {
                            // بنجيب مكان العنصر بالزبط من الريسايكل فيو
                            int currentPosition = holder.getAdapterPosition();
                            meals.remove(currentPosition);

                            notifyItemRemoved(currentPosition);
                            // هاد لاعادة ترتيب العناصر في الريسايكل فيو عشان ما يصر كراش
                            notifyItemRangeChanged(currentPosition, meals.size());
                        }
                    }
                });

                builder.setNegativeButton("❌ No", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    // هان بعمل تعريف وانفلاتينق لعناصر الريسايكل فيو الي في ال xml
    public class MealViewHolder extends RecyclerView.ViewHolder {

        ImageView img_meal_image, img_delete;
        TextView tv_meal_name, tv_calories, tv_time;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);

            img_meal_image = itemView.findViewById(R.id.img_meal_image);
            tv_meal_name = itemView.findViewById(R.id.tv_meal_name);
            tv_calories = itemView.findViewById(R.id.tv_calories);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_delete = itemView.findViewById(R.id.img_delete);
        }
    }
}