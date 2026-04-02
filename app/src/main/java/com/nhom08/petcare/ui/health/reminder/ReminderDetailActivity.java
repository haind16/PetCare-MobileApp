package com.nhom08.petcare.ui.health.reminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nhom08.petcare.data.model.NhacNho;
import com.nhom08.petcare.data.repository.NhacNhoRepository;
import com.nhom08.petcare.databinding.ActivityReminderDetailBinding;
import com.nhom08.petcare.utils.PetManager;

import java.util.Calendar;

public class ReminderDetailActivity extends AppCompatActivity {

    private ActivityReminderDetailBinding binding;
    private Calendar selectedDateTime = Calendar.getInstance();
    private NhacNhoRepository repo;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReminderDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repo  = new NhacNhoRepository(this);
        petId = PetManager.getInstance(this).getCurrentPetId();

        String activityType = getIntent().getStringExtra("activity_type");
        if (activityType != null) binding.tvActivityType.setText(activityType);

        updateDateText();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnPickDate.setOnClickListener(v -> showDatePicker());

        binding.btnSave.setOnClickListener(v -> {
            if (petId == null || petId.isEmpty()) {
                Toast.makeText(this, "Chưa chọn thú cưng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra thời gian chưa qua
            if (selectedDateTime.getTimeInMillis() <= System.currentTimeMillis()) {
                Toast.makeText(this, "Vui lòng chọn thời gian trong tương lai",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String loai = binding.tvActivityType.getText().toString();
            String ngay = String.format("%02d/%02d/%d",
                    selectedDateTime.get(Calendar.DAY_OF_MONTH),
                    selectedDateTime.get(Calendar.MONTH) + 1,
                    selectedDateTime.get(Calendar.YEAR));
            String gio  = String.format("%02d:%02d",
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE));

            NhacNho item = new NhacNho();
            item.petId       = petId;
            item.loai        = loai;
            item.ngay        = ngay;
            item.gio         = gio;
            item.daHoanThanh = false;

            binding.btnSave.setEnabled(false);

            repo.add(item, r -> runOnUiThread(() -> {
                // Đặt AlarmManager sau khi lưu Room thành công
                ReminderScheduler.schedule(this, item,
                        selectedDateTime.getTimeInMillis());

                Toast.makeText(this, "Đã lưu lịch nhắc nhở!", Toast.LENGTH_SHORT).show();
                finish();
            }));
        });
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDateTime.set(year, month, day);
            showTimePicker();
        },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hour, minute) -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedDateTime.set(Calendar.MINUTE, minute);
            updateDateText();
        },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE), true).show();
    }

    private void updateDateText() {
        String dateStr = String.format("Ngày %d tháng %d năm %d  %02d:%02d",
                selectedDateTime.get(Calendar.DAY_OF_MONTH),
                selectedDateTime.get(Calendar.MONTH) + 1,
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE));
        binding.tvSelectedDate.setText(dateStr);
    }
}