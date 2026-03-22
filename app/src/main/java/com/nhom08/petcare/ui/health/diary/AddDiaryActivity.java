package com.nhom08.petcare.ui.health.diary;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddDiaryBinding;

public class AddDiaryActivity extends AppCompatActivity {

    private ActivityAddDiaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Ăn uống
        binding.btnDoAn.setOnClickListener(v -> openDetail("Cho ăn"));
        binding.btnDoUong.setOnClickListener(v -> openDetail("Cho uống nước"));

        // Chăm sóc
        binding.btnChaiLong.setOnClickListener(v -> openDetail("Chải lông"));
        binding.btnVeSinh.setOnClickListener(v -> openDetail("Vệ sinh"));
        binding.btnTam.setOnClickListener(v -> openDetail("Tắm"));

        // Hoạt động
        binding.btnDiBo.setOnClickListener(v -> openDetail("Đi bộ"));
        binding.btnHuanLuyen.setOnClickListener(v -> openDetail("Huấn luyện"));
        binding.btnVuiChoi.setOnClickListener(v -> openDetail("Vui chơi"));
        binding.btnDiNgu.setOnClickListener(v -> openDetail("Đi ngủ"));
    }

    private void openDetail(String activityName) {
        Intent intent = new Intent(this, DiaryDetailActivity.class);
        intent.putExtra("name", activityName);
        startActivity(intent);
    }
}