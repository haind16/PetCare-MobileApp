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
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.CanNangDao;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.databinding.FragmentHealthBinding;
import com.nhom08.petcare.ui.health.diary.DiaryActivity;
import com.nhom08.petcare.ui.health.info.InfoListActivity;
import com.nhom08.petcare.ui.health.medical.MedicalRecordActivity;
import com.nhom08.petcare.ui.health.statistics.HealthStatisticsActivity;
import com.nhom08.petcare.utils.PetManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment Sức khỏe.
 * Cung cấp cái nhìn tổng quan về sức khỏe thú cưng qua biểu đồ cân nặng gần nhất 
 * và các phím tắt truy cập vào: Cẩm nang bệnh lý, Dinh dưỡng, Kết nối thú y, Hồ sơ y tế và Nhật ký.
 */
public class HealthFragment extends Fragment {

    private FragmentHealthBinding binding;
    private CanNangDao canNangDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHealthBinding.inflate(inflater, container, false);
        canNangDao = AppDatabase.getInstance(requireContext()).canNangDao();

        // Xem thống kê chi tiết
        binding.tvViewAll.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), HealthStatisticsActivity.class)));

        // Truy cập cẩm nang bệnh lý
        binding.btnDiseaseLib.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoListActivity.class);
            intent.putExtra("type", InfoListActivity.TYPE_DISEASE);
            startActivity(intent);
        });

        // Truy cập thông tin dinh dưỡng
        binding.btnNutrition.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoListActivity.class);
            intent.putExtra("type", InfoListActivity.TYPE_NUTRITION);
            startActivity(intent);
        });

        // Liên hệ/Danh sách phòng khám thú y
        binding.btnVetContact.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InfoListActivity.class);
            intent.putExtra("type", InfoListActivity.TYPE_VET);
            startActivity(intent);
        });

        // Quản lý hồ sơ y tế (Đơn thuốc, phiếu khám)
        binding.btnMedicalRecord.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MedicalRecordActivity.class)));

        // Xem nhật ký hoạt động
        binding.btnDiary.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), DiaryActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại biểu đồ mỗi khi quay lại fragment
        loadChartData();
    }

    /**
     * Tải dữ liệu cân nặng từ local database để hiển thị lên biểu đồ tóm tắt.
     */
    private void loadChartData() {
        String petId = PetManager.getInstance(requireContext()).getCurrentPetId();
        if (petId == null || petId.isEmpty()) {
            setupEmptyChart();
            return;
        }

        new Thread(() -> {
            List<CanNang> records = canNangDao.getAllByPet(petId);

            // Sắp xếp dữ liệu theo trình tự thời gian tăng dần
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Collections.sort(records, (c1, c2) -> {
                try {
                    Date date1 = sdf.parse(c1.ngay);
                    Date date2 = sdf.parse(c2.ngay);
                    if (date1 != null && date2 != null) {
                        return date1.compareTo(date2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            });
            
            if (getActivity() == null || binding == null) return;
            
            getActivity().runOnUiThread(() -> {
                if (binding == null) return;
                if (records.isEmpty()) {
                    setupEmptyChart();
                    return;
                }
                // Lấy 6 mốc cân nặng gần nhất để hiển thị biểu đồ tóm tắt
                int start = Math.max(0, records.size() - 6);
                List<CanNang> recent = records.subList(start, records.size());

                List<Entry> entries = new ArrayList<>();
                List<String> labels = new ArrayList<>();
                for (int i = 0; i < recent.size(); i++) {
                    entries.add(new Entry(i, recent.get(i).canNang));
                    String ngay = recent.get(i).ngay;
                    labels.add(ngay != null && ngay.length() >= 5
                            ? ngay.substring(0, 5) : ngay); // Rút gọn ngày để hiển thị
                }
                renderChart(binding.lineChart, entries, labels);
            });
        }).start();
    }

    /**
     * Cấu hình và hiển thị biểu đồ đường (LineChart).
     */
    private void renderChart(LineChart chart, List<Entry> entries, List<String> labels) {
        LineDataSet dataSet = new LineDataSet(entries, "Cân nặng (kg)");
        dataSet.setColor(0xFF4FC3F7);
        dataSet.setCircleColor(0xFF4FC3F7);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        chart.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(labels.toArray(new String[0])));
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setTextSize(9f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.animateX(800);
        chart.setData(new LineData(dataSet));
        chart.invalidate();
    }

    private void setupEmptyChart() {
        if (binding == null) return;
        LineChart chart = binding.lineChart;
        chart.clear();
        chart.setNoDataText("Chưa có dữ liệu cân nặng");
        chart.setNoDataTextColor(0xFF888888);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}