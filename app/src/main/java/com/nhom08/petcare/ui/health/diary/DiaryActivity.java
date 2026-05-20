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

/**
 * Activity hiển thị danh sách nhật ký hoạt động của thú cưng.
 * Người dùng có thể xem lại lịch sử các hoạt động, thêm nhật ký mới hoặc xóa nhật ký cũ.
 */
public class DiaryActivity extends AppCompatActivity {

    private ActivityDiaryBinding binding;
    private final List<HistoryAdapter.HistoryItem> list = new ArrayList<>();
    private final List<NhatKy> rawList = new ArrayList<>(); // Giữ dữ liệu gốc từ database để xử lý ID
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
        
        // Chuyển sang màn hình thêm nhật ký mới
        binding.btnAddActivity.setOnClickListener(v ->
                startActivity(new Intent(this, AddDiaryActivity.class)));

        // Khởi tạo adapter với listener cho sự kiện click xem chi tiết và xóa
        adapter = new HistoryAdapter(list,
                // Xử lý khi người dùng nhấn vào một item nhật ký
                (position, item) -> {
                    NhatKy nk = rawList.get(position);
                    Intent intent = new Intent(this, DiaryDetailActivity.class);
                    intent.putExtra("id",   nk.id);
                    intent.putExtra("name", nk.loaiHoatDong);
                    intent.putExtra("date", nk.ngay);
                    intent.putExtra("note", nk.ghiChu);
                    startActivity(intent);
                },
                // Xử lý khi người dùng nhấn nút xóa nhật ký
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
        // Tải lại dữ liệu khi quay lại màn hình
        loadData();
    }

    /**
     * Tải danh sách nhật ký từ Repository và chuyển đổi sang dạng hiển thị.
     */
    private void loadData() {
        if (petId == null || petId.isEmpty()) return;

        repo.getAll(petId, records -> runOnUiThread(() -> {
            rawList.clear();
            list.clear();
            for (NhatKy nk : records) {
                rawList.add(nk);
                // HistoryItem là model chung dùng cho nhiều màn hình hiển thị lịch sử
                list.add(new HistoryAdapter.HistoryItem(
                        nk.loaiHoatDong,
                        nk.ngay
                ));
            }
            adapter.notifyDataSetChanged();
        }));
    }
}