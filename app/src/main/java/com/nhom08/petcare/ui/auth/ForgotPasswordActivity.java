package com.nhom08.petcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSendCode.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.etEmail.setError("Vui lòng nhập email");
                return;
            }
            Toast.makeText(this, "Đã gửi mã về email!",
                    Toast.LENGTH_SHORT).show();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            String code = binding.etCode.getText().toString().trim();
            if (code.isEmpty()) {
                binding.etCode.setError("Vui lòng nhập mã");
                return;
            }
            // Chuyển sang ResetPasswordActivity
            startActivity(new Intent(this, ResetPasswordActivity.class));
        });
    }
}