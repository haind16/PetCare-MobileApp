package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAllergyBinding;

public class AllergyActivity extends AppCompatActivity {
    private ActivityAllergyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllergyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            String content = binding.etContent.getText().toString().trim();
            if (content.isEmpty()) {
                binding.etContent.setError("Vui lòng nhập thông tin");
                return;
            }
            Toast.makeText(this, "Đã lưu!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}