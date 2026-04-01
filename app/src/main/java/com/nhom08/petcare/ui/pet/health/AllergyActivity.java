package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.DiUngDao;
import com.nhom08.petcare.data.model.DiUng;
import com.nhom08.petcare.databinding.ActivityAllergyBinding;
import java.util.ArrayList;
import java.util.List;

public class AllergyActivity extends AppCompatActivity {

    private ActivityAllergyBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryAdapter.HistoryItem> displayList = new ArrayList<>();
    private List<DiUng> dataList = new ArrayList<>();
    private DiUngDao dao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllergyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petId = getIntent().getStringExtra("pet_id");
        dao   = AppDatabase.getInstance(this).diUngDao();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddAllergyActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        adapter = new HistoryAdapter(displayList,
                (position, item) -> {
                    DiUng record = dataList.get(position);
                    Intent i = new Intent(this, AddAllergyActivity.class);
                    i.putExtra("pet_id", petId);
                    i.putExtra("is_edit", true);
                    i.putExtra("record_id", record.id);
                    i.putExtra("title", record.chatGayDiUng);
                    startActivity(i);
                },
                position -> {
                    DiUng record = dataList.get(position);
                    new Thread(() -> {
                        dao.deleteById(record.id);
                        runOnUiThread(this::loadData);
                    }).start();
                }
        );

        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (petId == null) return;
        new Thread(() -> {
            List<DiUng> records = dao.getAllByPet(petId);
            runOnUiThread(() -> {
                dataList.clear();
                dataList.addAll(records);
                displayList.clear();
                for (DiUng r : records) {
                    displayList.add(new HistoryAdapter.HistoryItem(r.chatGayDiUng, ""));
                }
                adapter.notifyDataSetChanged();
                binding.tvEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvList.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}