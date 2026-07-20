package com.example.yummyplan.view;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.R;
import com.example.yummyplan.model.User;

import java.util.ArrayList;

//هاد كلاس الأدابتر وهو زي الجسر بين البيانات وال xml بحيث بياخد البيانات الي انا خزنتها بالarray list وبوزعها وبرتبها في الxml الشكل الي حطيناه في الريسايكل فيو
public class RecentUsersAdapter extends RecyclerView.Adapter<RecentUsersAdapter.RecentUserViewHolder>{

    private ArrayList<User> recent_user_list;
    // هاد متغير لسياق الواجهة الحالية وساويناه بالواجهة تبعتنا تحت
    private Context context;

    //بعمل كونستراكتور لأني هحتاجه في شاشة الادمن داشبورد
    public RecentUsersAdapter(Context context, ArrayList<User> recent_user_list) {
        this.context = context;
        this.recent_user_list = recent_user_list;
    }

    @NonNull
    @Override
    public RecentUsersAdapter.RecentUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // هاد بعمل انفلاتنق للitem تبع الريسايكل يعني زي كأنه ببني الشاشة وبعدها بخزنها في فيو
        View v = LayoutInflater.from(context).inflate(R.layout.item_recent_user, parent, false);
        //بعدها ببعت الفيو هاد للRecentUserViewHolder عشان يمسك العناصر الي بدي اعبيها او اتعامل معها بالزبط
        RecentUserViewHolder recent_holder = new RecentUserViewHolder(v);
        return recent_holder;
    }

    //هاد الدالة الي بتاخد البيانات من اللست وبتعبيها في تصميم ال xml
    @Override
    public void onBindViewHolder(@NonNull RecentUsersAdapter.RecentUserViewHolder holder, int position) {
        // بنعمل اوبجكت من كلاس اليوزر وبنجيب البوزيشن
        User u = recent_user_list.get(position);

        // بناء على البوزيشن بعبي هاد القيم
        holder.tv_name.setText(u.getFullName());
        holder.tv_email.setText(u.getEmail());
        //هان بنجيب الصورة
       if (u.getUser_img() != null && !u.getUser_img().isEmpty()) {
            holder.img_user.setImageURI(Uri.parse(u.getUser_img()));
        } else {
            holder.img_user.setImageResource(R.drawable.ic_green_person);
        }
    }

    //هاد بتجيب عدد العناصر في الاري لست
    @Override
    public int getItemCount() {
        return recent_user_list.size();
    }

    // هاد كلاس مساعد بياخد وبركزلي عالحقول الي انا محدداها تحت بحيث كل لفة على item التطبيق من كل الواجهة بش بضوف هدول الحقلين وهيك بقراهم وبعدلهم بسرعة
    public static class RecentUserViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_email;
        ImageView img_user;
        public RecentUserViewHolder(@NonNull View itemView) {
            super(itemView);
            // هان بمسك العناصر الي بدي اتعامل معها وباشر عليها عشان بسرعة اعبي البيانات في الonBindViewHolder لاني عارفة وين بدي اعبيها
            tv_name = itemView.findViewById(R.id.tv_user_name);
            tv_email = itemView.findViewById(R.id.tv_user_email);
            img_user = itemView.findViewById(R.id.img_avtar_picture);
        }
    }

}
