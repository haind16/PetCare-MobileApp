package com.nhom08.petcare.ui.health.medical;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.repository.HoSoYTeRepository;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityMedicalRecordDetailBinding;

import java.io.File;

public class MedicalRecordDetailActivity extends AppCompatActivity {

    private ActivityMedicalRecordDetailBinding binding;
    private HoSoYTeRepository hoSoRepo;
    private PetRepository petRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hoSoRepo = new HoSoYTeRepository(this);
        petRepo  = new PetRepository(this);

        binding.btnBack.setOnClickListener(v -> finish());

        String recordId = getIntent().getStringExtra("record_id");
        if (recordId == null) {
            Toast.makeText(this, "Không tìm thấy hồ sơ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRecord(recordId);
    }

    private void loadRecord(String recordId) {
        hoSoRepo.getById(recordId, record -> runOnUiThread(() -> {
            if (record == null) {
                Toast.makeText(this, "Hồ sơ không tồn tại", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Thông tin khám
            binding.tvDate.setText("Ngày khám: "   + nvl(record.ngayKham));
            binding.tvType.setText("Loại khám: "   + nvl(record.loaiKham));
            binding.tvClinic.setText("Phòng khám: " + nvl(record.phongKham));
            binding.tvDoctor.setText("Bác sĩ: "     + nvl(record.bacSi));
            binding.tvDiagnosis.setText(nvl(record.chuanDoan));

            boolean hasDonThuoc  = record.donThuoc  != null && !record.donThuoc.isEmpty();
            boolean hasTiemPhong = record.tiemPhong != null && !record.tiemPhong.isEmpty();

            // Item 1 — Đơn thuốc
            if (hasDonThuoc) {
                binding.tvMedicine1Name.setText(record.donThuoc);
                binding.tvMedicine1Dosage.setVisibility(View.GONE);
            } else {
                binding.tvMedicine1Name.setText("Không có đơn thuốc");
                binding.tvMedicine1Dosage.setVisibility(View.GONE);
            }

            // Item 2 — Tiêm phòng
            if (hasTiemPhong) {
                binding.tvMedicine2Name.setText(record.tiemPhong);
                binding.tvMedicine2Name.setVisibility(View.VISIBLE);
                binding.tvMedicine2Dosage.setVisibility(View.GONE);
            } else {
                binding.tvMedicine2Name.setText("Không có tiêm phòng");
                binding.tvMedicine2Name.setVisibility(View.VISIBLE);
                binding.tvMedicine2Dosage.setVisibility(View.GONE);
            }

            loadPetInfo(record.petId);
        }));
    }

    private void loadPetInfo(String petId) {
        if (petId == null) return;
        petRepo.getPetById(petId, pet -> runOnUiThread(() -> {
            if (pet == null) return;
            binding.tvPetName.setText(nvl(pet.tenThuCung));
            binding.tvPetAge.setText(
                    pet.ngaySinh != null && !pet.ngaySinh.isEmpty()
                            ? "Ngày sinh: " + pet.ngaySinh
                            : "Ngày sinh: Chưa có");
            binding.tvPetWeight.setText(
                    pet.canNang > 0
                            ? "Cân nặng: " + pet.canNang + " kg"
                            : "Cân nặng: Chưa có");

            // Load ảnh thú cưng
            if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
                Glide.with(this)
                        .load(new File(pet.anhUrl))
                        .placeholder(R.drawable.pet_welcome)
                        .circleCrop()
                        .into(binding.imgPet);
            } else {
                binding.imgPet.setImageResource(R.drawable.pet_welcome);
            }
        }));
    }

    private String nvl(String s) { return s != null ? s : ""; }
}