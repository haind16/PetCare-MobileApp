package com.nhom08.petcare.ui.health.info;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import java.util.ArrayList;
import java.util.List;

public class InfoAdapter extends
        RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    // ----------------------------------------------------------------
    // Model — dùng chung cho benh_ly / dinh_duong / phong_kham
    // Các trường extra được pass sang InfoDetailActivity qua Intent
    // ----------------------------------------------------------------
    public static class InfoItem {
        public String name;
        public String desc;
        public String tag;
        public String tagColor;

        // Extra fields cho detail page
        public String field1; // trieuChung / thucPhamNenAn / diaChi
        public String field2; // nguyenNhan / thucPhamKhongNenAn / soDienThoai
        public String field3; // huongChamSoc / luuYKhauPhan / gioMoCua
        public String field4; // mucDoNguyHiem / doTuoi / danhGia (String)

        public InfoItem(String name, String desc, String tag, String tagColor) {
            this.name     = name;
            this.desc     = desc;
            this.tag      = tag;
            this.tagColor = tagColor;
        }
    }

    public interface OnItemClickListener {
        void onClick(InfoItem item);
    }

    private final List<InfoItem>       allList     = new ArrayList<>();
    private final List<InfoItem>       displayList = new ArrayList<>();
    private final OnItemClickListener  listener;

    public InfoAdapter(List<InfoItem> list, OnItemClickListener listener) {
        this.allList.addAll(list);
        this.displayList.addAll(list);
        this.listener = listener;
    }

    /** Cập nhật toàn bộ data (gọi sau khi Firebase load xong) */
    public void setData(List<InfoItem> newList) {
        allList.clear();
        allList.addAll(newList);
        displayList.clear();
        displayList.addAll(newList);
        notifyDataSetChanged();
    }

    /** Lọc danh sách theo từ khoá */
    public void filter(String keyword) {
        displayList.clear();
        if (keyword == null || keyword.trim().isEmpty()) {
            displayList.addAll(allList);
        } else {
            String kw = keyword.toLowerCase().trim();
            for (InfoItem item : allList) {
                if (item.name.toLowerCase().contains(kw)
                        || item.desc.toLowerCase().contains(kw)
                        || item.tag.toLowerCase().contains(kw)) {
                    displayList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItem item = displayList.get(position);
        holder.tvName.setText(item.name);
        holder.tvDesc.setText(item.desc);
        holder.tvTag.setText(item.tag);

        try {
            holder.tvTag.getBackground().setTint(Color.parseColor(item.tagColor));
        } catch (Exception ignored) {}

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
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