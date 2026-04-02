package com.nhom08.petcare.ui.shop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityOrderHistoryBinding;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderHistoryActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private ActivityOrderHistoryBinding binding;
    private OrderHistoryAdapter adapter;
    private final List<OrderItem> orderList = new ArrayList<>();

    // Model đơn hàng
    public static class OrderItem {
        public String orderId, thoiGian, trangThai, phuongThucTT;
        public long tongTien;
        public List<String> sanPhamNames = new ArrayList<>();

        public String getTrangThaiText() {
            if (trangThai == null) return "Không rõ";
            switch (trangThai) {
                case "cho_xac_nhan": return "Chờ xác nhận";
                case "da_xac_nhan":  return "Đã xác nhận";
                case "dang_giao":    return "Đang giao";
                case "da_giao":      return "Đã giao";
                case "da_thanh_toan":return "Đã thanh toán";
                case "da_huy":       return "Đã huỷ";
                default:             return trangThai;
            }
        }

        public int getTrangThaiColor() {
            if (trangThai == null) return 0xFF888888;
            switch (trangThai) {
                case "da_giao":
                case "da_thanh_toan": return 0xFF4CAF50; // xanh lá
                case "da_huy":        return 0xFFF44336; // đỏ
                default:              return 0xFFFF9800; // cam
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new OrderHistoryAdapter(orderList);
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setAdapter(adapter);

        loadOrders();
    }

    private void loadOrders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) return;

        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("orders")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderList.clear();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            OrderItem order = new OrderItem();
                            order.orderId      = child.getKey();
                            order.thoiGian     = child.child("thoiGian").getValue(String.class);
                            order.trangThai    = child.child("trangThai").getValue(String.class);
                            order.phuongThucTT = child.child("phuongThucTT").getValue(String.class);
                            Long tongTienL     = child.child("tongTien").getValue(Long.class);
                            order.tongTien     = tongTienL != null ? tongTienL : 0;

                            // Lấy tên sản phẩm trong đơn
                            for (DataSnapshot sp : child.child("danhSachSanPham").getChildren()) {
                                String ten = sp.child("ten").getValue(String.class);
                                if (ten != null) order.sanPhamNames.add(ten);
                            }
                            orderList.add(order);
                        }

                        // Sắp xếp mới nhất lên đầu (orderId Firebase push key tăng dần)
                        java.util.Collections.reverse(orderList);

                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvEmpty.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                        binding.rvOrders.setVisibility(orderList.isEmpty() ? View.GONE : View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    // ── Adapter ──────────────────────────────────────────────────────────────

    static class OrderHistoryAdapter
            extends RecyclerView.Adapter<OrderHistoryAdapter.VH> {

        private final List<OrderItem> list;
        OrderHistoryAdapter(List<OrderItem> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_history, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            OrderItem order = list.get(position);

            holder.tvTime.setText(order.thoiGian != null ? order.thoiGian : "");
            holder.tvStatus.setText(order.getTrangThaiText());
            holder.tvStatus.setTextColor(order.getTrangThaiColor());
            holder.tvTotal.setText(
                    NumberFormat.getNumberInstance(Locale.US)
                            .format(order.tongTien).replace(",", ".") + "đ");

            // Tên sản phẩm: ghép lại, tối đa 2 cái + "..."
            if (!order.sanPhamNames.isEmpty()) {
                String names = order.sanPhamNames.get(0);
                if (order.sanPhamNames.size() > 1)
                    names += ", " + order.sanPhamNames.get(1);
                if (order.sanPhamNames.size() > 2)
                    names += "...";
                holder.tvProducts.setText(names);
            } else {
                holder.tvProducts.setText("Không có sản phẩm");
            }

            // Phương thức thanh toán
            String pttt = order.phuongThucTT;
            if      ("cod".equals(pttt))  holder.tvPayment.setText("COD");
            else if ("bank".equals(pttt)) holder.tvPayment.setText("Chuyển khoản");
            else if ("qr".equals(pttt))   holder.tvPayment.setText("QR Code");
            else                          holder.tvPayment.setText(pttt != null ? pttt : "");
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTime, tvStatus, tvTotal, tvProducts, tvPayment;
            VH(View v) {
                super(v);
                tvTime     = v.findViewById(R.id.tvOrderTime);
                tvStatus   = v.findViewById(R.id.tvOrderStatus);
                tvTotal    = v.findViewById(R.id.tvOrderTotal);
                tvProducts = v.findViewById(R.id.tvOrderProducts);
                tvPayment  = v.findViewById(R.id.tvOrderPayment);
            }
        }
    }
}