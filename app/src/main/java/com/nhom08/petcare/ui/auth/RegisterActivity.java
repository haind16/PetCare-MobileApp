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

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance(
                "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSubmit.setOnClickListener(v -> register());
    }

    private void register() {
        String fullName = binding.etFullName.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

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

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    String userId = result.getUser().getUid();
                    android.util.Log.d("REGISTER", "Auth OK: " + userId);

                    Map<String, Object> user = new HashMap<>();
                    user.put("displayName", fullName);
                    user.put("username", username);
                    user.put("email", email);
                    user.put("phone", "");
                    user.put("address", "");

                    db.child("users").child(userId).setValue(user)
                            .addOnSuccessListener(unused -> {
                                android.util.Log.d("REGISTER", "DB OK!");
                                Toast.makeText(this,
                                        "Đăng ký thành công!",
                                        Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("REGISTER", "DB Error: " + e.getMessage());
                                binding.btnSubmit.setEnabled(true);
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(this,
                                        "Lỗi DB: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("REGISTER", "Auth Error: " + e.getMessage());
                    binding.btnSubmit.setEnabled(true);
                    Toast.makeText(this,
                            "Đăng ký thất bại: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}