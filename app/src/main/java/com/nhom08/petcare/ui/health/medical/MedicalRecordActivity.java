package com.nhom08.petcare.ui.health.medical;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityMedicalRecordBinding;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordActivity extends AppCompatActivity {

    private ActivityMedicalRecordBinding binding;
    private List<MedicalRecordAdapter.RecordItem> list = new ArrayList<>();
    private MedicalRecordAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAddRecord.setOnClickListener(v ->
                startActivity(new Intent(this, AddMedicalRecordActivity.class)));

        // Data mẫu
        list.add(new MedicalRecordAdapter.RecordItem(
                "25/12/2025", "Khám sức khỏe định kì",
                true, true));
        list.add(new MedicalRecordAdapter.RecordItem(
                "10/9/2025", "Vấn đề tiêu hóa",
                true, false));
        list.add(new MedicalRecordAdapter.RecordItem(
                "20/7/2025", "Viêm tai cấp",
                false, true));

        adapter = new MedicalRecordAdapter(list, item ->
                startActivity(new Intent(this,
                        MedicalRecordDetailActivity.class)));

        binding.rvMedicalRecords.setLayoutManager(
                new LinearLayoutManager(this));
        binding.rvMedicalRecords.setAdapter(adapter);
    }
}