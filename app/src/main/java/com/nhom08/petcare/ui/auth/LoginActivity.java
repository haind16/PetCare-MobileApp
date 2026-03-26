package com.nhom08.petcare.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nhom08.petcare.databinding.ActivityLoginBinding;
import com.nhom08.petcare.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSubmit.setOnClickListener(v -> login());

        binding.tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        binding.tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email    = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            binding.etUsername.setError("Vui lòng nhập email");
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

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnSubmit.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    binding.progressBar.setVisibility(View.GONE);

                    FirebaseUser user = result.getUser();

                    // Kiểm tra email đã xác nhận chưa
                    if (!user.isEmailVerified()) {
                        // Chưa xác nhận → đăng xuất, hiện thông báo
                        FirebaseAuth.getInstance().signOut();
                        binding.btnSubmit.setEnabled(true);

                        Toast.makeText(this,
                                "Email chưa được xác nhận!\nVui lòng kiểm tra hộp thư " + email + " và bấm vào link xác nhận.",
                                Toast.LENGTH_LONG).show();

                        // Hỏi user có muốn gửi lại mail không
                        showResendVerificationDialog(email, password);
                        return;
                    }

                    // Email đã xác nhận → vào app
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnSubmit.setEnabled(true);

                    // Phân biệt lỗi sai mật khẩu / sai email
                    String msg = e.getMessage();
                    if (msg != null && (msg.contains("password") || msg.contains("credential"))) {
                        Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_LONG).show();
                    } else if (msg != null && msg.contains("no user")) {
                        Toast.makeText(this, "Tài khoản không tồn tại!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ----------------------------------------------------------------
    // Dialog gửi lại email xác nhận
    // ----------------------------------------------------------------
    private void showResendVerificationDialog(String email, String password) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Email chưa xác nhận")
                .setMessage("Bạn có muốn gửi lại email xác nhận đến " + email + " không?")
                .setPositiveButton("Gửi lại", (dialog, which) -> {
                    // Đăng nhập lại tạm để gửi mail
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(result -> {
                                result.getUser().sendEmailVerification()
                                        .addOnSuccessListener(v -> {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(this,
                                                    "Đã gửi lại email xác nhận!",
                                                    Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(this,
                                                    "Không gửi được email: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        });
                            });
                })
                .setNegativeButton("Để sau", null)
                .show();
    }
}