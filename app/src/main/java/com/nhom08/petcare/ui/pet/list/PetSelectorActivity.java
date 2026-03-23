package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityPetSelectorBinding;
import com.nhom08.petcare.utils.PetManager;
import java.util.ArrayList;
import java.util.List;

public class PetSelectorActivity extends AppCompatActivity {

    private ActivityPetSelectorBinding binding;
    private PetRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetSelectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new PetRepository(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAddPet.setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.nhom08.petcare.ui.pet.profile.AddPetActivity.class)));

        loadPets();
    }

    private void loadPets() {
        String userId = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();
        String currentPetId = PetManager.getInstance(this)
                .getCurrentPetId();

        repository.getAllPets(userId, pets -> {
            runOnUiThread(() -> {
                List<PetSelectorAdapter.PetSelectorItem> list =
                        new ArrayList<>();

                for (com.nhom08.petcare.data.model.ThuCung pet : pets) {
                    String petType = pet.loai +
                            (pet.giong != null && !pet.giong.isEmpty()
                                    ? " • " + pet.giong : "");

                    list.add(new PetSelectorAdapter.PetSelectorItem(
                            pet.id,
                            pet.tenThuCung,
                            petType,
                            pet.id.equals(currentPetId)
                    ));
                }

                PetSelectorAdapter adapter = new PetSelectorAdapter(
                        list, selectedPet -> {
                    PetManager.getInstance(this).setCurrentPet(
                            selectedPet.petId,
                            selectedPet.petName,
                            selectedPet.petType);
                    setResult(RESULT_OK);
                    finish();
                });

                binding.rvPetSelector.setLayoutManager(
                        new LinearLayoutManager(this));
                binding.rvPetSelector.setAdapter(adapter);
            });
        });
    }
}