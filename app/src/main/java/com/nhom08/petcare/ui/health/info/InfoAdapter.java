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
import java.util.List;

public class InfoAdapter extends
        RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    public static class InfoItem {
        public String name, desc, tag, tagColor;
        public InfoItem(String name, String desc,
                        String tag, String tagColor) {
            this.name = name;
            this.desc = desc;
            this.tag = tag;
            this.tagColor = tagColor;
        }
    }

    public interface OnItemClickListener {
        void onClick(InfoItem item);
    }

    private List<InfoItem> list;
    private OnItemClickListener listener;

    public InfoAdapter(List<InfoItem> list,
                       OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);
        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder,
                                 int position) {
        InfoItem item = list.get(position);
        holder.tvName.setText(item.name);
        holder.tvDesc.setText(item.desc);
        holder.tvTag.setText(item.tag);
        holder.tvTag.getBackground().setTint(Color.parseColor(item.tagColor));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class InfoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvName, tvDesc, tvTag;

        InfoViewHolder(View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.imgItem);
            tvName = itemView.findViewById(R.id.tvName);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvTag = itemView.findViewById(R.id.tvTag);
        }
    }
}