package com.nhom08.petcare.ui.health.statistics;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.nhom08.petcare.databinding.ActivityHealthStatisticsBinding;
import com.nhom08.petcare.ui.pet.health.WeightActivity;
import com.nhom08.petcare.ui.health.diary.DiaryActivity;
import java.util.ArrayList;
import java.util.List;

public class HealthStatisticsActivity extends AppCompatActivity {

    private ActivityHealthStatisticsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHealthStatisticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        setupLineChart();
        setupBarChart();

        // Mũi tên cân nặng → WeightActivity đã có ở Profile
        binding.btnWeightDetail.setOnClickListener(v ->
                startActivity(new Intent(this, WeightActivity.class)));

        // Nút Chi tiết hoạt động → DiaryActivity đã có ở Health
        binding.btnActivityDetail.setOnClickListener(v ->
                startActivity(new Intent(this, DiaryActivity.class)));
    }

    private void setupLineChart() {
        LineChart chart = binding.lineChartWeight;

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 13f));
        entries.add(new Entry(1, 13.5f));
        entries.add(new Entry(2, 14.5f));
        entries.add(new Entry(3, 15f));
        entries.add(new Entry(4, 16f));
        entries.add(new Entry(5, 17f));

        LineDataSet dataSet = new LineDataSet(entries, "kg");
        dataSet.setColor(0xFF4FC3F7);
        dataSet.setCircleColor(0xFF4FC3F7);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(9f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        String[] months = {"T1", "T2", "T3", "T4", "T5", "T6"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setTextSize(9f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setScaleEnabled(false);
        chart.animateX(1000);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private void setupBarChart() {
        BarChart chart = binding.barChartActivity;

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 3));
        entries.add(new BarEntry(1, 5));
        entries.add(new BarEntry(2, 4));
        entries.add(new BarEntry(3, 7));
        entries.add(new BarEntry(4, 6));
        entries.add(new BarEntry(5, 4));
        entries.add(new BarEntry(6, 5));

        BarDataSet dataSet = new BarDataSet(entries, "Hoạt động");

        List<Integer> colors = new ArrayList<>();
        colors.add(0xFF80DEEA);
        colors.add(0xFF4FC3F7);
        colors.add(0xFF7986CB);
        colors.add(0xFF5C6BC0);
        colors.add(0xFF7E57C2);
        colors.add(0xFF9575CD);
        colors.add(0xFFB39DDB);
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(days));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setTextSize(10f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.setTouchEnabled(false);
        chart.setScaleEnabled(false);
        chart.animateY(1000);
        chart.setData(new BarData(dataSet));
        chart.invalidate();
    }
}