package com.nhom08.petcare.ui.pet.profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityPetProfileBinding;
import com.nhom08.petcare.ui.pet.health.AllergyActivity;
import com.nhom08.petcare.ui.pet.health.DewormingActivity;
import com.nhom08.petcare.ui.pet.health.DiseaseActivity;
import com.nhom08.petcare.ui.pet.health.PrescriptionActivity;
import com.nhom08.petcare.ui.pet.health.VaccineActivity;
import com.nhom08.petcare.ui.pet.health.WeightActivity;

public class PetProfileActivity extends AppCompatActivity {

    private ActivityPetProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Nút Edit hồ sơ
        binding.btnEdit.setOnClickListener(v ->
                startActivity(new Intent(this, EditPetActivity.class)));

        // Cân nặng
        binding.btnWeight.setOnClickListener(v ->
                startActivity(new Intent(this, WeightActivity.class)));

        // Lịch tiêm phòng
        binding.btnVaccine.setOnClickListener(v ->
                startActivity(new Intent(this, VaccineActivity.class)));

        // Ngày tẩy giun
        binding.btnDeworming.setOnClickListener(v ->
                startActivity(new Intent(this, DewormingActivity.class)));

        // Dị ứng
        binding.btnAllergy.setOnClickListener(v ->
                startActivity(new Intent(this, AllergyActivity.class)));

        // Bệnh nền
        binding.btnDisease.setOnClickListener(v ->
                startActivity(new Intent(this, DiseaseActivity.class)));

        // Đơn thuốc
        binding.btnPrescription.setOnClickListener(v ->
                startActivity(new Intent(this, PrescriptionActivity.class)));

    }
}