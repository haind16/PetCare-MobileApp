package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityPetSelectorBinding;
import com.nhom08.petcare.utils.PetManager;
import java.util.ArrayList;
import java.util.List;

public class PetSelectorActivity extends AppCompatActivity {

    private ActivityPetSelectorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetSelectorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAddPet.setOnClickListener(v ->
                startActivity(new Intent(this,
                        com.nhom08.petcare.ui.pet.profile.AddPetActivity.class)));

        String currentPetId = PetManager.getInstance(this).getCurrentPetId();

        // Data mẫu — sau này lấy từ Firestore
        List<PetSelectorAdapter.PetSelectorItem> list = new ArrayList<>();
        list.add(new PetSelectorAdapter.PetSelectorItem(
                "pet_1", "Cz", "Chó • Golden Retriever",
                "pet_1".equals(currentPetId)));
        list.add(new PetSelectorAdapter.PetSelectorItem(
                "pet_2", "Milu", "Chó • Corgi",
                "pet_2".equals(currentPetId)));
        list.add(new PetSelectorAdapter.PetSelectorItem(
                "pet_3", "Nhím", "Mèo • Anh lông ngắn",
                "pet_3".equals(currentPetId)));

        PetSelectorAdapter adapter = new PetSelectorAdapter(list, pet -> {
            // Lưu pet được chọn vào SharedPreferences
            PetManager.getInstance(this)
                    .setCurrentPet(pet.petId, pet.petName, pet.petType);

            // Quay về màn trước + reload
            setResult(RESULT_OK);
            finish();
        });

        binding.rvPetSelector.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPetSelector.setAdapter(adapter);
    }
}