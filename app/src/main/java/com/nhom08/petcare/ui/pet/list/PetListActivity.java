package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityPetListBinding;
import com.nhom08.petcare.ui.pet.profile.AddPetActivity;

import java.util.ArrayList;
import java.util.List;

public class PetListActivity extends AppCompatActivity {

    private ActivityPetListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Data mẫu tạm thời
        List<PetAdapter.PetItem> petList = new ArrayList<>();
        petList.add(new PetAdapter.PetItem("Nilo", "Đực", "5 tuổi", "Hoody"));
        petList.add(new PetAdapter.PetItem("Bông", "Đực", "3 tuổi", "Corgi"));
        petList.add(new PetAdapter.PetItem("Golden", "Cái", "7 tuổi", "Golden"));

        PetAdapter adapter = new PetAdapter(petList);
        binding.rvPetList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPetList.setAdapter(adapter);

        binding.btnAddPet.setOnClickListener(v ->
                startActivity(new Intent(this, AddPetActivity.class)));
    }
}