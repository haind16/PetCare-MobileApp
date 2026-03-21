package com.nhom08.petcare.ui.profile;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityProfileDetailBinding;

public class ProfileDetailActivity extends AppCompatActivity {

    private ActivityProfileDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (fullName.isEmpty()) {
                binding.etFullName.setError("Vui lòng nhập họ tên");
                return;
            }

            Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}