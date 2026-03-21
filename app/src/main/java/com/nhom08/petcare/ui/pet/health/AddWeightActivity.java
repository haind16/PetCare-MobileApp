package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddWeightBinding;

public class AddWeightActivity extends AppCompatActivity {

    private ActivityAddWeightBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            String weight = binding.etWeight.getText().toString().trim();
            if (weight.isEmpty()) {
                binding.etWeight.setError("Vui lòng nhập cân nặng");
                return;
            }
            Toast.makeText(this, "Đã lưu cân nặng!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}