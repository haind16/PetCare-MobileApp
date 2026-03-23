package com.nhom08.petcare.ui.health;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.nhom08.petcare.databinding.FragmentHealthBinding;
import com.nhom08.petcare.ui.health.diary.DiaryActivity;
import com.nhom08.petcare.ui.health.info.InfoListActivity;
import com.nhom08.petcare.ui.health.medical.MedicalRecordActivity;
import com.nhom08.petcare.ui.health.statistics.HealthStatisticsActivity;

import java.util.ArrayList;
import java.util.List;

public class HealthFragment extends Fragment {

    private FragmentHealthBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(inflater, container, false);

        setupChart();

        // Kết nối các nút
        binding.tvViewAll.setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        HealthStatisticsActivity.class)));

        binding.btnDiseaseLib.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoListActivity.class);
            intent.putExtra("type", InfoListActivity.TYPE_DISEASE);
            startActivity(intent);
        });

        binding.btnNutrition.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoListActivity.class);
            intent.putExtra("type", InfoListActivity.TYPE_NUTRITION);
            startActivity(intent);
        });

        binding.btnVetContact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoListActivity.class);
            intent.putExtra("type", InfoListActivity.TYPE_VET);
            startActivity(intent);
        });

        binding.btnMedicalRecord.setOnClickListener(v ->
                startActivity(new Intent(getActivity(),
                        MedicalRecordActivity.class)));

        binding.btnDiary.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DiaryActivity.class)));

        return binding.getRoot();
    }

    private void setupChart() {
        LineChart chart = binding.lineChart;

        // Data mẫu — sau này lấy từ Room/Firestore
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 4.2f));
        entries.add(new Entry(1, 4.3f));
        entries.add(new Entry(2, 4.1f));
        entries.add(new Entry(3, 4.4f));
        entries.add(new Entry(4, 4.5f));
        entries.add(new Entry(5, 4.3f));

        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
        dataSet.setColor(0xFF4FC3F7);
        dataSet.setCircleColor(0xFF4FC3F7);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Nhãn trục X
        String[] months = {"T1", "T2", "T3", "T4", "T5", "T6"};
        chart.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(months));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.animateX(1000);

        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    // Hàm này gọi khi có data mới từ backend
    public void updateChart(List<Float> weights, List<String> labels) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < weights.size(); i++) {
            entries.add(new Entry(i, weights.get(i)));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
        dataSet.setColor(0xFF4FC3F7);
        dataSet.setCircleColor(0xFF4FC3F7);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        binding.lineChart.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(labels.toArray(new String[0])));
        binding.lineChart.setData(new LineData(dataSet));
        binding.lineChart.animateX(500);
        binding.lineChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}