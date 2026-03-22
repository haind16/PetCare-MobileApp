package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityPrescriptionBinding;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity {

    private ActivityPrescriptionBinding binding;
    private List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddPrescriptionActivity.class)));

        adapter = new HistoryAdapter(list,
                (position, item) -> {
                    Intent intent = new Intent(this, AddPrescriptionActivity.class);
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