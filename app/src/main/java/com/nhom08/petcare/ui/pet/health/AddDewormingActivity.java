package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddDewormingBinding;

public class AddDewormingActivity extends AppCompatActivity {

    private ActivityAddDewormingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDewormingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        boolean isEdit = getIntent().getBooleanExtra("is_edit", false);
        if (isEdit) binding.tvTitle.setText("Sửa lịch tẩy giun");

        binding.btnSave.setOnClickListener(v -> {
            int day = binding.datePicker.getDayOfMonth();
            int month = binding.datePicker.getMonth() + 1;
            int year = binding.datePicker.getYear();
            Toast.makeText(this, "Đã lưu: " + day + "/" + month + "/" + year,
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}