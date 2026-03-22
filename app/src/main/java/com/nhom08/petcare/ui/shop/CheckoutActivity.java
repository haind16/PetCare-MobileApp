package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityCheckoutBinding;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private String selectedPayment = "cod";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Set icon mặc định trước
        binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_money, 0, R.drawable.ic_radio_on, 0);
        binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_bank, 0, R.drawable.ic_radio_off, 0);
        binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_qr, 0, R.drawable.ic_radio_off, 0);

        // Set background mặc định
        binding.rbCOD.setBackgroundResource(R.drawable.bg_payment_selected);
        binding.rbBank.setBackgroundResource(R.drawable.edit_text_border);
        binding.rbQR.setBackgroundResource(R.drawable.edit_text_border);

        // Set listener SAU
        binding.rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            // Reset tất cả
            binding.rbCOD.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbBank.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbQR.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_money, 0, R.drawable.ic_radio_off, 0);
            binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bank, 0, R.drawable.ic_radio_off, 0);
            binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_qr, 0, R.drawable.ic_radio_off, 0);

            // Set selected
            if (checkedId == R.id.rbCOD) {
                selectedPayment = "cod";
                binding.rbCOD.setBackgroundResource(R.drawable.bg_payment_selected);
                binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_money, 0, R.drawable.ic_radio_on, 0);
            } else if (checkedId == R.id.rbBank) {
                selectedPayment = "bank";
                binding.rbBank.setBackgroundResource(R.drawable.bg_payment_selected);
                binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_bank, 0, R.drawable.ic_radio_on, 0);
            } else if (checkedId == R.id.rbQR) {
                selectedPayment = "qr";
                binding.rbQR.setBackgroundResource(R.drawable.bg_payment_selected);
                binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_qr, 0, R.drawable.ic_radio_on, 0);
            }
        });

        // Xác nhận
        binding.btnConfirm.setOnClickListener(v -> {
            if (selectedPayment.equals("qr")) {
                startActivity(new Intent(this, QRPaymentActivity.class));
            } else if (selectedPayment.equals("bank")) {
                startActivity(new Intent(this, BankTransferActivity.class));
            } else {
                // COD → về Shop luôn
                Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                goBackToShop();
            }
        });
    }

    private void goBackToShop() {
        Intent intent = new Intent(this, com.nhom08.petcare.ui.main.MainActivity.class);
        intent.putExtra("nav_to", "shop");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}