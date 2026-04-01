package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.LichTiemPhongDao;
import com.nhom08.petcare.data.model.LichTiemPhong;
import com.nhom08.petcare.databinding.ActivityAddVaccineBinding;
import java.util.Calendar;
import java.util.UUID;

public class AddVaccineActivity extends AppCompatActivity {

    private ActivityAddVaccineBinding binding;
    private LichTiemPhongDao dao;
    private String petId, recordId;
    private boolean isEdit;

    private final String[] vaccineTypes = {
            "Phòng dại", "DHLPPI", "Bordetella", "Leptospirosis", "Canine Influenza"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao    = AppDatabase.getInstance(this).lichTiemPhongDao();
        petId  = getIntent().getStringExtra("pet_id");
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        recordId = getIntent().getStringExtra("record_id");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, vaccineTypes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spVaccineType.setAdapter(spinnerAdapter);

        if (isEdit) {
            binding.tvTitle.setText("Sửa lịch tiêm");
            String vaccineName = getIntent().getStringExtra("vaccine_name");
            String vaccineDate = getIntent().getStringExtra("vaccine_date");
            String vaccineReminder = getIntent().getStringExtra("vaccine_reminder");

            if (vaccineName != null) {
                for (int i = 0; i < vaccineTypes.length; i++) {
                    if (vaccineTypes[i].equals(vaccineName)) {
                        binding.spVaccineType.setSelection(i);
                        break;
                    }
                }
            }
            if (vaccineDate != null) setDatePicker(binding.datePicker, vaccineDate);
            if (vaccineReminder != null && !vaccineReminder.isEmpty())
                setDatePicker(binding.datePickerReminder, vaccineReminder);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveData());
    }

    private void setDatePicker(android.widget.DatePicker dp, String date) {
        try {
            String[] parts = date.split("/");
            dp.updateDate(Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[1]) - 1,
                    Integer.parseInt(parts[0]));
        } catch (Exception ignored) {}
    }

    private String getDateFromPicker(android.widget.DatePicker dp) {
        return String.format("%02d/%02d/%04d",
                dp.getDayOfMonth(), dp.getMonth() + 1, dp.getYear());
    }

    private void saveData() {
        String vaccine = binding.spVaccineType.getSelectedItem().toString();
        String ngayTiem = getDateFromPicker(binding.datePicker);
        String ngayNhac = getDateFromPicker(binding.datePickerReminder);

        // Validate: ngày nhắc không được trước ngày tiêm
        Calendar tiem = Calendar.getInstance();
        tiem.set(binding.datePicker.getYear(), binding.datePicker.getMonth(), binding.datePicker.getDayOfMonth());
        Calendar nhac = Calendar.getInstance();
        nhac.set(binding.datePickerReminder.getYear(), binding.datePickerReminder.getMonth(), binding.datePickerReminder.getDayOfMonth());
        if (nhac.before(tiem)) {
            Toast.makeText(this, "Ngày nhắc không được trước ngày tiêm", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            LichTiemPhong record = new LichTiemPhong();
            record.id          = (isEdit && recordId != null) ? recordId : UUID.randomUUID().toString();
            record.petId       = petId;
            record.tenVacxin   = vaccine;
            record.ngayTiem    = ngayTiem;
            record.ngayNhacNho = ngayNhac;
            dao.insert(record);
            runOnUiThread(() -> {
                Toast.makeText(this, isEdit ? "Đã cập nhật lịch tiêm!" : "Đã thêm lịch tiêm!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}