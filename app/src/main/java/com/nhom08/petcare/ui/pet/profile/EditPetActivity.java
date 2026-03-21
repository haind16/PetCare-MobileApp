package com.nhom08.petcare.ui.pet.profile;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityEditPetBinding;

public class EditPetActivity extends AppCompatActivity {

    private ActivityEditPetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Spinner giới tính
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Đực", "Cái"});
        genderAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(genderAdapter);

        // Spinner điều kiện sống
        ArrayAdapter<String> livingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Căn hộ vừa", "Nhà có sân", "Chung cư nhỏ"});
        livingAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        binding.spLiving.setAdapter(livingAdapter);

        binding.btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}