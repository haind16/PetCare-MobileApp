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

/**
 * Activity hiển thị giỏ hàng của người dùng.
 * Cho phép xem danh sách các sản phẩm đã chọn, thay đổi số lượng, xóa sản phẩm và tiến hành thanh toán.
 */
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

        // Nút xem lịch sử các đơn hàng đã mua
        binding.btnOrderHistory.setOnClickListener(v ->
                startActivity(new Intent(this, OrderHistoryActivity.class)));

        setupRecyclerView();
        setupCartRef();
        loadCartFromFirebase();

        // Xử lý chuyển sang màn hình thanh toán
        binding.btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CheckoutActivity.class));
            }
        });
    }

    /**
     * Thiết lập danh sách sản phẩm trong giỏ hàng.
     */
    private void setupRecyclerView() {
        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onQuantityChanged(CartAdapter.CartItem item) {
                // Cập nhật số lượng lên Firebase khi thay đổi trên giao diện
                updateQtyOnFirebase(item);
                updateTotal();
            }
            @Override
            public void onItemDeleted(CartAdapter.CartItem item) {
                // Xóa sản phẩm khỏi giỏ hàng trên Firebase
                deleteItemOnFirebase(item);
                updateTotal();
            }
        });
        binding.rvCart.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCart.setAdapter(adapter);
    }

    /**
     * Tham chiếu đến node "carts" của người dùng hiện tại trên Firebase.
     */
    private void setupCartRef() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        cartRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("carts").child(userId);
    }

    /**
     * Lắng nghe dữ liệu giỏ hàng từ Firebase và cập nhật giao diện.
     */
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
                    String anhUrl    = child.child("anhUrl").getValue(String.class);
                    if (ten == null) continue;
                    long gia     = giaLong  != null ? giaLong  : 0;
                    int  soLuong = soLuongL != null ? soLuongL.intValue() : 1;
                    cartItems.add(new CartAdapter.CartItem(
                            productId, ten, gia, soLuong,
                            anhUrl != null ? anhUrl : ""));
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

    /**
     * Tính toán và hiển thị tổng tiền của các sản phẩm trong giỏ hàng.
     */
    private void updateTotal() {
        long total = 0;
        for (CartAdapter.CartItem item : cartItems) total += item.gia * item.quantity;
        binding.tvTotal.setText(NumberFormat.getNumberInstance(Locale.US)
                .format(total).replace(",", ".") + "đ");
    }

    /**
     * Cập nhật số lượng sản phẩm lên Firebase.
     */
    private void updateQtyOnFirebase(CartAdapter.CartItem item) {
        if (cartRef == null || item.productId == null) return;
        cartRef.child(item.productId).child("soLuong").setValue(item.quantity);
    }

    /**
     * Xóa một sản phẩm khỏi node carts trên Firebase.
     */
    private void deleteItemOnFirebase(CartAdapter.CartItem item) {
        if (cartRef == null || item.productId == null) return;
        cartRef.child(item.productId).removeValue();
    }
}