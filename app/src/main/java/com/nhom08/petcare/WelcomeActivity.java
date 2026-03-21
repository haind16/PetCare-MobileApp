package com.nhom08.petcare;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityWelcomeBinding;
import com.nhom08.petcare.ui.auth.LoginActivity;
import com.nhom08.petcare.ui.auth.RegisterActivity;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        binding.btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}