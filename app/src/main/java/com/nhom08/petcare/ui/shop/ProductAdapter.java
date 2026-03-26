package com.nhom08.petcare.ui.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Nhớ import thư viện Glide
import com.nhom08.petcare.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends
        RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    // ----------------------------------------------------------------
    // Model — ánh xạ 1-1 với Firebase products node
    // ----------------------------------------------------------------
    public static class ProductItem {
        public String id;       // key Firebase (product_1, ...)
        public String name;     // ten
        public String danhMuc;  // danhMuc
        public String moTa;     // moTa
        public long   daBan;    // daBan
        public long   gia;      // gia (số nguyên, đơn vị VNĐ)
        public String anhUrl;   // Thêm trường chứa link ảnh

        public ProductItem(String id, String name, String danhMuc,
                           String moTa, long daBan, long gia, String anhUrl) {
            this.id      = id;
            this.name    = name;
            this.danhMuc = danhMuc;
            this.moTa    = moTa;
            this.daBan   = daBan;
            this.gia     = gia;
            this.anhUrl  = anhUrl;
        }

        /** Trả về giá đã format, ví dụ: "120.000đ" */
        public String getGiaFormatted() {
            return NumberFormat.getNumberInstance(Locale.US)
                    .format(gia).replace(",", ".") + "đ";
        }
    }

    public interface OnAddToCartListener {
        void onAddToCart(ProductItem item);
    }

    private final List<ProductItem>   list;
    private final OnAddToCartListener listener;

    public ProductAdapter(List<ProductItem> list, OnAddToCartListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem item = list.get(position);

        holder.tvProductName.setText(item.name);
        holder.tvProductId.setText("Đã bán: " + item.daBan);
        holder.tvPrice.setText(item.getGiaFormatted());

        // Sử dụng Glide để tải ảnh
        Glide.with(holder.itemView.getContext())
                .load(item.anhUrl)
                .placeholder(R.drawable.pet_welcome) // Ảnh chờ tải
                .error(R.drawable.pet_welcome)       // Ảnh nếu lỗi hoặc không có URL
                .into(holder.imgProduct);

        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    // ----------------------------------------------------------------
    // ViewHolder — ID khớp với item_product.xml
    // ----------------------------------------------------------------
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView  tvProductName, tvProductId, tvPrice;
        Button    btnAddToCart;
        ImageView imgProduct;

        ProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductId   = itemView.findViewById(R.id.tvProductId);
            tvPrice       = itemView.findViewById(R.id.tvPrice);
            btnAddToCart  = itemView.findViewById(R.id.btnAddToCart);
            imgProduct    = itemView.findViewById(R.id.imgProduct);
        }
    }
}