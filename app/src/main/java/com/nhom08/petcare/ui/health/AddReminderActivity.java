package com.nhom08.petcare.ui.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddReminderBinding;

public class AddReminderActivity extends AppCompatActivity {

    private ActivityAddReminderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddReminderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Click vào từng loại → sang màn chi tiết
        binding.btnAnSang.setOnClickListener(v -> openDetail("Lịch trình ăn sáng"));
        binding.btnDiDao.setOnClickListener(v -> openDetail("Lịch trình đi dạo"));
        binding.btnUongThuoc.setOnClickListener(v -> openDetail("Lịch trình uống thuốc"));
        binding.btnCatTiaLong.setOnClickListener(v -> openDetail("Lịch trình cắt tỉa lông"));
        binding.btnKhac.setOnClickListener(v -> openDetail("Hoạt động khác"));
    }

    private void openDetail(String activityType) {
        Intent intent = new Intent(this, ReminderDetailActivity.class);
        intent.putExtra("activity_type", activityType);
        startActivity(intent);
    }
}