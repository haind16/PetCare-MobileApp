package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityVaccineBinding;
import java.util.Calendar;

public class VaccineActivity extends AppCompatActivity {
    private ActivityVaccineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSave.setOnClickListener(v -> {
            int day = binding.datePicker.getDayOfMonth();
            int month = binding.datePicker.getMonth() + 1;
            int year = binding.datePicker.getYear();
            String date = day + "/" + month + "/" + year;
            Toast.makeText(this, "Đã lưu ngày tiêm: " + date,
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}