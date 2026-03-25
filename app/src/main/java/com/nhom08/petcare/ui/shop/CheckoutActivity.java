package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityCheckoutBinding;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private static final long PHI_SHIP = 15_000L; // phí ship cố định

    private ActivityCheckoutBinding binding;
    private String selectedPayment = "cod";

    // Giỏ hàng load từ Firebase
    private final List<CartAdapter.CartItem> cartItems = new ArrayList<>();
    private long tongTienSanPham = 0; // tổng tiền sản phẩm (chưa ship)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        setupOrderItemsRecyclerView();
        setupPaymentOptions();
        loadCartAndUpdateUI();

        // Phí ship luôn cố định 15.000đ
        binding.tvShipping.setText(formatVnd(PHI_SHIP));

        binding.btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    // ----------------------------------------------------------------
    // RecyclerView hiển thị danh sách sản phẩm trong đơn hàng
    // ----------------------------------------------------------------
    private void setupOrderItemsRecyclerView() {
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderItems.setNestedScrollingEnabled(false);
    }

    // ----------------------------------------------------------------
    // Load giỏ hàng từ Firebase → hiển thị + tính tổng tiền
    // ----------------------------------------------------------------
    private void loadCartAndUpdateUI() {
        String userId = getCurrentUserId();
        if (userId == null) return;

        DatabaseReference cartRef = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference("carts")
                .child(userId);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                tongTienSanPham = 0;

                for (DataSnapshot child : snapshot.getChildren()) {
                    String productId = child.getKey();
                    String ten       = child.child("ten").getValue(String.class);
                    Long   giaLong   = child.child("gia").getValue(Long.class);
                    Long   soLuongL  = child.child("soLuong").getValue(Long.class);

                    if (ten == null) continue;

                    long gia     = giaLong  != null ? giaLong  : 0;
                    int  soLuong = soLuongL != null ? soLuongL.intValue() : 1;

                    cartItems.add(new CartAdapter.CartItem(productId, ten, gia, soLuong));
                    tongTienSanPham += gia * soLuong;
                }

                // Gắn adapter hiển thị danh sách sản phẩm (read-only)
                binding.rvOrderItems.setAdapter(new OrderItemAdapter(cartItems));

                // Cập nhật tổng tiền
                updateTotalUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this,
                        "Không tải được giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Cập nhật hiển thị tổng tiền
    // ----------------------------------------------------------------
    private void updateTotalUI() {
        binding.tvSubtotal.setText(formatVnd(tongTienSanPham));
        binding.tvShipping.setText(formatVnd(PHI_SHIP));
        binding.tvTotal.setText(formatVnd(tongTienSanPham + PHI_SHIP));
    }

    // ----------------------------------------------------------------
    // Xử lý xác nhận đặt hàng
    // ----------------------------------------------------------------
    private void handleConfirm() {
        // Chặn nếu giỏ hàng rỗng
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống, vui lòng thêm sản phẩm!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnConfirm.setEnabled(false);
        saveOrder(userId);
    }

    // ----------------------------------------------------------------
    // Lưu đơn hàng lên Firebase → orders/{userId}/{autoKey}
    // ----------------------------------------------------------------
    private void saveOrder(String userId) {
        // Build danh sách sản phẩm
        List<Map<String, Object>> danhSach = new ArrayList<>();
        for (CartAdapter.CartItem item : cartItems) {
            Map<String, Object> sp = new HashMap<>();
            sp.put("ten",       item.name);
            sp.put("gia",       item.gia);
            sp.put("soLuong",   item.quantity);
            sp.put("thanhTien", item.gia * item.quantity);
            danhSach.add(sp);
        }

        String thoiGian = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        Map<String, Object> order = new HashMap<>();
        order.put("userId",           userId);
        order.put("thoiGian",         thoiGian);
        order.put("phuongThucTT",     selectedPayment);
        order.put("tongTienSanPham",  tongTienSanPham);
        order.put("phiShip",          PHI_SHIP);
        order.put("tongTien",         tongTienSanPham + PHI_SHIP);
        order.put("trangThai",        "cho_xac_nhan");
        order.put("danhSachSanPham",  danhSach);

        DatabaseReference ordersRef = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference("orders")
                .child(userId);

        DatabaseReference cartRef = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference("carts")
                .child(userId);

        ordersRef.push().setValue(order)
                .addOnSuccessListener(unused -> {
                    // Xoá giỏ hàng sau khi lưu đơn
                    cartRef.removeValue().addOnSuccessListener(unused2 -> {
                        if ("qr".equals(selectedPayment)) {
                            startActivity(new Intent(this, QRPaymentActivity.class));
                            finish();
                        } else if ("bank".equals(selectedPayment)) {
                            startActivity(new Intent(this, BankTransferActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Đặt hàng thành công! 🎉",
                                    Toast.LENGTH_LONG).show();
                            goBackToShop();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu đơn hàng!", Toast.LENGTH_SHORT).show();
                    binding.btnConfirm.setEnabled(true);
                });
    }

    // ----------------------------------------------------------------
    // Setup chọn phương thức thanh toán (giữ nguyên logic cũ)
    // ----------------------------------------------------------------
    private void setupPaymentOptions() {
        binding.rbCOD.setBackgroundResource(R.drawable.bg_payment_selected);
        binding.rbBank.setBackgroundResource(R.drawable.edit_text_border);
        binding.rbQR.setBackgroundResource(R.drawable.edit_text_border);
        binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_money, 0, R.drawable.ic_radio_on, 0);
        binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_bank, 0, R.drawable.ic_radio_off, 0);
        binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_qr, 0, R.drawable.ic_radio_off, 0);

        binding.rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            binding.rbCOD.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbBank.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbQR.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_money, 0, R.drawable.ic_radio_off, 0);
            binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_bank, 0, R.drawable.ic_radio_off, 0);
            binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_qr, 0, R.drawable.ic_radio_off, 0);

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
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;
    }

    private String formatVnd(long amount) {
        return NumberFormat.getNumberInstance(Locale.US)
                .format(amount).replace(",", ".") + "đ";
    }

    private void goBackToShop() {
        Intent intent = new Intent(this,
                com.nhom08.petcare.ui.main.MainActivity.class);
        intent.putExtra("nav_to", "shop");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // ----------------------------------------------------------------
    // Adapter nội bộ — hiển thị danh sách sản phẩm read-only trong checkout
    // ----------------------------------------------------------------
    private static class OrderItemAdapter
            extends RecyclerView.Adapter<OrderItemAdapter.VH> {

        private final List<CartAdapter.CartItem> list;

        OrderItemAdapter(List<CartAdapter.CartItem> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Dùng lại layout item_cart nhưng ẩn các nút +/-/xoá
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(com.nhom08.petcare.R.layout.item_cart, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            CartAdapter.CartItem item = list.get(position);

            holder.tvName.setText(item.name);
            holder.tvPrice.setText(
                    NumberFormat.getNumberInstance(Locale.US)
                            .format(item.gia).replace(",", ".") + "đ");
            holder.tvQty.setText("x" + item.quantity);

            // Ẩn các nút tương tác — đây là màn hình xem, không chỉnh sửa
            holder.btnPlus.setVisibility(View.GONE);
            holder.btnMinus.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice, tvQty;
            View     btnPlus, btnMinus, btnDelete;

            VH(View v) {
                super(v);
                tvName    = v.findViewById(R.id.tvProductName);
                tvPrice   = v.findViewById(R.id.tvPrice);
                tvQty     = v.findViewById(R.id.tvQuantity);
                btnPlus   = v.findViewById(R.id.btnPlus);
                btnMinus  = v.findViewById(R.id.btnMinus);
                btnDelete = v.findViewById(R.id.btnDelete);
            }
        }
    }
}