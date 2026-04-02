package com.nhom08.petcare.ui.health.medical;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.model.HoSoYTe;
import com.nhom08.petcare.data.repository.HoSoYTeRepository;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityMedicalRecordBinding;
import com.nhom08.petcare.utils.PetManager;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordActivity extends AppCompatActivity {

    private ActivityMedicalRecordBinding binding;
    private List<MedicalRecordAdapter.RecordItem> list = new ArrayList<>();
    private MedicalRecordAdapter adapter;
    private HoSoYTeRepository repo;
    private PetRepository petRepo;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo    = new HoSoYTeRepository(this);
        petRepo = new PetRepository(this);

        // Ưu tiên pet_id từ Intent (khi mở từ PetProfileActivity)
        // Fallback về PetManager (khi mở từ HealthFragment)
        petId = getIntent().getStringExtra("pet_id");
        if (petId == null || petId.isEmpty()) {
            petId = PetManager.getInstance(this).getCurrentPetId();
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddRecord.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMedicalRecordActivity.class);
            intent.putExtra("pet_id", petId); // truyền pet_id sang màn thêm
            startActivity(intent);
        });

        adapter = new MedicalRecordAdapter(list, item -> {
            Intent intent = new Intent(this, MedicalRecordDetailActivity.class);
            intent.putExtra("record_id", item.id);
            startActivity(intent);
        });

        binding.rvMedicalRecords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMedicalRecords.setAdapter(adapter);

        loadPetInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadPetInfo() {
        if (petId == null || petId.isEmpty()) return;
        petRepo.getPetById(petId, pet -> runOnUiThread(() -> {
            if (pet == null) return;
            binding.tvPetName.setText(pet.tenThuCung != null ? pet.tenThuCung : "");
            binding.tvPetAge.setText(pet.ngaySinh != null && !pet.ngaySinh.isEmpty()
                    ? "Ngày sinh: " + pet.ngaySinh : "Ngày sinh: Chưa có");
            binding.tvPetWeight.setText(pet.canNang > 0
                    ? "Cân nặng: " + pet.canNang + " kg" : "Cân nặng: Chưa có");
            if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
                Glide.with(this).load(new java.io.File(pet.anhUrl))
                        .placeholder(R.drawable.pet_welcome).circleCrop()
                        .into(binding.imgPet);
            } else {
                binding.imgPet.setImageResource(R.drawable.pet_welcome);
            }
        }));
    }

    private void loadData() {
        if (petId == null || petId.isEmpty()) {
            Toast.makeText(this, "Chưa chọn thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }

        repo.getAll(petId, records -> runOnUiThread(() -> {
            list.clear();
            for (HoSoYTe r : records) {
                list.add(new MedicalRecordAdapter.RecordItem(
                        r.id, r.ngayKham, r.loaiKham,
                        r.donThuoc  != null && !r.donThuoc.isEmpty(),
                        r.tiemPhong != null && !r.tiemPhong.isEmpty()
                ));
            }
            binding.rvMedicalRecords.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
            adapter.notifyDataSetChanged();
        }));
    }
}