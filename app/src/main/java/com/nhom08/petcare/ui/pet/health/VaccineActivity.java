package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.LichTiemPhongDao;
import com.nhom08.petcare.data.model.LichTiemPhong;
import com.nhom08.petcare.databinding.ActivityVaccineBinding;
import java.util.ArrayList;
import java.util.List;

public class VaccineActivity extends AppCompatActivity {

    private ActivityVaccineBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryAdapter.HistoryItem> displayList = new ArrayList<>();
    private List<LichTiemPhong> dataList = new ArrayList<>();
    private LichTiemPhongDao dao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVaccineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petId = getIntent().getStringExtra("pet_id");
        dao = AppDatabase.getInstance(this).lichTiemPhongDao();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(this, AddVaccineActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        adapter = new HistoryAdapter(displayList,
                (position, item) -> {
                    LichTiemPhong record = dataList.get(position);
                    Intent i = new Intent(this, AddVaccineActivity.class);
                    i.putExtra("pet_id", petId);
                    i.putExtra("is_edit", true);
                    i.putExtra("record_id", record.id);
                    i.putExtra("vaccine_name", record.tenVacxin);
                    i.putExtra("vaccine_date", record.ngayTiem);
                    i.putExtra("vaccine_reminder", record.ngayNhacNho);
                    startActivity(i);
                },
                position -> {
                    LichTiemPhong record = dataList.get(position);
                    new Thread(() -> {
                        dao.deleteById(record.id);
                        runOnUiThread(this::loadData);
                    }).start();
                }
        );

        binding.rvVaccineList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVaccineList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (petId == null) return;
        new Thread(() -> {
            List<LichTiemPhong> records = dao.getAllByPet(petId);
            runOnUiThread(() -> {
                dataList.clear();
                dataList.addAll(records);
                displayList.clear();
                for (LichTiemPhong r : records) {
                    displayList.add(new HistoryAdapter.HistoryItem(
                            r.tenVacxin,
                            "Ngày tiêm: " + r.ngayTiem
                                    + (r.ngayNhacNho != null && !r.ngayNhacNho.isEmpty()
                                    ? "  •  Nhắc: " + r.ngayNhacNho : "")));
                }
                adapter.notifyDataSetChanged();
                binding.tvEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvVaccineList.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}