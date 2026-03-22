package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityDewormingBinding;
import java.util.ArrayList;
import java.util.List;

public class DewormingActivity extends AppCompatActivity {

    private ActivityDewormingBinding binding;
    private List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDewormingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddDewormingActivity.class)));

        // Data mẫu
        list.add(new HistoryAdapter.HistoryItem(
                "Tẩy giun định kỳ", "19/3/2025"));

        adapter = new HistoryAdapter(list,
                (position, item) -> {
                    // Nút Sửa → mở AddDewormingActivity
                    Intent intent = new Intent(this, AddDewormingActivity.class);
                    intent.putExtra("is_edit", true);
                    intent.putExtra("title", item.title);
                    intent.putExtra("date", item.date);
                    startActivity(intent);
                },
                position -> {} // Xóa đã xử lý trong adapter
        );

        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);
    }
}