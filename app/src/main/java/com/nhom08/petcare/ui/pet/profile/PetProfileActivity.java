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

public class PetProfileActivity extends AppCompatActivity {

    private ActivityPetProfileBinding binding;
    private PetRepository repository;
    private String petId;

    // DAOs
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

        petId = getIntent().getStringExtra("pet_id");

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEdit.setOnClickListener(v -> {
            if (petId == null) return;
            Intent intent = new Intent(this, EditPetActivity.class);
            intent.putExtra("pet_id", petId);
            startActivity(intent);
        });

        binding.btnWeight.setOnClickListener(v -> {
            Intent i = new Intent(this, WeightActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });
//        binding.btnVaccine.setOnClickListener(v -> {
//            Intent i = new Intent(this, VaccineActivity.class);
//            i.putExtra("pet_id", petId);
//            startActivity(i);
//        });
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
//        binding.btnPrescription.setOnClickListener(v -> {
//            Intent i = new Intent(this, PrescriptionActivity.class);
//            i.putExtra("pet_id", petId);
//            startActivity(i);
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPet();
        loadHealthSummary();
    }

    // ── Load thông tin cơ bản thú cưng ───────────────────────────────────────

    private void loadPet() {
        if (petId == null) return;
        repository.getPetById(petId, pet -> runOnUiThread(() -> {
            if (pet == null) { finish(); return; }

            // Lấy thông tin cơ bản
            binding.tvPetName.setText(pet.tenThuCung);
            String loaiGiong = (pet.loai != null ? pet.loai : "")
                    + (pet.giong != null && !pet.giong.isEmpty() ? " • " + pet.giong : "");
            binding.tvPetBreed.setText(loaiGiong.isEmpty() ? "Chưa rõ" : loaiGiong);
            binding.tvPetGender.setText(pet.gioiTinh != null ? pet.gioiTinh : "Chưa rõ");
            binding.tvPetAge.setText(tinhTuoi(pet.ngaySinh));

            // Hiển thị ảnh
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

            // 🌟 CHẠY LUỒNG PHỤ ĐỂ LẤY CÂN NẶNG MỚI NHẤT
            new Thread(() -> {
                CanNangDao canNangDao = AppDatabase.getInstance(getApplicationContext()).canNangDao();
                List<CanNang> weightRecords = canNangDao.getAllByPet(petId);

                float latestWeight = pet.canNang; // Fallback lấy cân nặng gốc

                if (weightRecords != null && !weightRecords.isEmpty()) {
                    // Sắp xếp danh sách giảm dần (Ngày mới nhất lên đầu)
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

                // Cập nhật UI trên Main Thread
                final float finalWeight = latestWeight;
                runOnUiThread(() -> {
                    binding.tvPetWeight.setText(finalWeight > 0 ? finalWeight + " kg" : "Chưa có");
                });
            }).start();
            // 🌟 KẾT THÚC XỬ LÝ CÂN NẶNG
        }));
    }

    private void bindPetData(ThuCung pet) {
        binding.tvPetName.setText(pet.tenThuCung);
        String loaiGiong = (pet.loai != null ? pet.loai : "")
                + (pet.giong != null && !pet.giong.isEmpty() ? " • " + pet.giong : "");
        binding.tvPetBreed.setText(loaiGiong.isEmpty() ? "Chưa rõ" : loaiGiong);
        binding.tvPetGender.setText(pet.gioiTinh != null ? pet.gioiTinh : "Chưa rõ");
        binding.tvPetAge.setText(tinhTuoi(pet.ngaySinh));
        binding.tvPetWeight.setText(pet.canNang > 0 ? pet.canNang + " kg" : "Chưa có");

        if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
            if (pet.anhUrl.startsWith("http")) {
                // Nếu là link Cloudinary
                Glide.with(this).load(pet.anhUrl)
                        .centerCrop().placeholder(R.drawable.pet_welcome)
                        .into(binding.imgPet);
            } else {
                // Nếu là file cũ offline trong máy
                Glide.with(this).load(new File(pet.anhUrl))
                        .centerCrop().placeholder(R.drawable.pet_welcome)
                        .into(binding.imgPet);
            }
        } else {
            binding.imgPet.setImageResource(R.drawable.pet_welcome);
        }
    }

    // ── Load summary từng mục sức khoẻ từ Room ───────────────────────────────

    private void loadHealthSummary() {
        if (petId == null) return;

        new Thread(() -> {
            // 1. Lịch tiêm phòng — lấy bản ghi mới nhất (ngày tiêm gần nhất)
            List<LichTiemPhong> vaccines = lichTiemPhongDao.getAllByPet(petId);
            String vaccineText = "Chưa có";
            if (!vaccines.isEmpty()) {
                // getAllByPet không có ORDER BY → lấy bản ghi cuối cùng
                LichTiemPhong latest = vaccines.get(vaccines.size() - 1);
                vaccineText = latest.ngayTiem != null ? latest.ngayTiem : latest.tenVacxin;
            }

            // 2. Tẩy giun — lấy ngày mới nhất (DAO sắp xếp DESC)
            List<TayGiun> dewormings = tayGiunDao.getAllByPet(petId);
            String dewormingText = "Chưa có";
            if (!dewormings.isEmpty()) {
                dewormingText = dewormings.get(0).ngay; // DESC → index 0 = mới nhất
            }

            // 3. Dị ứng — ghép tên các chất
            List<DiUng> allergies = diUngDao.getAllByPet(petId);
            String allergyText = "Chưa có";
            if (!allergies.isEmpty()) {
                if (allergies.size() == 1) {
                    allergyText = allergies.get(0).chatGayDiUng;
                } else {
                    // Hiện bản đầu + số còn lại
                    allergyText = allergies.get(0).chatGayDiUng
                            + " +" + (allergies.size() - 1);
                }
            }

            // 4. Bệnh nền — tương tự dị ứng
            List<BenhNen> diseases = benhNenDao.getAllByPet(petId);
            String diseaseText = "Chưa có";
            if (!diseases.isEmpty()) {
                if (diseases.size() == 1) {
                    diseaseText = diseases.get(0).tenBenh;
                } else {
                    diseaseText = diseases.get(0).tenBenh
                            + " +" + (diseases.size() - 1);
                }
            }

            // 5. Đơn thuốc — số lượng đơn
            List<DonThuoc> prescriptions = donThuocDao.getAllByPet(petId);
            String prescriptionText = "Chưa có";
            if (!prescriptions.isEmpty()) {
                prescriptionText = prescriptions.size() + " đơn";
            }

            // Update UI trên main thread
            final String fVaccine      = vaccineText;
            final String fDeworming    = dewormingText;
            final String fAllergy      = allergyText;
            final String fDisease      = diseaseText;
            final String fPrescription = prescriptionText;

            runOnUiThread(() -> {
//                setSummary(binding.tvVaccineSummary,      fVaccine,      false);
                setSummary(binding.tvDewormingSummary,    fDeworming,    false);
                setSummary(binding.tvAllergySummary,      fAllergy,      false);
                setSummary(binding.tvDiseaseSummary,      fDisease,      true);
//                setSummary(binding.tvPrescriptionSummary, fPrescription, true);
            });
        }).start();
    }

    private void setSummary(android.widget.TextView tv, String text, boolean bracket) {
        boolean empty = "Chưa có".equals(text);
        tv.setText(bracket && empty ? "Chưa có" : text);
        tv.setTextColor(empty ? 0xFF888888 : getColor(R.color.primary_blue));
    }

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