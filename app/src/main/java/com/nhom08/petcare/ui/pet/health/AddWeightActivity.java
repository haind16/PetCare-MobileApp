package com.nhom08.petcare.ui.pet.health;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.CanNangDao;
import com.nhom08.petcare.data.local.dao.ThuCungDao;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.databinding.ActivityAddWeightBinding;
import java.util.Calendar;
import java.util.UUID;

public class AddWeightActivity extends AppCompatActivity {

    private ActivityAddWeightBinding binding;
    private CanNangDao canNangDao;
    private ThuCungDao thuCungDao;
    private String petId;
    private boolean isEdit = false;
    private String recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        canNangDao = AppDatabase.getInstance(this).canNangDao();
        thuCungDao = AppDatabase.getInstance(this).thuCungDao();

        petId   = getIntent().getStringExtra("pet_id");
        isEdit  = getIntent().getBooleanExtra("is_edit", false);
        recordId = getIntent().getStringExtra("record_id");

        binding.btnBack.setOnClickListener(v -> finish());

        if (isEdit) {
            // Điền dữ liệu cũ vào form
            float weight = getIntent().getFloatExtra("weight", 0f);
            String date  = getIntent().getStringExtra("date");
            binding.etWeight.setText(weight > 0 ? String.valueOf(weight) : "");
            if (date != null) {
                try {
                    // date format: dd/MM/yyyy
                    String[] parts = date.split("/");
                    binding.datePicker.updateDate(
                            Integer.parseInt(parts[2]),  // year
                            Integer.parseInt(parts[1]) - 1, // month (0-based)
                            Integer.parseInt(parts[0])   // day
                    );
                } catch (Exception ignored) {}
            }
        }

        binding.btnSave.setOnClickListener(v -> saveData());
    }

    private void saveData() {
        String weightStr = binding.etWeight.getText().toString().trim();
        if (weightStr.isEmpty()) {
            binding.etWeight.setError("Vui lòng nhập cân nặng");
            return;
        }

        float weight;
        try {
            weight = Float.parseFloat(weightStr);
        } catch (NumberFormatException e) {
            binding.etWeight.setError("Cân nặng không hợp lệ");
            return;
        }

        if (weight <= 0 || weight > 200) {
            binding.etWeight.setError("Cân nặng phải từ 0 đến 200 kg");
            return;
        }

        int day   = binding.datePicker.getDayOfMonth();
        int month = binding.datePicker.getMonth() + 1; // 0-based → 1-based
        int year  = binding.datePicker.getYear();
        String date = String.format("%02d/%02d/%04d", day, month, year);

        // Không cho phép chọn ngày tương lai
        Calendar selected = Calendar.getInstance();
        selected.set(year, binding.datePicker.getMonth(), day);
        if (selected.after(Calendar.getInstance())) {
            Toast.makeText(this, "Không thể chọn ngày trong tương lai", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            CanNang record = new CanNang();
            record.id     = isEdit && recordId != null ? recordId : UUID.randomUUID().toString();
            record.petId  = petId;
            record.canNang = weight;
            record.ngay   = date;
            canNangDao.insert(record);

            // Cập nhật canNang mới nhất lên ThuCung (lấy bản ghi mới nhất theo ngày)
            java.util.List<CanNang> all = canNangDao.getAllByPet(petId);
            if (!all.isEmpty()) {
                // getAllByPet sắp xếp ASC → lấy phần tử cuối = mới nhất
                float latestWeight = all.get(all.size() - 1).canNang;
                thuCungDao.updateCanNang(petId, latestWeight);
            }

            runOnUiThread(() -> {
                Toast.makeText(this,
                        isEdit ? "Đã cập nhật cân nặng!" : "Đã lưu cân nặng!",
                        Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}