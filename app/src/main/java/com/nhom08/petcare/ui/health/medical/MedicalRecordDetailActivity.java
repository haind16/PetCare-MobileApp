package com.nhom08.petcare.ui.health.medical;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityMedicalRecordDetailBinding;

public class MedicalRecordDetailActivity extends AppCompatActivity {

    private ActivityMedicalRecordDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordDetailBinding.inflate(
                getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Data mẫu — sau lấy từ Intent/Firestore
        binding.tvPetName.setText("Milo");
        binding.tvPetAge.setText("Tuổi: 3");
        binding.tvPetWeight.setText("Cân nặng: 4.5 kg");
        binding.tvDate.setText("Ngày khám: 25/12/2025");
        binding.tvType.setText("Loại khám: Khám sức khỏe định kì");
        binding.tvClinic.setText("Phòng khám: Happy Pet Clinic");
        binding.tvDoctor.setText("Bác sĩ: Trần Văn B");
        binding.tvDiagnosis.setText("Viêm tai nhẹ do vi khuẩn");
        binding.tvMedicine1Name.setText("Amoxicillin 250mg");
        binding.tvMedicine1Dosage.setText("Ngày 2 lần - 7 ngày");
        binding.tvMedicine2Name.setText("Thuốc nhỏ tai Oticin-10");
        binding.tvMedicine2Dosage.setText("Mỗi bên tai 1 giọt - 7 ngày");
    }
}