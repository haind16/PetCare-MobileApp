package com.nhom08.petcare.ui.health.info;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import java.util.ArrayList;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    public static class InfoItem {
        public String name;
        public String desc;
        public String tag;
        public String tagColor;
        public String anhUrl;    // Ảnh icon vuông nhỏ
        public String anhBiaUrl; // THÊM DÒNG NÀY: Ảnh bìa ngang to

        public String field1;
        public String field2;
        public String field3;
        public String field4;

        // Cập nhật hàm khởi tạo
        public InfoItem(String name, String desc, String tag, String tagColor, String anhUrl, String anhBiaUrl) {
            this.name      = name;
            this.desc      = desc;
            this.tag       = tag;
            this.tagColor  = tagColor;
            this.anhUrl    = anhUrl;
            this.anhBiaUrl = anhBiaUrl; // Gán giá trị
        }
    }

    public interface OnItemClickListener { void onClick(InfoItem item); }

    private final List<InfoItem>       allList     = new ArrayList<>();
    private final List<InfoItem>       displayList = new ArrayList<>();
    private final OnItemClickListener  listener;

    public InfoAdapter(List<InfoItem> list, OnItemClickListener listener) {
        this.allList.addAll(list);
        this.displayList.addAll(list);
        this.listener = listener;
    }

    public void setData(List<InfoItem> newList) {
        allList.clear(); allList.addAll(newList);
        displayList.clear(); displayList.addAll(newList);
        notifyDataSetChanged();
    }

    public void filter(String keyword) {
        displayList.clear();
        if (keyword == null || keyword.trim().isEmpty()) {
            displayList.addAll(allList);
        } else {
            String kw = keyword.toLowerCase().trim();
            for (InfoItem item : allList) {
                if (item.name.toLowerCase().contains(kw) || item.desc.toLowerCase().contains(kw) || item.tag.toLowerCase().contains(kw)) {
                    displayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = displayList.get(position);
        holder.tvName.setText(item.name);
        holder.tvDesc.setText(item.desc);
        holder.tvTag.setText(item.tag);

        // Dùng Glide tải ảnh thu nhỏ ở danh sách (Dùng anhUrl)
        Glide.with(holder.itemView.getContext())
                .load(item.anhUrl)
                .placeholder(R.drawable.pet_welcome)
                .error(R.drawable.pet_welcome)
                .into(holder.imgItem);

        try { holder.tvTag.getBackground().setTint(Color.parseColor(item.tagColor)); } catch (Exception ignored) {}
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(item); });
    }

    @Override
    public int getItemCount() { return displayList.size(); }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView  tvName, tvDesc, tvTag;
        InfoViewHolder(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvName  = itemView.findViewById(R.id.tvName);
            tvDesc  = itemView.findViewById(R.id.tvDesc);
            tvTag   = itemView.findViewById(R.id.tvTag);
        }
    }
}