package com.nhom08.petcare.ui.health.medical;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.CanNangDao;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.data.model.HoSoYTe;
import com.nhom08.petcare.data.repository.HoSoYTeRepository;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityMedicalRecordBinding;
import com.nhom08.petcare.utils.PetManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity hiển thị danh sách hồ sơ y tế của thú cưng.
 * Cho phép xem tóm tắt thông tin thú cưng (tên, ngày sinh, cân nặng hiện tại) 
 * và danh sách các lần thăm khám y tế đã được ghi nhận.
 */
public class MedicalRecordActivity extends AppCompatActivity {

    private ActivityMedicalRecordBinding binding;
    private List<MedicalRecordAdapter.RecordItem> list = new ArrayList<>();
    private MedicalRecordAdapter adapter;
    private HoSoYTeRepository repo;
    private PetRepository petRepo;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMedicalRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo    = new HoSoYTeRepository(this);
        petRepo = new PetRepository(this);

        // Xác định ID thú cưng cần xem hồ sơ y tế
        petId = getIntent().getStringExtra("pet_id");
        if (petId == null || petId.isEmpty()) {
            petId = PetManager.getInstance(this).getCurrentPetId();
        }

        binding.btnBack.setOnClickListener(v -> finish());
        
        // Mở màn hình thêm hồ sơ y tế mới
        binding.btnAddRecord.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddMedicalRecordActivity.class);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        // Cấu hình adapter cho danh sách hồ sơ y tế
        adapter = new MedicalRecordAdapter(list, item -> {
            // Mở chi tiết hồ sơ y tế khi người dùng nhấn vào một item
            Intent intent = new Intent(this, MedicalRecordDetailActivity.class);
            intent.putExtra("record_id", item.id);
            startActivity(intent);
        });

        binding.rvMedicalRecords.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMedicalRecords.setAdapter(adapter);

        loadPetInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật danh sách hồ sơ y tế mỗi khi quay lại màn hình
        loadData();
    }

    /**
     * Tải thông tin thú cưng để hiển thị ở phần Header.
     */
    private void loadPetInfo() {
        if (petId == null || petId.isEmpty()) return;
        petRepo.getPetById(petId, pet -> runOnUiThread(() -> {
            if (pet == null) return;
            binding.tvPetName.setText(pet.tenThuCung != null ? pet.tenThuCung : "");
            binding.tvPetAge.setText(pet.ngaySinh != null && !pet.ngaySinh.isEmpty()
                    ? "Ngày sinh: " + pet.ngaySinh : "Ngày sinh: Chưa có");
            
            // Truy vấn cân nặng mới nhất từ lịch sử cân nặng
            new Thread(() -> {
                CanNangDao canNangDao = AppDatabase.getInstance(getApplicationContext()).canNangDao();
                List<CanNang> weightRecords = canNangDao.getAllByPet(petId);

                if (weightRecords != null && !weightRecords.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Collections.sort(weightRecords, (c1, c2) -> {
                        try {
                            Date date1 = sdf.parse(c1.ngay);
                            Date date2 = sdf.parse(c2.ngay);
                            if (date1 != null && date2 != null) {
                                return date2.compareTo(date1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    });

                    float latestWeight = weightRecords.get(0).canNang;
                    runOnUiThread(() -> binding.tvPetWeight.setText("Cân nặng: " + latestWeight + " kg"));
                } else {
                    runOnUiThread(() -> binding.tvPetWeight.setText(pet.canNang > 0
                            ? "Cân nặng: " + pet.canNang + " kg" : "Cân nặng: Chưa có"));
                }
            }).start();

            // Hiển thị ảnh thú cưng
            if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
                if (pet.anhUrl.startsWith("http")) {
                    Glide.with(this)
                            .load(pet.anhUrl)
                            .placeholder(R.drawable.pet_welcome)
                            .circleCrop()
                            .into(binding.imgPet);
                } else {
                    Glide.with(this)
                            .load(new java.io.File(pet.anhUrl))
                            .placeholder(R.drawable.pet_welcome)
                            .circleCrop()
                            .into(binding.imgPet);
                }
            } else {
                binding.imgPet.setImageResource(R.drawable.pet_welcome);
            }
        }));
    }

    /**
     * Tải danh sách hồ sơ y tế của thú cưng từ database.
     */
    private void loadData() {
        if (petId == null || petId.isEmpty()) {
            Toast.makeText(this, "Chưa chọn thú cưng", Toast.LENGTH_SHORT).show();
            return;
        }

        repo.getAll(petId, records -> runOnUiThread(() -> {
            list.clear();
            for (HoSoYTe r : records) {
                list.add(new MedicalRecordAdapter.RecordItem(
                        r.id, r.ngayKham, r.loaiKham,
                        r.donThuoc  != null && !r.donThuoc.isEmpty(),
                        r.tiemPhong != null && !r.tiemPhong.isEmpty()
                ));
            }
            binding.rvMedicalRecords.setVisibility(list.isEmpty() ? View.GONE : View.VISIBLE);
            adapter.notifyDataSetChanged();
        }));
    }
}