package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Cần import thư viện Glide
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

    private static final String DB_URL  = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final long   PHI_SHIP = 15_000L;

    private ActivityCheckoutBinding          binding;
    private String                           selectedPayment = "cod";
    private final List<CartAdapter.CartItem> cartItems       = new ArrayList<>();
    private long                             tongTienSanPham = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        setupOrderItemsRecyclerView();
        setupPaymentOptions();
        loadCartAndUpdateUI();
        loadCustomerInfo();

        binding.tvShipping.setText(formatVnd(PHI_SHIP));
        binding.btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.btnConfirm.setEnabled(true);
    }

    // ----------------------------------------------------------------
    // Load thông tin khách hàng từ Firebase
    // ----------------------------------------------------------------
    private void loadCustomerInfo() {
        String userId = getCurrentUserId();
        if (userId == null) return;

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String displayName = snapshot.child("displayName").getValue(String.class);
                        String phone       = snapshot.child("phone").getValue(String.class);
                        String address     = snapshot.child("address").getValue(String.class);

                        binding.tvCustomerName.setText(
                                (displayName != null && !displayName.isEmpty())
                                        ? displayName : "Chưa cập nhật tên");

                        binding.tvPhone.setText(
                                (phone != null && !phone.isEmpty())
                                        ? phone : "Chưa có SĐT");

                        binding.tvAddress.setText("Địa chỉ: " +
                                ((address != null && !address.isEmpty())
                                        ? address : "Chưa cập nhật"));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    // ----------------------------------------------------------------
    // RecyclerView danh sách sản phẩm (read-only)
    // ----------------------------------------------------------------
    private void setupOrderItemsRecyclerView() {
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderItems.setNestedScrollingEnabled(false);
    }

    // ----------------------------------------------------------------
    // Load giỏ hàng từ Firebase
    // ----------------------------------------------------------------
    private void loadCartAndUpdateUI() {
        String userId = getCurrentUserId();
        if (userId == null) return;

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("carts")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        cartItems.clear();
                        tongTienSanPham = 0;

                        for (DataSnapshot child : snapshot.getChildren()) {
                            String productId = child.getKey();
                            String ten       = child.child("ten").getValue(String.class);
                            Long   giaLong   = child.child("gia").getValue(Long.class);
                            Long   soLuongL  = child.child("soLuong").getValue(Long.class);
                            String anhUrl    = child.child("anhUrl").getValue(String.class);

                            if (ten == null) continue;

                            long gia     = giaLong  != null ? giaLong  : 0;
                            int  soLuong = soLuongL != null ? soLuongL.intValue() : 1;
                            String safeAnhUrl = anhUrl != null ? anhUrl : "";

                            cartItems.add(new CartAdapter.CartItem(productId, ten, gia, soLuong, safeAnhUrl));
                            tongTienSanPham += gia * soLuong;
                        }

                        binding.rvOrderItems.setAdapter(new OrderItemAdapter(cartItems));
                        updateTotalUI();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CheckoutActivity.this,
                                "Không tải được giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTotalUI() {
        binding.tvSubtotal.setText(formatVnd(tongTienSanPham));
        binding.tvShipping.setText(formatVnd(PHI_SHIP));
        binding.tvTotal.setText(formatVnd(tongTienSanPham + PHI_SHIP));
    }

    // ----------------------------------------------------------------
    // Xử lý xác nhận đặt hàng
    // ----------------------------------------------------------------
    private void handleConfirm() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống, vui lòng thêm sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        String phone   = binding.tvPhone.getText().toString();
        String address = binding.tvAddress.getText().toString();
        if (phone.contains("Chưa") || address.contains("Chưa")) {
            Toast.makeText(this, "Vui lòng cập nhật số điện thoại và địa chỉ trong hồ sơ cá nhân trước!", Toast.LENGTH_LONG).show();
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
    // Lưu đơn hàng lên Firebase
    // ----------------------------------------------------------------
    private void saveOrder(String userId) {
        List<Map<String, Object>> danhSach = new ArrayList<>();
        for (CartAdapter.CartItem item : cartItems) {
            Map<String, Object> sp = new HashMap<>();
            sp.put("ten",       item.name);
            sp.put("gia",       item.gia);
            sp.put("soLuong",   item.quantity);
            sp.put("thanhTien", item.gia * item.quantity);
            sp.put("anhUrl",    item.anhUrl); // <-- LƯU ẢNH VÀO ĐƠN HÀNG ĐỂ DÙNG SAU NÀY
            danhSach.add(sp);
        }

        String thoiGian = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        String tenKH   = binding.tvCustomerName.getText().toString();
        String sdt     = binding.tvPhone.getText().toString();
        String diaChi  = binding.tvAddress.getText().toString().replace("Địa chỉ: ", "");

        Map<String, Object> order = new HashMap<>();
        order.put("userId",          userId);
        order.put("tenKhachHang",    tenKH);
        order.put("soDienThoai",     sdt);
        order.put("diaChiGiaoHang",  diaChi);
        order.put("thoiGian",        thoiGian);
        order.put("phuongThucTT",    selectedPayment);
        order.put("tongTienSanPham", tongTienSanPham);
        order.put("phiShip",         PHI_SHIP);
        order.put("tongTien",        tongTienSanPham + PHI_SHIP);
        order.put("trangThai",       "cho_xac_nhan");
        order.put("danhSachSanPham", danhSach);

        DatabaseReference ordersRef = FirebaseDatabase.getInstance(DB_URL).getReference("orders").child(userId);
        DatabaseReference cartRef = FirebaseDatabase.getInstance(DB_URL).getReference("carts").child(userId);

        ordersRef.push().setValue(order)
                .addOnSuccessListener(unused -> {
                    if ("qr".equals(selectedPayment)) {
                        startActivity(new Intent(this, QRPaymentActivity.class));
                    } else if ("bank".equals(selectedPayment)) {
                        startActivity(new Intent(this, BankTransferActivity.class));
                    } else {
                        cartRef.removeValue().addOnSuccessListener(unused2 -> {
                            Toast.makeText(this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                            goBackToShop();
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi lưu đơn hàng!", Toast.LENGTH_SHORT).show();
                    binding.btnConfirm.setEnabled(true);
                });
    }

    // ----------------------------------------------------------------
    // Setup phương thức thanh toán
    // ----------------------------------------------------------------
    private void setupPaymentOptions() {
        binding.rbCOD.setBackgroundResource(R.drawable.bg_payment_selected);
        binding.rbBank.setBackgroundResource(R.drawable.edit_text_border);
        binding.rbQR.setBackgroundResource(R.drawable.edit_text_border);
        binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, R.drawable.ic_radio_on,  0);
        binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank,  0, R.drawable.ic_radio_off, 0);
        binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_qr,    0, R.drawable.ic_radio_off, 0);

        binding.rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            binding.rbCOD.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbBank.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbQR.setBackgroundResource(R.drawable.edit_text_border);
            binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, R.drawable.ic_radio_off, 0);
            binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank,  0, R.drawable.ic_radio_off, 0);
            binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_qr,    0, R.drawable.ic_radio_off, 0);

            if (checkedId == R.id.rbCOD) {
                selectedPayment = "cod";
                binding.rbCOD.setBackgroundResource(R.drawable.bg_payment_selected);
                binding.rbCOD.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, R.drawable.ic_radio_on, 0);
            } else if (checkedId == R.id.rbBank) {
                selectedPayment = "bank";
                binding.rbBank.setBackgroundResource(R.drawable.bg_payment_selected);
                binding.rbBank.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank, 0, R.drawable.ic_radio_on, 0);
            } else if (checkedId == R.id.rbQR) {
                selectedPayment = "qr";
                binding.rbQR.setBackgroundResource(R.drawable.bg_payment_selected);
                binding.rbQR.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_qr, 0, R.drawable.ic_radio_on, 0);
            }
        });
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    private String formatVnd(long amount) {
        return NumberFormat.getNumberInstance(Locale.US)
                .format(amount).replace(",", ".") + "đ";
    }

    private void goBackToShop() {
        Intent intent = new Intent(this, com.nhom08.petcare.ui.main.MainActivity.class);
        intent.putExtra("nav_to", "shop");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    // ----------------------------------------------------------------
    // Adapter hiển thị sản phẩm trong checkout (Đã cập nhật Glide)
    // ----------------------------------------------------------------
    private class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.VH> { // Bỏ chữ static

        private final List<CartAdapter.CartItem> list;

        OrderItemAdapter(List<CartAdapter.CartItem> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart, parent, false);
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

            holder.btnPlus.setVisibility(View.GONE);
            holder.btnMinus.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

            // Dùng Glide tải ảnh vào imgProduct
            Glide.with(holder.itemView.getContext())
                    .load(item.anhUrl)
                    .placeholder(R.drawable.pet_welcome)
                    .error(R.drawable.pet_welcome)
                    .into(holder.imgProduct);
        }

        @Override
        public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice, tvQty;
            View     btnPlus, btnMinus, btnDelete;
            ImageView imgProduct; // Thêm biến chứa ImageView

            VH(View v) {
                super(v);
                tvName    = v.findViewById(R.id.tvProductName);
                tvPrice   = v.findViewById(R.id.tvPrice);
                tvQty     = v.findViewById(R.id.tvQuantity);
                btnPlus   = v.findViewById(R.id.btnPlus);
                btnMinus  = v.findViewById(R.id.btnMinus);
                btnDelete = v.findViewById(R.id.btnDelete);
                imgProduct = v.findViewById(R.id.imgProduct); // Ánh xạ ID
            }
        }
    }
}