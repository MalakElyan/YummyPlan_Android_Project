package com.example.yummyplan.controller;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yummyplan.utills.DatabaseHelper;
import com.example.yummyplan.databinding.ActivityAllUsersBinding;
import com.example.yummyplan.model.User;
import com.example.yummyplan.view.RecentUsersAdapter;

import java.util.ArrayList;

public class Activity_All_Users extends AppCompatActivity {

    private ActivityAllUsersBinding binding;
    private DatabaseHelper db_helper;
    private RecentUsersAdapter adapter;
    private RecyclerView rv_AllUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllUsersBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);

        rv_AllUsers = binding.rvAllUsers;

        db_helper = new DatabaseHelper(this);

        // هان بجيب كل المستخدمين
        ArrayList<User> allUsersList = (ArrayList<User>) db_helper.getAllUsers();
        adapter = new RecentUsersAdapter(this, allUsersList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        rv_AllUsers.setLayoutManager(lm);
         rv_AllUsers.setAdapter(adapter);

        // زر الback
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

