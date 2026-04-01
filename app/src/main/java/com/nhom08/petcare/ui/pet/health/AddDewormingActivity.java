package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.TayGiunDao;
import com.nhom08.petcare.data.model.TayGiun;
import com.nhom08.petcare.databinding.ActivityAddDewormingBinding;
import java.util.Calendar;
import java.util.UUID;

public class AddDewormingActivity extends AppCompatActivity {

    private ActivityAddDewormingBinding binding;
    private TayGiunDao dao;
    private String petId, recordId;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDewormingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao    = AppDatabase.getInstance(this).tayGiunDao();
        petId  = getIntent().getStringExtra("pet_id");
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        recordId = getIntent().getStringExtra("record_id");

        if (isEdit) {
            binding.tvTitle.setText("Sửa lịch tẩy giun");
            String date = getIntent().getStringExtra("date");
            String note = getIntent().getStringExtra("note");
            if (date != null) {
                try {
                    String[] parts = date.split("/");
                    binding.datePicker.updateDate(
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[1]) - 1,
                            Integer.parseInt(parts[0]));
                } catch (Exception ignored) {}
            }
            if (note != null) binding.etNote.setText(note);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        int day = binding.datePicker.getDayOfMonth();
        int month = binding.datePicker.getMonth() + 1;
        int year = binding.datePicker.getYear();

        // Không cho chọn ngày tương lai
        Calendar selected = Calendar.getInstance();
        selected.set(year, binding.datePicker.getMonth(), day);
        if (selected.after(Calendar.getInstance())) {
            Toast.makeText(this, "Không thể chọn ngày trong tương lai", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = String.format("%02d/%02d/%04d", day, month, year);
        String note = binding.etNote.getText().toString().trim();

        new Thread(() -> {
            TayGiun record = new TayGiun();
            record.id     = (isEdit && recordId != null) ? recordId : UUID.randomUUID().toString();
            record.petId  = petId;
            record.ngay   = date;
            record.ghiChu = note.isEmpty() ? null : note;
            dao.insert(record);
            runOnUiThread(() -> {
                Toast.makeText(this, isEdit ? "Đã cập nhật!" : "Đã lưu lịch tẩy giun!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}