package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.BenhNenDao;
import com.nhom08.petcare.data.model.BenhNen;
import com.nhom08.petcare.databinding.ActivityDiseaseBinding;
import java.util.ArrayList;
import java.util.List;

public class DiseaseActivity extends AppCompatActivity {

    private ActivityDiseaseBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryAdapter.HistoryItem> displayList = new ArrayList<>();
    private List<BenhNen> dataList = new ArrayList<>();
    private BenhNenDao dao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiseaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petId = getIntent().getStringExtra("pet_id");
        dao   = AppDatabase.getInstance(this).benhNenDao();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddDiseaseActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        adapter = new HistoryAdapter(displayList,
                (position, item) -> {
                    BenhNen record = dataList.get(position);
                    Intent i = new Intent(this, AddDiseaseActivity.class);
                    i.putExtra("pet_id", petId);
                    i.putExtra("is_edit", true);
                    i.putExtra("record_id", record.id);
                    i.putExtra("title", record.tenBenh);
                    startActivity(i);
                },
                position -> {
                    BenhNen record = dataList.get(position);
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
            List<BenhNen> records = dao.getAllByPet(petId);
            runOnUiThread(() -> {
                dataList.clear();
                dataList.addAll(records);
                displayList.clear();
                for (BenhNen r : records) {
                    displayList.add(new HistoryAdapter.HistoryItem(r.tenBenh, ""));
                }
                adapter.notifyDataSetChanged();
                binding.tvEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvList.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}