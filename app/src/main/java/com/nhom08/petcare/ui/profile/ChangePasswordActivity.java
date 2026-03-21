package com.nhom08.petcare.ui.profile;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private boolean showCurrent = false;
    private boolean showNew = false;
    private boolean showConfirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Toggle hiện/ẩn mật khẩu
        binding.btnToggleCurrent.setOnClickListener(v -> {
            showCurrent = !showCurrent;
            binding.etCurrentPassword.setTransformationMethod(
                    showCurrent ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance());
            binding.btnToggleCurrent.setImageResource(
                    showCurrent ? R.drawable.ic_eye : R.drawable.ic_eye_off);
            binding.etCurrentPassword.setSelection(
                    binding.etCurrentPassword.getText().length());
        });

        binding.btnToggleNew.setOnClickListener(v -> {
            showNew = !showNew;
            binding.etNewPassword.setTransformationMethod(
                    showNew ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance());
            binding.btnToggleNew.setImageResource(
                    showNew ? R.drawable.ic_eye : R.drawable.ic_eye_off);
            binding.etNewPassword.setSelection(
                    binding.etNewPassword.getText().length());
        });

        binding.btnToggleConfirm.setOnClickListener(v -> {
            showConfirm = !showConfirm;
            binding.etConfirmPassword.setTransformationMethod(
                    showConfirm ? HideReturnsTransformationMethod.getInstance()
                            : PasswordTransformationMethod.getInstance());
            binding.btnToggleConfirm.setImageResource(
                    showConfirm ? R.drawable.ic_eye : R.drawable.ic_eye_off);
            binding.etConfirmPassword.setSelection(
                    binding.etConfirmPassword.getText().length());
        });

        // Xác nhận
        binding.btnConfirm.setOnClickListener(v -> {
            String current = binding.etCurrentPassword.getText().toString().trim();
            String newPass = binding.etNewPassword.getText().toString().trim();
            String confirm = binding.etConfirmPassword.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu mới không khớp",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: cập nhật mật khẩu qua Firebase
            Toast.makeText(this, "Đổi mật khẩu thành công!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
