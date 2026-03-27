package com.nhom08.petcare.ui.pet.profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityPetProfileBinding;
import com.nhom08.petcare.ui.pet.health.*;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class PetProfileActivity extends AppCompatActivity {

    private ActivityPetProfileBinding binding;
    private PetRepository repository;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new PetRepository(this);
        petId = getIntent().getStringExtra("pet_id");

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEdit.setOnClickListener(v -> {
            if (petId == null) return;
            Intent intent = new Intent(this, EditPetActivity.class);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        binding.btnWeight.setOnClickListener(v -> { Intent i = new Intent(this, WeightActivity.class); i.putExtra("pet_id", petId); startActivity(i); });
        binding.btnVaccine.setOnClickListener(v -> { Intent i = new Intent(this, VaccineActivity.class); i.putExtra("pet_id", petId); startActivity(i); });
        binding.btnDeworming.setOnClickListener(v -> { Intent i = new Intent(this, DewormingActivity.class); i.putExtra("pet_id", petId); startActivity(i); });
        binding.btnAllergy.setOnClickListener(v -> { Intent i = new Intent(this, AllergyActivity.class); i.putExtra("pet_id", petId); startActivity(i); });
        binding.btnDisease.setOnClickListener(v -> { Intent i = new Intent(this, DiseaseActivity.class); i.putExtra("pet_id", petId); startActivity(i); });
        binding.btnPrescription.setOnClickListener(v -> { Intent i = new Intent(this, PrescriptionActivity.class); i.putExtra("pet_id", petId); startActivity(i); });

        loadPet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPet();
    }

    private void loadPet() {
        if (petId == null) return;
        repository.getPetById(petId, pet -> runOnUiThread(() -> {
            if (pet == null) { finish(); return; }
            bindPetData(pet);
        }));
    }

    private void bindPetData(ThuCung pet) {
        binding.tvPetName.setText(pet.tenThuCung);
        String loaiGiong = (pet.loai != null ? pet.loai : "") + (pet.giong != null && !pet.giong.isEmpty() ? " • " + pet.giong : "");
        binding.tvPetBreed.setText(loaiGiong.isEmpty() ? "Chưa rõ" : loaiGiong);
        binding.tvPetGender.setText(pet.gioiTinh != null ? pet.gioiTinh : "Chưa rõ");
        binding.tvPetAge.setText(tinhTuoi(pet.ngaySinh));

        // HIỂN THỊ CÂN NẶNG
        if (pet.canNang > 0) {
            binding.tvPetWeight.setText(pet.canNang + " kg");
        } else {
            binding.tvPetWeight.setText("Chưa có");
        }

        if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
            Glide.with(this).load(new File(pet.anhUrl)).centerCrop().placeholder(R.drawable.pet_welcome).into(binding.imgPet);
        } else {
            binding.imgPet.setImageResource(R.drawable.pet_welcome);
        }
    }

    private String tinhTuoi(String ngaySinh) {
        if (ngaySinh == null || ngaySinh.isEmpty()) return "Chưa rõ";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate born = LocalDate.parse(ngaySinh, fmt);
            LocalDate now = LocalDate.now();
            if (born.isAfter(now)) return "Chưa sinh";
            Period period = Period.between(born, now);
            if (period.getYears() > 0) return period.getYears() + " tuổi";
            if (period.getMonths() > 0) return period.getMonths() + " tháng";
            return period.getDays() + " ngày";
        } catch (Exception e) { return ngaySinh; }
    }
}