package com.nhom08.petcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom08.petcare.databinding.ActivityRegisterBinding;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity xử lý đăng ký tài khoản mới.
 * Sử dụng Firebase Authentication để tạo tài khoản và Firebase Realtime Database để lưu thông tin người dùng.
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Firebase Auth và Database Reference
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance(
                "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference();

        // Nút quay lại
        binding.btnBack.setOnClickListener(v -> finish());
        
        // Nút thực hiện đăng ký
        binding.btnSubmit.setOnClickListener(v -> register());
    }

    /**
     * Logic đăng ký người dùng.
     * 1. Kiểm tra tính hợp lệ của thông tin (Họ tên, Email, SĐT, Mật khẩu).
     * 2. Gọi Firebase Auth để tạo tài khoản.
     * 3. Nếu thành công, lưu thông tin bổ sung vào Firebase Realtime Database.
     * 4. Gửi email xác nhận tài khoản.
     */
    private void register() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();
        String phone    = binding.etUsername.getText().toString().trim(); // Trường username thực chất lưu SĐT
        String password = binding.etPassword.getText().toString().trim();

        // Validation dữ liệu đầu vào
        if (fullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            return;
        }
        if (email.isEmpty()) {
            binding.etEmail.setError("Vui lòng nhập email");
            return;
        }
        if (phone.isEmpty()) {
            binding.etUsername.setError("Vui lòng nhập số điện thoại");
            return;
        }
        if (password.isEmpty()) {
            binding.etPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }
        if (password.length() < 6) {
            binding.etPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            return;
        }

        binding.btnSubmit.setEnabled(false);

        // Tạo tài khoản trên Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = result.getUser().getUid();

                    // Chuẩn bị dữ liệu user để lưu vào Realtime DB
                    Map<String, Object> user = new HashMap<>();
                    user.put("displayName", fullName);
                    user.put("username",    fullName); // Đồng bộ username với fullName
                    user.put("email",       email);
                    user.put("phone",       phone);
                    user.put("address",     "");

                    // Lưu vào node "users"
                    db.child("users").child(userId).setValue(user)
                            .addOnSuccessListener(unused -> {
                                // Gửi email xác nhận tài khoản qua Firebase
                                result.getUser().sendEmailVerification()
                                        .addOnSuccessListener(v -> {
                                            Toast.makeText(this,
                                                    "Đăng ký thành công!\nVui lòng kiểm tra email " + email + " để xác nhận tài khoản.",
                                                    Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Vẫn coi là thành công nhưng thông báo lỗi gửi mail
                                            Toast.makeText(this,
                                                    "Đăng ký thành công! (Không gửi được email xác nhận)",
                                                    Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            Intent intent = new Intent(this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                binding.btnSubmit.setEnabled(true);
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(this,
                                        "Lỗi lưu dữ liệu: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    binding.btnSubmit.setEnabled(true);
                    Toast.makeText(this,
                            "Đăng ký thất bại: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}