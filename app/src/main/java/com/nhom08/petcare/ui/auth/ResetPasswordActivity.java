package com.nhom08.petcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> {
            String newPass = binding.etNewPassword.getText().toString().trim();
            String confirmPass = binding.etConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Mật khẩu không khớp",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Reset xong → về Login
            Toast.makeText(this, "Đổi mật khẩu thành công!",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity(); // Xóa hết stack, về Login
        });
    }
}