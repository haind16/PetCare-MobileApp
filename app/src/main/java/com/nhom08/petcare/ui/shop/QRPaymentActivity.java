package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityQrPaymentBinding;

public class QRPaymentActivity extends AppCompatActivity {

    private ActivityQrPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Dùng ảnh tĩnh thay vì gọi API
        binding.imgQR.setImageResource(R.drawable.viet_qr);
        // Xóa padding vì ảnh QR đã có nền đẹp rồi
        binding.imgQR.setPadding(0, 0, 0, 0);

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
