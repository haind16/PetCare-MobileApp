package com.nhom08.petcare.ui.pet.profile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityAddPetBinding;
import java.util.Calendar;

public class AddPetActivity extends AppCompatActivity {

    private ActivityAddPetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Chọn ngày sinh
        binding.etBirthDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String date = day + "/" + (month + 1) + "/" + year;
                binding.etBirthDate.setText(date);
            }, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Chọn ảnh
        binding.btnPickImage.setOnClickListener(v -> {
            // TODO: mở gallery chọn ảnh
            Toast.makeText(this, "Chọn ảnh", Toast.LENGTH_SHORT).show();
        });

        // Xác nhận thêm
        binding.btnConfirm.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String gender = binding.etGender.getText().toString().trim();
            String weight = binding.etWeight.getText().toString().trim();
            String birthDate = binding.etBirthDate.getText().toString().trim();
            String breed = binding.etBreed.getText().toString().trim();
            String living = binding.etLivingCondition.getText().toString().trim();

            if (name.isEmpty()) {
                binding.etName.setError("Vui lòng nhập tên");
                return;
            }

            // TODO: lưu vào database
            Toast.makeText(this, "Đã thêm thú cưng " + name + "!",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}