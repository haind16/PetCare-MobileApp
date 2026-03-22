package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityBankTransferBinding;

public class BankTransferActivity extends AppCompatActivity {

    private ActivityBankTransferBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBankTransferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnConfirmPaid.setOnClickListener(v -> {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, com.nhom08.petcare.ui.main.MainActivity.class);
            intent.putExtra("nav_to", "shop");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}