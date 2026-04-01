package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.TayGiunDao;
import com.nhom08.petcare.data.model.TayGiun;
import com.nhom08.petcare.databinding.ActivityDewormingBinding;
import java.util.ArrayList;
import java.util.List;

public class DewormingActivity extends AppCompatActivity {

    private ActivityDewormingBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryAdapter.HistoryItem> displayList = new ArrayList<>();
    private List<TayGiun> dataList = new ArrayList<>();
    private TayGiunDao dao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDewormingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petId = getIntent().getStringExtra("pet_id");
        dao   = AppDatabase.getInstance(this).tayGiunDao();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddDewormingActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        adapter = new HistoryAdapter(displayList,
                (position, item) -> {
                    TayGiun record = dataList.get(position);
                    Intent i = new Intent(this, AddDewormingActivity.class);
                    i.putExtra("pet_id", petId);
                    i.putExtra("is_edit", true);
                    i.putExtra("record_id", record.id);
                    i.putExtra("date", record.ngay);
                    i.putExtra("note", record.ghiChu);
                    startActivity(i);
                },
                position -> {
                    TayGiun record = dataList.get(position);
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
            List<TayGiun> records = dao.getAllByPet(petId);
            runOnUiThread(() -> {
                dataList.clear();
                dataList.addAll(records);
                displayList.clear();
                for (TayGiun r : records) {
                    String title = (r.ghiChu != null && !r.ghiChu.isEmpty())
                            ? r.ghiChu : "Tẩy giun định kỳ";
                    displayList.add(new HistoryAdapter.HistoryItem(title, r.ngay));
                }
                adapter.notifyDataSetChanged();
                binding.tvEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvList.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}