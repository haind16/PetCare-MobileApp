package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.DonThuocDao;
import com.nhom08.petcare.data.model.DonThuoc;
import com.nhom08.petcare.databinding.ActivityAddPrescriptionBinding;
import java.util.UUID;

public class AddPrescriptionActivity extends AppCompatActivity {

    private ActivityAddPrescriptionBinding binding;
    private DonThuocDao dao;
    private String petId, recordId;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao      = AppDatabase.getInstance(this).donThuocDao();
        petId    = getIntent().getStringExtra("pet_id");
        isEdit   = getIntent().getBooleanExtra("is_edit", false);
        recordId = getIntent().getStringExtra("record_id");

        if (isEdit) {
            binding.tvTitle.setText("Sửa đơn thuốc");
            String name   = getIntent().getStringExtra("title");
            String dosage = getIntent().getStringExtra("dosage");
            String usage  = getIntent().getStringExtra("usage");
            if (name   != null) binding.etMedicineName.setText(name);
            if (dosage != null) binding.etDosage.setText(dosage);
            if (usage  != null) binding.etUsage.setText(usage);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String name   = binding.etMedicineName.getText().toString().trim();
        String dosage = binding.etDosage.getText().toString().trim();
        String usage  = binding.etUsage.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etMedicineName.setError("Vui lòng nhập tên thuốc");
            return;
        }

        new Thread(() -> {
            DonThuoc record = new DonThuoc();
            record.id        = (isEdit && recordId != null) ? recordId : UUID.randomUUID().toString();
            record.petId     = petId;
            record.tenThuoc  = name;
            record.lieuLuong = dosage.isEmpty() ? null : dosage;
            record.cachDung  = usage.isEmpty() ? null : usage;
            dao.insert(record);
            runOnUiThread(() -> {
                Toast.makeText(this, isEdit ? "Đã cập nhật đơn thuốc!" : "Đã lưu đơn thuốc!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}