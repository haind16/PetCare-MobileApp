package com.nhom08.petcare.ui.pet.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.CanNangDao;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.databinding.ActivityWeightBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeightActivity extends AppCompatActivity {

    private ActivityWeightBinding binding;
    private HistoryAdapter adapter;
    private List<HistoryAdapter.HistoryItem> displayList = new ArrayList<>();
    private List<CanNang> dataList = new ArrayList<>();
    private CanNangDao canNangDao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        petId = getIntent().getStringExtra("pet_id");
        canNangDao = AppDatabase.getInstance(this).canNangDao();

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddWeightActivity.class);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        adapter = new HistoryAdapter(
                displayList,
                (position, item) -> {
                    // Sửa: mở AddWeightActivity với dữ liệu cũ
                    CanNang cn = dataList.get(position);
                    Intent intent = new Intent(this, AddWeightActivity.class);
                    intent.putExtra("pet_id", petId);
                    intent.putExtra("is_edit", true);
                    intent.putExtra("record_id", cn.id);
                    intent.putExtra("weight", cn.canNang);
                    intent.putExtra("date", cn.ngay);
                    startActivity(intent);
                },
                position -> {
                    // Xóa: thực hiện xóa trong DB
                    CanNang cn = dataList.get(position);
                    new Thread(() -> {
                        canNangDao.deleteById(cn.id);
                        runOnUiThread(this::loadData);
                    }).start();
                }
        );

        binding.rvWeightHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWeightHistory.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        if (petId == null) return;
        new Thread(() -> {
            List<CanNang> records = canNangDao.getAllByPet(petId);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Collections.sort(records, (c1, c2) -> {
                try {
                    Date date1 = sdf.parse(c1.ngay);
                    Date date2 = sdf.parse(c2.ngay);
                    if (date1 != null && date2 != null) {
                        // LƯU Ý: date2 so sánh với date1 để xếp GIẢM DẦN (Mới nhất lên đầu)
                        // Nếu bạn muốn xếp TĂNG DẦN (Cũ nhất lên đầu) thì đổi lại thành: return date1.compareTo(date2);
                        return date2.compareTo(date1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            });

            runOnUiThread(() -> {
                dataList.clear();
                dataList.addAll(records);
                displayList.clear();
                for (CanNang cn : records) {
                    displayList.add(new HistoryAdapter.HistoryItem(cn.canNang + " kg", cn.ngay));
                }
                adapter.notifyDataSetChanged();
                // Hiển thị empty state nếu không có dữ liệu
                binding.tvEmpty.setVisibility(displayList.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvWeightHistory.setVisibility(displayList.isEmpty() ? View.GONE : View.VISIBLE);
            });
        }).start();
    }
}