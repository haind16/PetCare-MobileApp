package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityCartBinding;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Data mẫu giỏ hàng
        List<CartAdapter.CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartAdapter.CartItem(
                "Thức ăn hộp cho chó Jazzy 400g", "320", "120,000đ", 1));
        cartItems.add(new CartAdapter.CartItem(
                "Thuốc tẩy giun cho Pet", "94", "35,000đ", 2));
        cartItems.add(new CartAdapter.CartItem(
                "Bát ăn inox cho Pet", "92", "75,000đ", 1));

        CartAdapter adapter = new CartAdapter(cartItems);
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);

        binding.btnCheckout.setOnClickListener(v ->
                startActivity(new Intent(this, CheckoutActivity.class)));
    }
}