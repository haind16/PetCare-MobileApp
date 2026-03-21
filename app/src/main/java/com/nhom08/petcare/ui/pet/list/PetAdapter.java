package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import com.nhom08.petcare.ui.pet.profile.PetProfileActivity;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    public static class PetItem {
        public String name, gender, age, breed;
        public PetItem(String name, String gender, String age, String breed) {
            this.name = name;
            this.gender = gender;
            this.age = age;
            this.breed = breed;
        }
    }

    private List<PetItem> petList;

    public PetAdapter(List<PetItem> petList) {
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        PetItem pet = petList.get(position);
        holder.tvPetName.setText(pet.name);
        holder.tvGender.setText(pet.gender);
        holder.tvAge.setText(pet.age);
        holder.tvBreed.setText(pet.breed);

        // Nút Sửa thông tin → sang PetProfileActivity
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PetProfileActivity.class);
            intent.putExtra("pet_name", pet.name);
            v.getContext().startActivity(intent);
        });

        // Nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            petList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, petList.size());
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView tvPetName, tvGender, tvAge, tvBreed;
        Button btnEdit, btnDelete;
        ImageView imgPet;

        PetViewHolder(View itemView) {
            super(itemView);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvBreed = itemView.findViewById(R.id.tvBreed);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            imgPet = itemView.findViewById(R.id.imgPet);
        }
    }
}