package com.nhom08.petcare.ui.pet.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class PetSelectorAdapter extends
        RecyclerView.Adapter<PetSelectorAdapter.ViewHolder> {

    public static class PetSelectorItem {
        public String petId, petName, petType;
        public boolean isSelected;

        public PetSelectorItem(String petId, String petName,
                               String petType, boolean isSelected) {
            this.petId = petId;
            this.petName = petName;
            this.petType = petType;
            this.isSelected = isSelected;
        }
    }

    public interface OnPetSelectedListener {
        void onSelected(PetSelectorItem pet);
    }

    private List<PetSelectorItem> list;
    private OnPetSelectedListener listener;
    private int selectedPosition = -1;

    public PetSelectorAdapter(List<PetSelectorItem> list,
                              OnPetSelectedListener listener) {
        this.list = list;
        this.listener = listener;

        // Tìm pet đang được chọn
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet_selector, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PetSelectorItem item = list.get(position);
        holder.tvPetName.setText(item.petName);
        holder.tvPetType.setText(item.petType);

        // Hiện tick nếu đang được chọn
        holder.imgSelected.setVisibility(
                position == selectedPosition ? View.VISIBLE : View.GONE);

        // Highlight card nếu được chọn
        holder.itemView.setAlpha(position == selectedPosition ? 1f : 0.7f);

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null) listener.onSelected(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgPet;
        TextView tvPetName, tvPetType;
        ImageView imgSelected;

        ViewHolder(View itemView) {
            super(itemView);
            imgPet = itemView.findViewById(R.id.imgPet);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvPetType = itemView.findViewById(R.id.tvPetType);
            imgSelected = itemView.findViewById(R.id.imgSelected);
        }
    }
}