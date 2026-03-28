package com.nhom08.petcare.ui.health.medical;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom08.petcare.R;

import java.util.List;

public class MedicalRecordAdapter extends
        RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {

    public static class RecordItem {
        public String id, date, type;
        public boolean hasDonThuoc, hasTiemPhong;

        public RecordItem(String id, String date, String type,
                          boolean hasDonThuoc, boolean hasTiemPhong) {
            this.id           = id;
            this.date         = date;
            this.type         = type;
            this.hasDonThuoc  = hasDonThuoc;
            this.hasTiemPhong = hasTiemPhong;
        }
    }

    public interface OnItemClickListener { void onClick(RecordItem item); }

    private final List<RecordItem> list;
    private final OnItemClickListener listener;

    public MedicalRecordAdapter(List<RecordItem> list, OnItemClickListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medical_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordItem item = list.get(position);
        holder.tvDate.setText(item.date != null ? item.date : "");
        holder.tvType.setText(item.type != null ? item.type : "");
        holder.tagDonThuoc.setVisibility(item.hasDonThuoc  ? View.VISIBLE : View.GONE);
        holder.tagTiemPhong.setVisibility(item.hasTiemPhong ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(item); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvType;
        LinearLayout tagDonThuoc, tagTiemPhong;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate      = itemView.findViewById(R.id.tvDate);
            tvType      = itemView.findViewById(R.id.tvType);
            tagDonThuoc  = itemView.findViewById(R.id.tvTagDonThuoc);
            tagTiemPhong = itemView.findViewById(R.id.tvTagTiemPhong);
        }
    }
}