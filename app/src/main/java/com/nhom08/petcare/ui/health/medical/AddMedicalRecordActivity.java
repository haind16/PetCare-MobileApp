package com.nhom08.petcare.ui.health.medical;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.model.HoSoYTe;
import com.nhom08.petcare.data.repository.HoSoYTeRepository;
import com.nhom08.petcare.databinding.ActivityAddMedicalRecordBinding;
import com.nhom08.petcare.utils.PetManager;
import java.util.Calendar;

public class AddMedicalRecordActivity extends AppCompatActivity {

    private ActivityAddMedicalRecordBinding binding;
    private Calendar selectedDate = Calendar.getInstance();
    private HoSoYTeRepository repo;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedicalRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo = new HoSoYTeRepository(this);

        // Ưu tiên pet_id từ Intent, fallback về PetManager
        petId = getIntent().getStringExtra("pet_id");
        if (petId == null || petId.isEmpty()) {
            petId = PetManager.getInstance(this).getCurrentPetId();
        }

        binding.btnBack.setOnClickListener(v -> finish());

        // Hiện ngày mặc định
        updateDateText();

        // Chọn ngày khám
        binding.btnSelectDate.setOnClickListener(v ->
                new DatePickerDialog(this, (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    updateDateText();
                },
                        selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        );

        binding.btnSave.setOnClickListener(v -> saveRecord());
    }

    private void updateDateText() {
        int d = selectedDate.get(Calendar.DAY_OF_MONTH);
        int m = selectedDate.get(Calendar.MONTH) + 1;
        int y = selectedDate.get(Calendar.YEAR);
        binding.tvSelectedDate.setText(
                String.format("%d/%d/%d", d, m, y));
    }

    private void saveRecord() {
        String loaiKham  = binding.etType.getText().toString().trim();
        String phongKham = binding.etClinic.getText().toString().trim();
        String bacSi     = binding.etDoctor.getText().toString().trim();
        String chuanDoan = binding.etDiagnosis.getText().toString().trim();
        String donThuoc  = binding.etPrescription.getText().toString().trim();
        String tiemPhong = binding.etVaccine.getText().toString().trim();
        String ngayKham  = binding.tvSelectedDate.getText().toString();

        if (loaiKham.isEmpty()) {
            binding.etType.setError("Vui lòng nhập loại khám");
            return;
        }

        if (petId == null || petId.isEmpty()) {
            Toast.makeText(this, "Chưa chọn thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }

        HoSoYTe record = new HoSoYTe();
        record.petId     = petId;
        record.ngayKham  = ngayKham;
        record.loaiKham  = loaiKham;
        record.phongKham = phongKham.isEmpty() ? null : phongKham;
        record.bacSi     = bacSi.isEmpty() ? null : bacSi;
        record.chuanDoan = chuanDoan.isEmpty() ? null : chuanDoan;
        record.donThuoc  = donThuoc.isEmpty() ? null : donThuoc;
        record.tiemPhong = tiemPhong.isEmpty() ? null : tiemPhong;

        binding.btnSave.setEnabled(false);
        repo.add(record, r -> runOnUiThread(() -> {
            Toast.makeText(this, "Đã lưu hồ sơ!", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}