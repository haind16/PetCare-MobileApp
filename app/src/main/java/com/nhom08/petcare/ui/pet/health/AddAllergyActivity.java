package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.DiUngDao;
import com.nhom08.petcare.data.model.DiUng;
import com.nhom08.petcare.databinding.ActivityAddAllergyBinding;
import java.util.UUID;

/**
 * Activity để thêm hoặc sửa thông tin dị ứng của thú cưng.
 * Dữ liệu được lưu trực tiếp vào bảng DiUng (SQLite) thông qua Thread phụ.
 */
public class AddAllergyActivity extends AppCompatActivity {

    private ActivityAddAllergyBinding binding;
    private DiUngDao dao;
    private String petId, recordId;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAllergyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dao      = AppDatabase.getInstance(this).diUngDao();
        petId    = getIntent().getStringExtra("pet_id");
        isEdit   = getIntent().getBooleanExtra("is_edit", false);
        recordId = getIntent().getStringExtra("record_id");

        if (isEdit) {
            binding.tvTitle.setText("Sửa thông tin dị ứng");
            String existing = getIntent().getStringExtra("title");
            if (existing != null) binding.etContent.setText(existing);
        }

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveData());
    }

    /**
     * Khởi chạy Worker Thread để lưu thông tin dị ứng, tránh block Main Thread.
     */
    private void saveData() {
        String content = binding.etContent.getText().toString().trim();
        if (content.isEmpty()) {
            binding.etContent.setError("Vui lòng nhập chất gây dị ứng");
            return;
        }

        new Thread(() -> {
            DiUng record = new DiUng();
            record.id            = (isEdit && recordId != null) ? recordId : UUID.randomUUID().toString();
            record.petId         = petId;
            record.chatGayDiUng  = content;
            dao.insert(record);
            runOnUiThread(() -> {
                Toast.makeText(this, isEdit ? "Đã cập nhật dị ứng!" : "Đã lưu dị ứng!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}