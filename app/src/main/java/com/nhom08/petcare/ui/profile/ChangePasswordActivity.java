package com.nhom08.petcare.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityChangePasswordBinding;
import com.nhom08.petcare.ui.auth.LoginActivity; // Đảm bảo import đúng đường dẫn LoginActivity của bạn

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private boolean showCurrent = false;
    private boolean showNew = false;
    private boolean showConfirm = false;

    // Link DB bạn đã cung cấp
    private static final String DB_URL = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // ----------------------------------------------------------------
        // Toggle hiện/ẩn mật khẩu (Giữ nguyên code chuẩn của bạn)
        // ----------------------------------------------------------------
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

        // ----------------------------------------------------------------
        // Xác nhận Đổi mật khẩu
        // ----------------------------------------------------------------
        binding.btnConfirm.setOnClickListener(v -> {
            String current = binding.etCurrentPassword.getText().toString().trim();
            String newPass = binding.etNewPassword.getText().toString().trim();
            String confirm = binding.etConfirmPassword.getText().toString().trim();

            if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, "Mật khẩu mới phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm xử lý Firebase Auth
            updatePasswordOnFirebase(current, newPass);
        });
    }

    // ----------------------------------------------------------------
    // Logic cập nhật mật khẩu lên Firebase
    // ----------------------------------------------------------------
    private void updatePasswordOnFirebase(String currentPass, String newPass) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            // Bước 1: Xác thực lại bằng mật khẩu cũ (Bắt buộc bởi Firebase)
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Bước 2: Cập nhật mật khẩu mới trên Authentication
                    user.updatePassword(newPass).addOnCompleteListener(updateTask -> {
                        if (updateTask.isSuccessful()) {

                            // Bước 3: Cập nhật mật khẩu trên Realtime Database (Bảng users)
                            DatabaseReference dbRef = FirebaseDatabase.getInstance(DB_URL)
                                    .getReference("users").child(user.getUid());
                            dbRef.child("password").setValue(newPass); // Nếu db lưu là "matKhau", hãy sửa chữ "password" lại nhé

                            Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();

                            // Đăng xuất và đẩy về Login
                            auth.signOut();
                            Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ChangePasswordActivity.this, "Lỗi cập nhật mật khẩu mới!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Nếu mật khẩu cũ không đúng
                    Toast.makeText(ChangePasswordActivity.this, "Mật khẩu hiện tại không đúng!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Không tìm thấy phiên đăng nhập!", Toast.LENGTH_SHORT).show();
        }
    }
}