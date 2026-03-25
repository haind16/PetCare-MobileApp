package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityQrPaymentBinding;

public class QRPaymentActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private ActivityQrPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Dùng ảnh QR tĩnh
        binding.imgQR.setImageResource(R.drawable.viet_qr);
        binding.imgQR.setPadding(0, 0, 0, 0);

        binding.btnConfirmPaid.setOnClickListener(v -> confirmPaid());
    }

    private void confirmPaid() {
        // Cập nhật trạng thái đơn hàng mới nhất → "da_thanh_toan"
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId != null) {
            FirebaseDatabase.getInstance(DB_URL)
                    .getReference("orders")
                    .child(userId)
                    .orderByKey()
                    .limitToLast(1)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        for (com.google.firebase.database.DataSnapshot child : snapshot.getChildren()) {
                            child.getRef().child("trangThai").setValue("da_thanh_toan");
                        }
                    });
        }

        Toast.makeText(this, "Xác nhận thanh toán QR thành công! 🎉",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, com.nhom08.petcare.ui.main.MainActivity.class);
        intent.putExtra("nav_to", "shop");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}