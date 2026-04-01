package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.DonThuocDao;
import com.nhom08.petcare.data.model.DonThuoc;
import com.nhom08.petcare.databinding.ActivityPrescriptionBinding;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionActivity extends AppCompatActivity {

    private ActivityPrescriptionBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryAdapter.HistoryItem> displayList = new ArrayList<>();
    private List<DonThuoc> dataList = new ArrayList<>();
    private DonThuocDao dao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petId = getIntent().getStringExtra("pet_id");
        dao   = AppDatabase.getInstance(this).donThuocDao();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddPrescriptionActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        adapter = new HistoryAdapter(displayList,
                (position, item) -> {
                    DonThuoc record = dataList.get(position);
                    Intent i = new Intent(this, AddPrescriptionActivity.class);
                    i.putExtra("pet_id", petId);
                    i.putExtra("is_edit", true);
                    i.putExtra("record_id", record.id);
                    i.putExtra("title", record.tenThuoc);
                    i.putExtra("dosage", record.lieuLuong);
                    i.putExtra("usage", record.cachDung);
                    startActivity(i);
                },
                position -> {
                    DonThuoc record = dataList.get(position);
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
            List<DonThuoc> records = dao.getAllByPet(petId);
            runOnUiThread(() -> {
                dataList.clear();
                dataList.addAll(records);
                displayList.clear();
                for (DonThuoc r : records) {
                    String sub = (r.lieuLuong != null && !r.lieuLuong.isEmpty() ? r.lieuLuong : "")
                            + (r.cachDung != null && !r.cachDung.isEmpty() ? "  •  " + r.cachDung : "");
                    displayList.add(new HistoryAdapter.HistoryItem(r.tenThuoc, sub));
                }
                adapter.notifyDataSetChanged();
                binding.tvEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvList.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}