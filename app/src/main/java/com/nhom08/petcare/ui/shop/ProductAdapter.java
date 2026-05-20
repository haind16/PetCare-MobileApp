package com.nhom08.petcare.ui.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter quản lý việc hiển thị danh sách sản phẩm trong Cửa hàng.
 * Hiển thị thông tin sản phẩm (tên, giá, hình ảnh, số lượng đã bán) và xử lý sự kiện thêm vào giỏ hàng.
 */
public class ProductAdapter extends
        RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    /**
     * Model đại diện cho một sản phẩm trong cửa hàng.
     * Ánh xạ với các thuộc tính trong node "products" của Firebase Realtime Database.
     */
    public static class ProductItem {
        public String id;       // ID duy nhất của sản phẩm
        public String name;     // Tên sản phẩm
        public String danhMuc;  // Danh mục sản phẩm (Thức ăn, Phụ kiện, Thuốc)
        public String moTa;     // Mô tả chi tiết sản phẩm
        public long   daBan;    // Số lượng sản phẩm đã bán thành công
        public long   gia;      // Giá sản phẩm (VNĐ)
        public String anhUrl;   // Đường dẫn ảnh sản phẩm từ Firebase/Cloudinary

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

        /**
         * Định dạng giá tiền sang chuỗi hiển thị thân thiện (Ví dụ: 100.000đ).
         * @return Chuỗi giá đã định dạng.
         */
        public String getGiaFormatted() {
            return NumberFormat.getNumberInstance(Locale.US)
                    .format(gia).replace(",", ".") + "đ";
        }
    }

    /**
     * Interface lắng nghe sự kiện thêm sản phẩm vào giỏ hàng.
     */
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

        // Tải ảnh sản phẩm sử dụng Glide
        Glide.with(holder.itemView.getContext())
                .load(item.anhUrl)
                .placeholder(R.drawable.pet_welcome)
                .error(R.drawable.pet_welcome)
                .into(holder.imgProduct);

        // Sự kiện khi người dùng nhấn nút "Thêm vào giỏ"
        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

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