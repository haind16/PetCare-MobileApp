package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddDiseaseBinding;

public class AddDiseaseActivity extends AppCompatActivity {

    private ActivityAddDiseaseBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDiseaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        boolean isEdit = getIntent().getBooleanExtra("is_edit", false);
        if (isEdit) binding.tvTitle.setText("Sửa bệnh nền");

        String existingContent = getIntent().getStringExtra("title");
        if (existingContent != null) binding.etContent.setText(existingContent);

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