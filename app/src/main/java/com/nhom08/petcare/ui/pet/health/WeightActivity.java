package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityWeightBinding;
import java.util.ArrayList;
import java.util.List;

public class WeightActivity extends AppCompatActivity {

    private ActivityWeightBinding binding;
    private List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddWeightActivity.class)));

        // Data mẫu
        list.add(new HistoryAdapter.HistoryItem("4.5 kg", "1/1/2026"));

        adapter = new HistoryAdapter(list,
                (position, item) -> {
                    Intent intent = new Intent(this, AddWeightActivity.class);
                    intent.putExtra("is_edit", true);
                    intent.putExtra("weight", item.title);
                    intent.putExtra("date", item.date);
                    startActivity(intent);
                },
                position -> {}
        );

        binding.rvWeightHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWeightHistory.setAdapter(adapter);
    }
}