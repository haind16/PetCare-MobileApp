package com.nhom08.petcare.ui.health.diary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nhom08.petcare.data.model.NhatKy;
import com.nhom08.petcare.data.repository.NhatKyRepository;
import com.nhom08.petcare.databinding.ActivityDiaryBinding;
import com.nhom08.petcare.ui.pet.health.HistoryAdapter;
import com.nhom08.petcare.utils.PetManager;

import java.util.ArrayList;
import java.util.List;

public class DiaryActivity extends AppCompatActivity {

    private ActivityDiaryBinding binding;
    private final List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private final List<NhatKy> rawList = new ArrayList<>(); // giữ data gốc để lấy id khi xóa
    private HistoryAdapter adapter;
    private NhatKyRepository repo;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo  = new NhatKyRepository(this);
        petId = PetManager.getInstance(this).getCurrentPetId();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddActivity.setOnClickListener(v ->
                startActivity(new Intent(this, AddDiaryActivity.class)));

        adapter = new HistoryAdapter(list,
                // Click xem chi tiết
                (position, item) -> {
                    NhatKy nk = rawList.get(position);
                    Intent intent = new Intent(this, DiaryDetailActivity.class);
                    intent.putExtra("id",   nk.id);
                    intent.putExtra("name", nk.loaiHoatDong);
                    intent.putExtra("date", nk.ngay);
                    intent.putExtra("note", nk.ghiChu);
                    startActivity(intent);
                },
                // Xóa
                position -> {
                    NhatKy nk = rawList.get(position);
                    repo.delete(nk.id, r -> runOnUiThread(() -> {
                        rawList.remove(position);
                        list.remove(position);
                        adapter.notifyItemRemoved(position);
                    }));
                }
        );

        binding.rvDiary.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDiary.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (petId == null || petId.isEmpty()) return;

        repo.getAll(petId, records -> runOnUiThread(() -> {
            rawList.clear();
            list.clear();
            for (NhatKy nk : records) {
                rawList.add(nk);
                list.add(new HistoryAdapter.HistoryItem(
                        nk.loaiHoatDong,
                        nk.ngay
                ));
            }
            adapter.notifyDataSetChanged();
        }));
    }
}