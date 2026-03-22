package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddPrescriptionBinding;

public class AddPrescriptionActivity extends AppCompatActivity {

    private ActivityAddPrescriptionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        boolean isEdit = getIntent().getBooleanExtra("is_edit", false);
        if (isEdit) binding.tvTitle.setText("Sửa đơn thuốc");

        String existingTitle = getIntent().getStringExtra("title");
        if (existingTitle != null) binding.etMedicineName.setText(existingTitle);

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etMedicineName.getText().toString().trim();
            String dosage = binding.etDosage.getText().toString().trim();
            String usage = binding.etUsage.getText().toString().trim();

            if (name.isEmpty()) {
                binding.etMedicineName.setError("Vui lòng nhập tên thuốc");
                return;
            }
            Toast.makeText(this, "Đã lưu đơn thuốc!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}