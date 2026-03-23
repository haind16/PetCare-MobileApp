package com.nhom08.petcare.ui.health.medical;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddMedicalRecordBinding;
import java.util.Calendar;

public class AddMedicalRecordActivity extends AppCompatActivity {

    private ActivityAddMedicalRecordBinding binding;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMedicalRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Chọn ngày khám
        binding.btnSelectDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate.set(year, month, day);
                binding.tvSelectedDate.setText(
                        day + "/" + (month + 1) + "/" + year);
            },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Lưu
        binding.btnSave.setOnClickListener(v -> {
            String loaiKham = binding.etType.getText().toString().trim();
            String phongKham = binding.etClinic.getText().toString().trim();
            String bacSi = binding.etDoctor.getText().toString().trim();
            String chuanDoan = binding.etDiagnosis.getText().toString().trim();

            if (loaiKham.isEmpty()) {
                binding.etType.setError("Vui lòng nhập loại khám");
                return;
            }
            // TODO: lưu vào Firestore
            Toast.makeText(this, "Đã lưu hồ sơ!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}