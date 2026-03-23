package com.nhom08.petcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nhom08.petcare.databinding.ActivityRegisterBinding;
import com.nhom08.petcare.ui.main.MainActivity;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.btnBack.setOnClickListener(v -> finish());

        // btnSubmit → đăng ký
        binding.btnSubmit.setOnClickListener(v -> register());
    }

    private void register() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        // Validate
        if (fullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            return;
        }
        if (email.isEmpty()) {
            binding.etEmail.setError("Vui lòng nhập email");
            return;
        }
        if (username.isEmpty()) {
            binding.etUsername.setError("Vui lòng nhập username");
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

        // Tạo tài khoản Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = result.getUser().getUid();

                    Map<String, Object> user = new HashMap<>();
                    user.put("displayName", fullName);
                    user.put("username", username);
                    user.put("email", email);
                    user.put("phone", "");
                    user.put("address", "");

                    db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this,
                                        "Đăng ký thành công! Vui lòng đăng nhập.",
                                        Toast.LENGTH_SHORT).show();

                                // Đăng xuất sau khi tạo tài khoản
                                FirebaseAuth.getInstance().signOut();

                                // Về màn Login
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Firestore lỗi nhưng Auth đã tạo → signOut + về Login
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(this,
                                        "Lưu thông tin thất bại: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                binding.btnSubmit.setEnabled(true);
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