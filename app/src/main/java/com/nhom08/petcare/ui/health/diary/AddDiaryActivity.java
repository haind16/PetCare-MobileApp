package com.nhom08.petcare.ui.health.diary;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddDiaryBinding;

/**
 * Activity cho phép chọn loại hoạt động để thêm vào nhật ký.
 * Bao gồm các nhóm: Ăn uống, Chăm sóc và Hoạt động vui chơi.
 */
public class AddDiaryActivity extends AppCompatActivity {

    private ActivityAddDiaryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Nhóm Ăn uống
        binding.btnDoAn.setOnClickListener(v -> openDetail("Cho ăn"));
        binding.btnDoUong.setOnClickListener(v -> openDetail("Cho uống nước"));

        // Nhóm Chăm sóc
        binding.btnChaiLong.setOnClickListener(v -> openDetail("Chải lông"));
        binding.btnVeSinh.setOnClickListener(v -> openDetail("Vệ sinh"));
        binding.btnTam.setOnClickListener(v -> openDetail("Tắm"));

        // Nhóm Hoạt động
        binding.btnDiBo.setOnClickListener(v -> openDetail("Đi bộ"));
        binding.btnHuanLuyen.setOnClickListener(v -> openDetail("Huấn luyện"));
        binding.btnVuiChoi.setOnClickListener(v -> openDetail("Vui chơi"));
        binding.btnDiNgu.setOnClickListener(v -> openDetail("Đi ngủ"));
    }

    /**
     * Chuyển sang màn hình chi tiết để nhập ghi chú và lưu nhật ký.
     * @param activityName Tên hoạt động đã chọn.
     */
    private void openDetail(String activityName) {
        Intent intent = new Intent(this, DiaryDetailActivity.class);
        intent.putExtra("name", activityName);
        startActivity(intent);
    }
}