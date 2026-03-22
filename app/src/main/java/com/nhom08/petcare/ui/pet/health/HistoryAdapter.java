package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import java.util.List;

public class HistoryAdapter extends
        RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public static class HistoryItem {
        public String title, date;
        public HistoryItem(String title, String date) {
            this.title = title;
            this.date = date;
        }
    }

    public interface OnEditClickListener {
        void onEdit(int position, HistoryItem item);
    }

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    private List<HistoryItem> list;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public HistoryAdapter(List<HistoryItem> list,
                          OnEditClickListener editListener,
                          OnDeleteClickListener deleteListener) {
        this.list = list;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder,
                                 int position) {
        HistoryItem item = list.get(position);
        holder.tvTitle.setText(item.title);
        holder.tvDate.setText(item.date);

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add("Sửa");
            popup.getMenu().add("Xóa");
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getTitle().equals("Sửa")) {
                    if (editListener != null)
                        editListener.onEdit(position, item);
                } else {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    if (deleteListener != null)
                        deleteListener.onDelete(position);
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, btnMore;

        HistoryViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}