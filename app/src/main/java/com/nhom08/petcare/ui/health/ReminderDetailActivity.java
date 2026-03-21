package com.nhom08.petcare.ui.health;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityReminderDetailBinding;
import java.util.Calendar;

public class ReminderDetailActivity extends AppCompatActivity {

    private ActivityReminderDetailBinding binding;
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nhận tên loại hoạt động
        String activityType = getIntent().getStringExtra("activity_type");
        if (activityType != null) {
            binding.tvActivityType.setText(activityType);
        }

        binding.btnBack.setOnClickListener(v -> finish());

        // Chọn ngày giờ
        binding.btnPickDate.setOnClickListener(v -> showDatePicker());

        // Lưu
        binding.btnSave.setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu lịch nhắc nhở!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDateTime.set(year, month, day);
            showTimePicker();
        }, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();
        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedDateTime.set(Calendar.MINUTE, minute);
            // Hiển thị ngày giờ đã chọn
            String dateStr = String.format("Ngày %d tháng %d năm %d %d:%02d",
                    selectedDateTime.get(Calendar.DAY_OF_MONTH),
                    selectedDateTime.get(Calendar.MONTH) + 1,
                    selectedDateTime.get(Calendar.YEAR),
                    hour, minute);
            binding.tvSelectedDate.setText(dateStr);
        }, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE), false).show();
    }
}