package com.nhom08.petcare.ui.health.diary;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nhom08.petcare.data.model.NhatKy;
import com.nhom08.petcare.data.repository.NhatKyRepository;
import com.nhom08.petcare.databinding.ActivityDiaryDetailBinding;
import com.nhom08.petcare.utils.PetManager;

import java.util.Calendar;

/**
 * Activity hiển thị chi tiết và cho phép chỉnh sửa/thêm mới một bản ghi nhật ký.
 * Người dùng có thể nhập ghi chú và chọn ngày thực hiện hoạt động.
 */
public class DiaryDetailActivity extends AppCompatActivity {

    private ActivityDiaryDetailBinding binding;
    private Calendar selectedDate = Calendar.getInstance();
    private NhatKyRepository repo;
    private String petId;
    private String recordId; // Sẽ có giá trị nếu là chỉnh sửa bản ghi cũ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo     = new NhatKyRepository(this);
        petId    = PetManager.getInstance(this).getCurrentPetId();
        recordId = getIntent().getStringExtra("id");

        // Lấy dữ liệu truyền từ Intent
        String name = getIntent().getStringExtra("name");
        String date = getIntent().getStringExtra("date");
        String note = getIntent().getStringExtra("note");

        if (name != null) binding.tvActivityName.setText(name);
        if (note != null) binding.etComment.setText(note);

        // Hiển thị ngày: Nếu là xem lại thì dùng ngày cũ, nếu thêm mới thì dùng ngày hiện tại
        if (date != null && !date.isEmpty()) {
            binding.tvDate.setText(date);
        } else {
            updateDateText();
        }

        binding.btnCancel.setOnClickListener(v -> finish());

        // Mở hộp thoại chọn ngày
        binding.btnEditDate.setOnClickListener(v ->
                new DatePickerDialog(this, (view, year, month, day) -> {
                    selectedDate.set(year, month, day);
                    updateDateText();
                },
                        selectedDate.get(Calendar.YEAR),
                        selectedDate.get(Calendar.MONTH),
                        selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        );

        // Lưu thông tin nhật ký vào Room Database
        binding.btnSave.setOnClickListener(v -> {
            String comment    = binding.etComment.getText().toString().trim();
            String actName    = binding.tvActivityName.getText().toString();
            String dateStr    = binding.tvDate.getText().toString();

            if (petId == null || petId.isEmpty()) {
                Toast.makeText(this, "Chưa chọn thú cưng", Toast.LENGTH_SHORT).show();
                return;
            }

            NhatKy nk = new NhatKy();
            if (recordId != null) nk.id = recordId; // Gán ID cũ để Update thay vì Insert mới
            nk.petId         = petId;
            nk.loaiHoatDong  = actName;
            nk.ngay          = dateStr;
            nk.ghiChu        = comment;

            binding.btnSave.setEnabled(false);
            repo.add(nk, r -> runOnUiThread(() -> {
                Toast.makeText(this, "Đã lưu nhật ký!", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }

    /**
     * Cập nhật chuỗi hiển thị ngày theo định dạng mô tả.
     */
    private void updateDateText() {
        String[] days = {"Chủ nhật", "Thứ hai", "Thứ ba",
                "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 1;
        int day   = selectedDate.get(Calendar.DAY_OF_MONTH);
        int month = selectedDate.get(Calendar.MONTH) + 1;
        int year  = selectedDate.get(Calendar.YEAR);

        binding.tvDate.setText("Vào " + days[dayOfWeek] + ", ngày "
                + day + " tháng " + month + " năm " + year);
    }
}