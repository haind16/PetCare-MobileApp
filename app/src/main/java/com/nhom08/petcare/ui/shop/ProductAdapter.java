package com.nhom08.petcare.ui.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import java.util.List;

public class ProductAdapter extends
        RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public static class ProductItem {
        public String name, sold, price;
        public ProductItem(String name, String sold, String price) {
            this.name = name;
            this.sold = sold;
            this.price = price;
        }
    }

    public interface OnAddToCartListener {
        void onAddToCart(ProductItem item);
    }

    private List<ProductItem> list;
    private OnAddToCartListener listener;

    public ProductAdapter(List<ProductItem> list,
                          OnAddToCartListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder,
                                 int position) {
        ProductItem item = list.get(position);
        holder.tvProductName.setText(item.name);
        holder.tvProductId.setText("Đã bán: " + item.sold);
        holder.tvPrice.setText(item.price);
        holder.btnAddToCart.setOnClickListener(v -> {
            if (listener != null) listener.onAddToCart(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductId, tvPrice;
        Button btnAddToCart;

        ProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}