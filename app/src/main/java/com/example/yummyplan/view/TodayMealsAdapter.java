package com.example.yummyplan.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.R;
import com.example.yummyplan.model.Meal;

import java.util.ArrayList;

public class TodayMealsAdapter extends RecyclerView.Adapter<TodayMealsAdapter.MealViewHolder> {

    private Context context;
    private ArrayList<Meal> mealsList;

    public TodayMealsAdapter(Context context, ArrayList<Meal> mealsList) {
        this.context = context;
        this.mealsList = mealsList;
    }

    @NonNull
    @Override
    public TodayMealsAdapter.MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_today_meal, parent, false);
        MealViewHolder holder = new MealViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TodayMealsAdapter.MealViewHolder holder, int position) {

        // بنعمل أوبجكت من كلاس الوجبة وبنجيب البوزيشن الحالي
        Meal m = mealsList.get(position);

        // بناءً على البوزيشن بنعبي البيانات في حقول البطاقة
        holder.tv_meal_name.setText(m.getTitle());
        holder.tv_meal_category.setText(m.getCategory());

        // هان بنعرض السعرات والوقت زي ما صممتيها بالظبط في الـ XML
        holder.tv_meal_details.setText(m.getCalories() + " kcal  •  " + m.getCookTime() + " min");

        // هان بنفحص الصورة وبنعرضها، لو مش موجودة بنحط الصورة الافتراضية
        if (m.getImagePath() != null && !m.getImagePath().isEmpty()) {
            holder.img_meal_image.setImageURI(Uri.parse(m.getImagePath()));
        } else {
            holder.img_meal_image.setImageResource(R.drawable.meal);
        }

        // هان حالة التشيك بوكس الافتراضية غير محددة
        holder.cb_check.setChecked(false);
    }

    // هاد بترجع عدد الوجبات الي في القائمة
    @Override
    public int getItemCount() {
        return mealsList.size();
    }

    public class MealViewHolder extends RecyclerView.ViewHolder {

        ImageView img_meal_image;
        TextView tv_meal_category, tv_meal_name, tv_meal_details;
        CheckBox cb_check;
        public MealViewHolder(@NonNull View itemView) {
            super(itemView);

            img_meal_image = itemView.findViewById(R.id.img_meal_image);
            tv_meal_category = itemView.findViewById(R.id.tv_meal_category);
            tv_meal_name = itemView.findViewById(R.id.tv_meal_name);
            tv_meal_details = itemView.findViewById(R.id.tv_meal_details);
            cb_check = itemView.findViewById(R.id.cb_check);
        }
    }
}
