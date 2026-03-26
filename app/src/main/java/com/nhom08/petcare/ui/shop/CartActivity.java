package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.databinding.ActivityCartBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private ActivityCartBinding binding;
    private CartAdapter         adapter;
    private DatabaseReference   cartRef;

    private final List<CartAdapter.CartItem> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        setupCartRef();
        loadCartFromFirebase();

        binding.btnCheckout.setOnClickListener(v -> {
            // Kiểm tra nếu giỏ hàng trống
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                // Có sản phẩm mới cho phép chuyển sang màn Checkout
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });
    }

    // ----------------------------------------------------------------
    // Setup RecyclerView
    // ----------------------------------------------------------------
    private void setupRecyclerView() {
        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged(CartAdapter.CartItem item) {
                // Cập nhật số lượng lên Firebase
                updateQtyOnFirebase(item);
                updateTotal();
            }

            @Override
            public void onItemDeleted(CartAdapter.CartItem item) {
                // Xoá item khỏi Firebase
                deleteItemOnFirebase(item);
                updateTotal();
            }
        });
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);
    }

    // ----------------------------------------------------------------
    // Khởi tạo DatabaseReference đến carts/{userId}
    // ----------------------------------------------------------------
    private void setupCartRef() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cartRef = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference("carts")
                .child(userId);
    }

    // ----------------------------------------------------------------
    // Load giỏ hàng từ Firebase
    // ----------------------------------------------------------------
    private void loadCartFromFirebase() {
        if (cartRef == null) return;

        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String productId = child.getKey();
                    String ten       = child.child("ten").getValue(String.class);
                    Long   giaLong   = child.child("gia").getValue(Long.class);
                    Long   soLuongL  = child.child("soLuong").getValue(Long.class);

                    // Lấy link ảnh từ dữ liệu giỏ hàng trên Firebase
                    String anhUrl    = child.child("anhUrl").getValue(String.class);

                    if (ten == null) continue;

                    long gia      = giaLong   != null ? giaLong   : 0;
                    int  soLuong  = soLuongL  != null ? soLuongL.intValue() : 1;

                    // Gán giá trị mặc định là chuỗi rỗng nếu không có ảnh
                    String safeAnhUrl = anhUrl != null ? anhUrl : "";

                    // Cập nhật constructor để truyền thêm safeAnhUrl
                    cartItems.add(new CartAdapter.CartItem(
                            productId, ten, gia, soLuong, safeAnhUrl));
                }
                adapter.notifyDataSetChanged();
                updateTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this,
                        "Không tải được giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Tính & hiển thị tổng tiền
    // ----------------------------------------------------------------
    private void updateTotal() {
        long total = 0;
        for (CartAdapter.CartItem item : cartItems) {
            total += item.gia * item.quantity;
        }
        String formatted = NumberFormat.getNumberInstance(Locale.US)
                .format(total).replace(",", ".") + "đ";
        binding.tvTotal.setText(formatted);
    }

    // ----------------------------------------------------------------
    // Cập nhật số lượng 1 item trên Firebase
    // ----------------------------------------------------------------
    private void updateQtyOnFirebase(CartAdapter.CartItem item) {
        if (cartRef == null || item.productId == null) return;
        cartRef.child(item.productId).child("soLuong").setValue(item.quantity);
    }

    // ----------------------------------------------------------------
    // Xoá 1 item khỏi Firebase
    // ----------------------------------------------------------------
    private void deleteItemOnFirebase(CartAdapter.CartItem item) {
        if (cartRef == null || item.productId == null) return;
        cartRef.child(item.productId).removeValue();
    }
}