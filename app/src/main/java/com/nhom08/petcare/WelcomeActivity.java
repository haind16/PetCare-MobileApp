package com.nhom08.petcare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nhom08.petcare.databinding.ActivityWelcomeBinding;
import com.nhom08.petcare.ui.auth.LoginActivity;
import com.nhom08.petcare.ui.auth.RegisterActivity;
import com.nhom08.petcare.ui.main.MainActivity;

/**
 * Activity màn hình chào mừng.
 * Kiểm tra trạng thái đăng nhập của người dùng.
 * Nếu đã đăng nhập thì chuyển vào trang chủ, nếu chưa thì cho phép chọn Đăng nhập/Đăng ký.
 */
public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Kiểm tra xem người dùng đã đăng nhập chưa thông qua Firebase Auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Nếu đã login → chuyển thẳng vào MainActivity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Nếu chưa login → hiển thị các nút điều hướng đến màn hình Đăng nhập và Đăng ký
        binding.btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        binding.btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}