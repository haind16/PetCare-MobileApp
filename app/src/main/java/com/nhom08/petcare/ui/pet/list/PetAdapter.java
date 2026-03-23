package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.ui.pet.profile.PetProfileActivity;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<ThuCung> petList;
    private OnDeleteListener deleteListener;

    public interface OnDeleteListener {
        void onDelete(ThuCung pet);
    }

    public PetAdapter(List<ThuCung> petList) {
        this.petList = petList;
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder,
                                 int position) {
        ThuCung pet = petList.get(position);
        holder.tvPetName.setText(pet.tenThuCung);
        holder.tvGender.setText(pet.gioiTinh);
        holder.tvAge.setText(pet.ngaySinh);
        holder.tvBreed.setText(pet.giong);

        // Sửa → sang PetProfileActivity
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),
                    PetProfileActivity.class);
            intent.putExtra("pet_id", pet.id);
            intent.putExtra("pet_name", pet.tenThuCung);
            v.getContext().startActivity(intent);
        });

        // Xóa
        holder.btnDelete.setOnClickListener(v -> {
            petList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, petList.size());
            if (deleteListener != null) deleteListener.onDelete(pet);
        });
    }

    @Override
    public int getItemCount() { return petList.size(); }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetName, tvGender, tvAge, tvBreed;
        Button btnEdit, btnDelete;

        PetViewHolder(View itemView) {
            super(itemView);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvGender  = itemView.findViewById(R.id.tvGender);
            tvAge     = itemView.findViewById(R.id.tvAge);
            tvBreed   = itemView.findViewById(R.id.tvBreed);
            btnEdit   = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}