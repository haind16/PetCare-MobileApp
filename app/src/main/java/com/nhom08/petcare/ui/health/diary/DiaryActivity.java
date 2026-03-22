package com.nhom08.petcare.ui.health.diary;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityDiaryBinding;
import com.nhom08.petcare.ui.pet.health.HistoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    private ActivityDiaryBinding binding;
    private List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Nút Thêm hoạt động
        binding.btnAddActivity.setOnClickListener(v ->
                startActivity(new Intent(this, AddDiaryActivity.class)));

        // Data mẫu
        list.add(new HistoryAdapter.HistoryItem(
                "Cho Cz ăn", "Thứ hai, 16 tháng 2 năm 2026"));
        list.add(new HistoryAdapter.HistoryItem(
                "Cho Cz đi bộ", "Thứ ba, 17 tháng 2 năm 2026"));

        adapter = new HistoryAdapter(list,
                (position, item) -> {
                    Intent intent = new Intent(this, DiaryDetailActivity.class);
                    intent.putExtra("name", item.title);
                    intent.putExtra("date", item.date);
                    startActivity(intent);
                },
                position -> {}
        );

        binding.rvDiary.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDiary.setAdapter(adapter);
    }
}