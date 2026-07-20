package com.example.yummyplan.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.R;

import java.util.ArrayList;

public class HealthyTipsAdapter extends RecyclerView.Adapter<HealthyTipsAdapter.TipViewHolder> {

    private Context context;
    private ArrayList<String> tipsList;

    public HealthyTipsAdapter(Context context, ArrayList<String> tipsList) {
        this.context = context;
        this.tipsList = tipsList;
    }

    @NonNull
    @Override
    public HealthyTipsAdapter.TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // انفلاتينق للتصميم تبع بطاقة النصيحة
        View view = LayoutInflater.from(context).inflate(R.layout.item_healthy_tip, parent, false);
        TipViewHolder holder = new TipViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HealthyTipsAdapter.TipViewHolder holder, int position) {
        // بنجيب النص كامل
        String fullData = tipsList.get(position);

        // هان بنفصل العنوان عن الوصف باستخدام هاد //
        if (fullData.contains("//")) {
            String[] parts = fullData.split("//");
            String title = parts[0].trim();
            String description = parts[1].trim();

            // بنعبي العنوان والوصف المتغيرين
            holder.tv_tip_title.setText(title);
            holder.tvTipDescription.setText(description);
        } else {
            // هان لو نسيت احط الفاصل يحط هاد العنوان افتراضي
            holder.tv_tip_title.setText("Healthy Tip");
            holder.tvTipDescription.setText(fullData);
        }

        // هان الأيقونة ثابتة لكل البطاقات
        holder.img_tip_icon.setImageResource(R.drawable.idea);
    }

    // هاد بترجع عدد النصائح الي في القائمة
    @Override
    public int getItemCount() {
        return tipsList.size();
    }

    public class TipViewHolder extends RecyclerView.ViewHolder {

        ImageView img_tip_icon;
        TextView tv_tip_title, tvTipDescription;

        public TipViewHolder(@NonNull View itemView) {
            super(itemView);

            img_tip_icon = itemView.findViewById(R.id.img_tip_icon);
            tv_tip_title = itemView.findViewById(R.id.tv_tip_title);
            tvTipDescription = itemView.findViewById(R.id.tvTipDescription);
        }
    }
}