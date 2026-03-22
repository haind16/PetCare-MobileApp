package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddVaccineBinding;

public class AddVaccineActivity extends AppCompatActivity {

    private ActivityAddVaccineBinding binding;
    private boolean isEditMode = false;
    private int editPosition = -1;

    // Danh sách vaccine
    private String[] vaccineTypes = {
            "Phòng dại",
            "DHLPPI",
            "Bordetella",
            "Leptospirosis",
            "Canine Influenza"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, vaccineTypes);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        binding.spVaccineType.setAdapter(adapter);

        // Kiểm tra mode Sửa hay Thêm
        isEditMode = getIntent().getBooleanExtra("is_edit", false);
        editPosition = getIntent().getIntExtra("position", -1);

        if (isEditMode) {
            // Đổi tiêu đề thành "Sửa lịch tiêm"
            binding.tvTitle.setText("Sửa lịch tiêm");

            // Điền data cũ vào form
            String vaccineName = getIntent()
                    .getStringExtra("vaccine_name");
            if (vaccineName != null) {
                for (int i = 0; i < vaccineTypes.length; i++) {
                    if (vaccineTypes[i].equals(vaccineName)) {
                        binding.spVaccineType.setSelection(i);
                        break;
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener(v -> {
            String vaccine = binding.spVaccineType
                    .getSelectedItem().toString();
            int day = binding.datePicker.getDayOfMonth();
            int month = binding.datePicker.getMonth() + 1;
            int year = binding.datePicker.getYear();
            String date = day + "/" + month + "/" + year;

            if (isEditMode) {
                Toast.makeText(this, "Đã cập nhật lịch tiêm!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đã thêm: " + vaccine + " - " + date,
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }
}