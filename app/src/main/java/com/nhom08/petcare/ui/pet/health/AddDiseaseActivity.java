package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.BenhNenDao;
import com.nhom08.petcare.data.model.BenhNen;
import com.nhom08.petcare.databinding.ActivityAddDiseaseBinding;
import java.util.UUID;

public class AddDiseaseActivity extends AppCompatActivity {

    private ActivityAddDiseaseBinding binding;
    private BenhNenDao dao;
    private String petId, recordId;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddDiseaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao      = AppDatabase.getInstance(this).benhNenDao();
        petId    = getIntent().getStringExtra("pet_id");
        isEdit   = getIntent().getBooleanExtra("is_edit", false);
        recordId = getIntent().getStringExtra("record_id");

        if (isEdit) {
            binding.tvTitle.setText("Sửa bệnh nền");
            String existing = getIntent().getStringExtra("title");
            if (existing != null) binding.etContent.setText(existing);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String content = binding.etContent.getText().toString().trim();
        if (content.isEmpty()) {
            binding.etContent.setError("Vui lòng nhập tên bệnh");
            return;
        }

        new Thread(() -> {
            BenhNen record = new BenhNen();
            record.id      = (isEdit && recordId != null) ? recordId : UUID.randomUUID().toString();
            record.petId   = petId;
            record.tenBenh = content;
            dao.insert(record);
            runOnUiThread(() -> {
                Toast.makeText(this, isEdit ? "Đã cập nhật bệnh nền!" : "Đã lưu bệnh nền!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}