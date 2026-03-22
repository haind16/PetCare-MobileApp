package com.nhom08.petcare.ui.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import java.util.List;

public class CartAdapter extends
        RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public static class CartItem {
        public String name, sold, price;
        public int quantity;
        public CartItem(String name, String sold, String price, int qty) {
            this.name = name;
            this.sold = sold;
            this.price = price;
            this.quantity = qty;
        }
    }

    private List<CartItem> list;

    public CartAdapter(List<CartItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder,
                                 int position) {
        CartItem item = list.get(position);
        holder.tvProductName.setText(item.name);
        holder.tvProductId.setText("Đã bán: " + item.sold);
        holder.tvPrice.setText(item.price);
        holder.tvQuantity.setText(String.valueOf(item.quantity));

        holder.btnPlus.setOnClickListener(v -> {
            item.quantity++;
            holder.tvQuantity.setText(String.valueOf(item.quantity));
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                holder.tvQuantity.setText(String.valueOf(item.quantity));
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductId, tvPrice, tvQuantity;
        TextView btnPlus, btnMinus;
        ImageView btnDelete;

        CartViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}