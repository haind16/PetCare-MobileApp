package com.nhom08.petcare.ui.health.diary;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityDiaryDetailBinding;
import java.util.Calendar;

public class DiaryDetailActivity extends AppCompatActivity {

    private ActivityDiaryDetailBinding binding;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận data
        String name = getIntent().getStringExtra("name");
        String date = getIntent().getStringExtra("date");

        if (name != null) binding.tvActivityName.setText(name);

        // Set ngày hiện tại
        updateDateText();

        // Hủy
        binding.btnCancel.setOnClickListener(v -> finish());

        // Sửa ngày
        binding.btnEditDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate.set(year, month, day);
                updateDateText();
            },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Lưu
        binding.btnSave.setOnClickListener(v -> {
            String comment = binding.etComment.getText().toString().trim();
            // TODO: lưu vào database
            Toast.makeText(this, "Đã lưu nhật ký!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updateDateText() {
        String[] days = {"Chủ nhật", "Thứ hai", "Thứ ba",
                "Thứ tư", "Thứ năm", "Thứ sáu", "Thứ bảy"};
        int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK) - 1;
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        int month = selectedDate.get(Calendar.MONTH) + 1;
        int year = selectedDate.get(Calendar.YEAR);

        String dateStr = "Vào " + days[dayOfWeek] + ", ngày "
                + day + " tháng " + month + " năm " + year;
        binding.tvDate.setText(dateStr);
    }
}