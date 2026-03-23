package com.nhom08.petcare.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.nhom08.petcare.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> finish());

        // Nút Gửi mã
        binding.btnSendCode.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            if (email.isEmpty()) {
                binding.etEmail.setError("Vui lòng nhập email");
                return;
            }
            binding.btnSendCode.setEnabled(false);

            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this,
                                "Email đặt lại mật khẩu đã được gửi!",
                                Toast.LENGTH_LONG).show();
                        binding.btnSendCode.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        binding.btnSendCode.setEnabled(true);
                        Toast.makeText(this,
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        });

        // Nút Submit (checkbox)
        binding.btnSubmit.setOnClickListener(v -> finish());
    }
}