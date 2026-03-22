package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityAllergyBinding;
import java.util.ArrayList;
import java.util.List;

public class AllergyActivity extends AppCompatActivity {

    private ActivityAllergyBinding binding;
    private List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllergyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddAllergyActivity.class)));

        // Data mẫu
        list.add(new HistoryAdapter.HistoryItem("Hành lá", "19/3/2025"));

        adapter = new HistoryAdapter(list,
                (position, item) -> {
                    Intent intent = new Intent(this, AddAllergyActivity.class);
                    intent.putExtra("is_edit", true);
                    intent.putExtra("title", item.title);
                    startActivity(intent);
                },
                position -> {}
        );

        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);
    }
}