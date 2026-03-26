package com.nhom08.petcare.ui.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Nhớ import thư viện Glide
import com.nhom08.petcare.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends
        RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    // ----------------------------------------------------------------
    // Model — ánh xạ với Firebase carts/{userId}/{productId}
    // ----------------------------------------------------------------
    public static class CartItem {
        public String productId; // key Firebase (product_1, ...)
        public String name;      // ten
        public long   gia;       // gia
        public int    quantity;  // soLuong
        public String anhUrl;    // Thêm trường anhUrl

        public CartItem(String productId, String name, long gia, int quantity, String anhUrl) {
            this.productId = productId;
            this.name      = name;
            this.gia       = gia;
            this.quantity  = quantity;
            this.anhUrl    = anhUrl; // Khởi tạo anhUrl
        }

        /** Trả về giá 1 đơn vị đã format, ví dụ: "120.000đ" */
        public String getGiaFormatted() {
            return NumberFormat.getNumberInstance(Locale.US)
                    .format(gia).replace(",", ".") + "đ";
        }
    }

    // ----------------------------------------------------------------
    // Listener — CartActivity lắng nghe thay đổi để sync Firebase
    // ----------------------------------------------------------------
    public interface OnCartChangeListener {
        void onQuantityChanged(CartItem item);
        void onItemDeleted(CartItem item);
    }

    private final List<CartItem>       list;
    private final OnCartChangeListener listener;

    public CartAdapter(List<CartItem> list, OnCartChangeListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = list.get(position);

        holder.tvProductName.setText(item.name);
        holder.tvPrice.setText(item.getGiaFormatted());
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        // Sử dụng Glide để tải ảnh sản phẩm trong giỏ hàng
        Glide.with(holder.itemView.getContext())
                .load(item.anhUrl)
                .placeholder(R.drawable.pet_welcome) // Ảnh mặc định trong lúc chờ tải
                .error(R.drawable.pet_welcome)       // Ảnh mặc định nếu lỗi hoặc anhUrl rỗng
                .into(holder.imgProduct);

        // Nút tăng số lượng
        holder.btnPlus.setOnClickListener(v -> {
            item.quantity++;
            holder.tvQuantity.setText(String.valueOf(item.quantity));
            if (listener != null) listener.onQuantityChanged(item);
        });

        // Nút giảm số lượng (tối thiểu 1)
        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                holder.tvQuantity.setText(String.valueOf(item.quantity));
                if (listener != null) listener.onQuantityChanged(item);
            }
        });

        // Nút xoá item
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onItemDeleted(item);
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    // ----------------------------------------------------------------
    // ViewHolder — ID khớp với item_cart.xml
    // ----------------------------------------------------------------
    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView  tvProductName, tvPrice, tvQuantity;
        TextView  btnPlus, btnMinus;
        ImageView btnDelete;
        ImageView imgProduct; // Thêm ImageView cho ảnh sản phẩm

        CartViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice       = itemView.findViewById(R.id.tvPrice);
            tvQuantity    = itemView.findViewById(R.id.tvQuantity);
            btnPlus       = itemView.findViewById(R.id.btnPlus);
            btnMinus      = itemView.findViewById(R.id.btnMinus);
            btnDelete     = itemView.findViewById(R.id.btnDelete);

            // Đảm bảo ID này khớp với ImageView ảnh sản phẩm trong file item_cart.xml của bạn
            imgProduct    = itemView.findViewById(R.id.imgProduct);
        }
    }
}