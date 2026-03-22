package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityVaccineBinding;
import java.util.ArrayList;
import java.util.List;

public class VaccineActivity extends AppCompatActivity {

    private ActivityVaccineBinding binding;
    private List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddVaccineActivity.class)));

        // Data mẫu
        list.add(new HistoryAdapter.HistoryItem(
                "Loại vaccine: DHLPPI", "Ngày tiêm: 23/6/2025"));

        adapter = new HistoryAdapter(list,
                (position, item) -> {
                    // Nút Sửa → mở AddVaccineActivity
                    Intent intent = new Intent(this, AddVaccineActivity.class);
                    intent.putExtra("is_edit", true);
                    intent.putExtra("position", position);
                    intent.putExtra("vaccine_name", item.title);
                    intent.putExtra("vaccine_date", item.date);
                    startActivity(intent);
                },
                position -> {} // Xóa đã xử lý trong adapter
        );

        binding.rvVaccineList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVaccineList.setAdapter(adapter);
    }
}