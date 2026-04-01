package com.nhom08.petcare.ui.health.statistics;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.CanNangDao;
import com.nhom08.petcare.data.local.dao.NhatKyDao;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.data.model.NhatKy;
import com.nhom08.petcare.databinding.ActivityHealthStatisticsBinding;
import com.nhom08.petcare.ui.pet.health.WeightActivity;
import com.nhom08.petcare.ui.health.diary.DiaryActivity;
import com.nhom08.petcare.utils.PetManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HealthStatisticsActivity extends AppCompatActivity {

    private ActivityHealthStatisticsBinding binding;
    private CanNangDao canNangDao;
    private NhatKyDao nhatKyDao;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        canNangDao = AppDatabase.getInstance(this).canNangDao();
        nhatKyDao  = AppDatabase.getInstance(this).nhatKyDao();
        petId      = PetManager.getInstance(this).getCurrentPetId();

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnWeightDetail.setOnClickListener(v -> {
            Intent i = new Intent(this, WeightActivity.class);
            i.putExtra("pet_id", petId);
            startActivity(i);
        });

        binding.btnActivityDetail.setOnClickListener(v ->
                startActivity(new Intent(this, DiaryActivity.class)));

        loadWeightChart();
        loadActivityChart();
    }

    // ── BIỂU ĐỒ CÂN NẶNG ────────────────────────────────────────────────────

    private void loadWeightChart() {
        if (petId == null) { setupEmptyLineChart(); return; }

        new Thread(() -> {
            List<CanNang> records = canNangDao.getAllByPet(petId);
            runOnUiThread(() -> {
                if (records.isEmpty()) { setupEmptyLineChart(); return; }

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Collections.sort(records, (c1, c2) -> {
                    try {
                        Date date1 = sdf.parse(c1.ngay);
                        Date date2 = sdf.parse(c2.ngay);
                        if (date1 != null && date2 != null) {
                            return date1.compareTo(date2); // Sắp xếp tăng dần (Cũ -> Mới)
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                });

                // Lấy tối đa 12 bản ghi mới nhất
                int start = Math.max(0, records.size() - 12);
                List<CanNang> recent = records.subList(start, records.size());

                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                for (int i = 0; i < recent.size(); i++) {
                    entries.add(new Entry(i, recent.get(i).canNang));
                    String ngay = recent.get(i).ngay;
                    labels.add(ngay != null && ngay.length() >= 5
                            ? ngay.substring(0, 5) : ngay);
                }

                // Cập nhật text thay đổi cân nặng
                if (recent.size() >= 2) {
                    float first = recent.get(0).canNang;
                    float last  = recent.get(recent.size() - 1).canNang;
                    float diff  = last - first;
                    String sign = diff >= 0 ? "+" : "";
                    binding.tvWeightChange.setText(sign + diff + " kg");
                    binding.tvWeightChange.setTextColor(
                            diff >= 0 ? 0xFF4CAF50 : 0xFFF44336); // xanh tăng, đỏ giảm
                    binding.tvWeightDate.setText(
                            recent.get(0).ngay + " → " + recent.get(recent.size() - 1).ngay);
                } else {
                    binding.tvWeightChange.setText(recent.get(0).canNang + " kg");
                    binding.tvWeightDate.setText(recent.get(0).ngay);
                }

                renderLineChart(entries, labels);
            });
        }).start();
    }

    private void renderLineChart(List<Entry> entries, List<String> labels) {
        LineChart chart = binding.lineChartWeight;

        LineDataSet dataSet = new LineDataSet(entries, "kg");
        dataSet.setColor(0xFF4FC3F7);
        dataSet.setCircleColor(0xFF4FC3F7);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(9f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Fill gradient nhẹ bên dưới đường
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(0xFF4FC3F7);
        dataSet.setFillAlpha(30);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels.toArray(new String[0])));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setTextSize(9f);
        chart.getXAxis().setLabelRotationAngle(-30f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setTextSize(9f);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.animateX(1000);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private void setupEmptyLineChart() {
        LineChart chart = binding.lineChartWeight;
        chart.clear();
        chart.setNoDataText("Chưa có dữ liệu cân nặng");
        chart.setNoDataTextColor(0xFF888888);
        chart.invalidate();
        binding.tvWeightChange.setText("--");
        binding.tvWeightDate.setText("Chưa có dữ liệu");
    }

    // ── BIỂU ĐỒ HOẠT ĐỘNG ────────────────────────────────────────────────────

    private void loadActivityChart() {
        if (petId == null) { setupEmptyBarChart(); return; }

        new Thread(() -> {
            List<NhatKy> allDiary = nhatKyDao.getAllByPet(petId);
            runOnUiThread(() -> {
                if (allDiary.isEmpty()) { setupEmptyBarChart(); return; }

                // Đếm số bản ghi nhật ký theo thứ trong tuần
                // NhatKy.ngay format dd/MM/yyyy → parse thứ
                String[] dayLabels = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
                float[] counts = new float[7];

                for (NhatKy nk : allDiary) {
                    int dow = getDayOfWeek(nk.ngay); // 0=T2 ... 6=CN
                    if (dow >= 0) counts[dow]++;
                }

                List<BarEntry> entries = new ArrayList<>();
                for (int i = 0; i < 7; i++) {
                    entries.add(new BarEntry(i, counts[i]));
                }
                renderBarChart(entries, dayLabels);
            });
        }).start();
    }

    private void renderBarChart(List<BarEntry> entries, String[] labels) {
        BarChart chart = binding.barChartActivity;

        BarDataSet dataSet = new BarDataSet(entries, "Hoạt động");
        List<Integer> colors = new ArrayList<>();
        colors.add(0xFF80DEEA); colors.add(0xFF4FC3F7); colors.add(0xFF7986CB);
        colors.add(0xFF5C6BC0); colors.add(0xFF7E57C2); colors.add(0xFF9575CD);
        colors.add(0xFFB39DDB);
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setTextSize(10f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getAxisLeft().setGranularity(1f);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.animateY(1000);
        chart.setData(new BarData(dataSet));
        chart.invalidate();
    }

    private void setupEmptyBarChart() {
        BarChart chart = binding.barChartActivity;
        chart.clear();
        chart.setNoDataText("Chưa có dữ liệu hoạt động");
        chart.setNoDataTextColor(0xFF888888);
        chart.invalidate();
    }

    // ── HELPER: lấy thứ từ chuỗi dd/MM/yyyy ─────────────────────────────────

    private int getDayOfWeek(String ngay) {
        if (ngay == null || ngay.length() < 10) return -1;
        try {
            String[] parts = ngay.split("/");
            int day   = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int year  = Integer.parseInt(parts[2]);
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(year, month, day);
            // Calendar.DAY_OF_WEEK: 1=CN,2=T2...7=T7 → map sang 0=T2...6=CN
            int dow = cal.get(java.util.Calendar.DAY_OF_WEEK);
            return dow == java.util.Calendar.SUNDAY ? 6 : dow - 2;
        } catch (Exception e) { return -1; }
    }
}