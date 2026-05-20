package com.nhom08.petcare.ui.pet.profile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.*;
import com.nhom08.petcare.data.model.*;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityPetProfileBinding;
import com.nhom08.petcare.ui.pet.health.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Activity hiển thị hồ sơ chi tiết của một thú cưng.
 * Cung cấp thông tin tổng quan và các mục quản lý sức khỏe chuyên sâu: 
 * Cân nặng, Tiêm phòng, Tẩy giun, Dị ứng, Bệnh nền và Đơn thuốc.
 */
public class PetProfileActivity extends AppCompatActivity {

    private ActivityPetProfileBinding binding;
    private PetRepository repository;
    private String petId;

    // Các DAO để truy vấn thông tin tóm tắt sức khỏe
    private LichTiemPhongDao lichTiemPhongDao;
    private TayGiunDao tayGiunDao;
    private DiUngDao diUngDao;
    private BenhNenDao benhNenDao;
    private DonThuocDao donThuocDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository       = new PetRepository(this);
        lichTiemPhongDao = AppDatabase.getInstance(this).lichTiemPhongDao();
        tayGiunDao       = AppDatabase.getInstance(this).tayGiunDao();
        diUngDao         = AppDatabase.getInstance(this).diUngDao();
        benhNenDao       = AppDatabase.getInstance(this).benhNenDao();
        donThuocDao      = AppDatabase.getInstance(this).donThuocDao();

        // Lấy ID thú cưng từ Intent
        petId = getIntent().getStringExtra("pet_id");

        binding.btnBack.setOnClickListener(v -> finish());
        
        // Nút chỉnh sửa thông tin cơ bản
        binding.btnEdit.setOnClickListener(v -> {
            if (petId == null) return;
            Intent intent = new Intent(this, EditPetActivity.class);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        // Điều hướng đến các màn hình chi tiết sức khỏe
        binding.btnWeight.setOnClickListener(v -> {
            Intent i = new Intent(this, WeightActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });
        
        binding.btnDeworming.setOnClickListener(v -> {
            Intent i = new Intent(this, DewormingActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });
        
        binding.btnAllergy.setOnClickListener(v -> {
            Intent i = new Intent(this, AllergyActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });
        
        binding.btnDisease.setOnClickListener(v -> {
            Intent i = new Intent(this, DiseaseActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại dữ liệu mỗi khi quay lại màn hình
        loadPet();
        loadHealthSummary();
    }

    /**
     * Tải thông tin cơ bản của thú cưng từ database.
     */
    private void loadPet() {
        if (petId == null) return;
        repository.getPetById(petId, pet -> runOnUiThread(() -> {
            if (pet == null) { finish(); return; }

            binding.tvPetName.setText(pet.tenThuCung);
            String loaiGiong = (pet.loai != null ? pet.loai : "")
                    + (pet.giong != null && !pet.giong.isEmpty() ? " • " + pet.giong : "");
            binding.tvPetBreed.setText(loaiGiong.isEmpty() ? "Chưa rõ" : loaiGiong);
            binding.tvPetGender.setText(pet.gioiTinh != null ? pet.gioiTinh : "Chưa rõ");
            binding.tvPetAge.setText(tinhTuoi(pet.ngaySinh));

            // Hiển thị ảnh sử dụng Glide
            if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
                if (pet.anhUrl.startsWith("http")) {
                    Glide.with(this).load(pet.anhUrl)
                            .centerCrop().placeholder(R.drawable.pet_welcome)
                            .into(binding.imgPet);
                } else {
                    Glide.with(this).load(new File(pet.anhUrl))
                            .centerCrop().placeholder(R.drawable.pet_welcome)
                            .into(binding.imgPet);
                }
            } else {
                binding.imgPet.setImageResource(R.drawable.pet_welcome);
            }

            // Truy vấn cân nặng ghi nhận gần nhất từ lịch sử cân nặng
            new Thread(() -> {
                CanNangDao canNangDao = AppDatabase.getInstance(getApplicationContext()).canNangDao();
                List<CanNang> weightRecords = canNangDao.getAllByPet(petId);

                float latestWeight = pet.canNang;

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
                    latestWeight = weightRecords.get(0).canNang;
                }

                final float finalWeight = latestWeight;
                runOnUiThread(() -> {
                    binding.tvPetWeight.setText(finalWeight > 0 ? finalWeight + " kg" : "Chưa có");
                });
            }).start();
        }));
    }

    /**
     * Tải và hiển thị thông tin tóm tắt của các mục sức khỏe (dòng chữ nhỏ dưới mỗi nút).
     */
    private void loadHealthSummary() {
        if (petId == null) return;

        new Thread(() -> {
            // 1. Tẩy giun - Lấy ngày gần nhất
            List<TayGiun> dewormings = tayGiunDao.getAllByPet(petId);
            String dewormingText = "Chưa có";
            if (!dewormings.isEmpty()) {
                dewormingText = dewormings.get(0).ngay;
            }

            // 2. Dị ứng - Liệt kê tên hoặc số lượng
            List<DiUng> allergies = diUngDao.getAllByPet(petId);
            String allergyText = "Chưa có";
            if (!allergies.isEmpty()) {
                if (allergies.size() == 1) {
                    allergyText = allergies.get(0).chatGayDiUng;
                } else {
                    allergyText = allergies.get(0).chatGayDiUng + " +" + (allergies.size() - 1);
                }
            }

            // 3. Bệnh nền
            List<BenhNen> diseases = benhNenDao.getAllByPet(petId);
            String diseaseText = "Chưa có";
            if (!diseases.isEmpty()) {
                if (diseases.size() == 1) {
                    diseaseText = diseases.get(0).tenBenh;
                } else {
                    diseaseText = diseases.get(0).tenBenh + " +" + (diseases.size() - 1);
                }
            }

            // Cập nhật UI
            final String fDeworming    = dewormingText;
            final String fAllergy      = allergyText;
            final String fDisease      = diseaseText;

            runOnUiThread(() -> {
                setSummary(binding.tvDewormingSummary,    fDeworming,    false);
                setSummary(binding.tvAllergySummary,      fAllergy,      false);
                setSummary(binding.tvDiseaseSummary,      fDisease,      true);
            });
        }).start();
    }

    /**
     * Hàm helper để hiển thị chuỗi tóm tắt với màu sắc phù hợp.
     */
    private void setSummary(android.widget.TextView tv, String text, boolean bracket) {
        boolean empty = "Chưa có".equals(text);
        tv.setText(bracket && empty ? "Chưa có" : text);
        tv.setTextColor(empty ? 0xFF888888 : getColor(R.color.primary_blue));
    }

    /**
     * Hàm tính tuổi dựa trên ngày sinh.
     */
    private String tinhTuoi(String ngaySinh) {
        if (ngaySinh == null || ngaySinh.isEmpty()) return "Chưa rõ";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate born = LocalDate.parse(ngaySinh, fmt);
            LocalDate now  = LocalDate.now();
            if (born.isAfter(now)) return "Chưa sinh";
            Period period = Period.between(born, now);
            if (period.getYears()  > 0) return period.getYears()  + " tuổi";
            if (period.getMonths() > 0) return period.getMonths() + " tháng";
            return period.getDays() + " ngày";
        } catch (Exception e) { return ngaySinh; }
    }
}