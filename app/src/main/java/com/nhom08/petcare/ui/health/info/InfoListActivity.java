package com.nhom08.petcare.ui.health.info;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.ActivityInfoListBinding;
import java.util.ArrayList;
import java.util.List;

public class InfoListActivity extends AppCompatActivity {

    private ActivityInfoListBinding binding;

    public static final String TYPE_DISEASE   = "disease";
    public static final String TYPE_NUTRITION = "nutrition";
    public static final String TYPE_VET       = "vet";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        String type = getIntent().getStringExtra("type");
        setupByType(type);
    }

    private void setupByType(String type) {
        List<InfoAdapter.InfoItem> list = new ArrayList<>();

        if (TYPE_DISEASE.equals(type)) {
            binding.tvTitle.setText("Tra cứu bệnh lý");
            binding.etSearch.setHint("Tìm bệnh theo triệu chứng...");

            list.add(new InfoAdapter.InfoItem(
                    "Viêm đường ruột", "Nôn, tiêu chảy, bỏ ăn",
                    "Phổ biến", "#4CAF50"));
            list.add(new InfoAdapter.InfoItem(
                    "Ve rận", "Ngứa, rụng lông, da đỏ",
                    "Ngoài da", "#FF9800"));
            list.add(new InfoAdapter.InfoItem(
                    "Ho cũi chó", "Ho khan, hắng kéo dài",
                    "Hô hấp", "#4FC3F7"));
            list.add(new InfoAdapter.InfoItem(
                    "Sốt virus", "Sốt cao, mệt mỏi",
                    "Nguy hiểm", "#F44336"));

        } else if (TYPE_NUTRITION.equals(type)) {
            binding.tvTitle.setText("Tra cứu dinh dưỡng");
            binding.etSearch.setHint("Tìm theo độ tuổi thú cưng...");

            list.add(new InfoAdapter.InfoItem(
                    "Thức ăn cho chó con",
                    "Giàu đạm, hỗ trợ phát triển xương",
                    "Dưới 1 tuổi", "#4FC3F7"));
            list.add(new InfoAdapter.InfoItem(
                    "Thức ăn cho mèo trưởng thành",
                    "Cân bằng dinh dưỡng, kiểm soát cân",
                    "1-7 tuổi", "#4CAF50"));
            list.add(new InfoAdapter.InfoItem(
                    "Chế độ ăn giảm cân",
                    "Hỗ trợ kiểm soát béo phì",
                    "Kiểm soát cân nặng", "#FF9800"));
            list.add(new InfoAdapter.InfoItem(
                    "Dinh dưỡng cho thú cưng bệnh",
                    "Dành cho thú cưng suy nhược",
                    "Đặc biệt", "#F44336"));

        } else if (TYPE_VET.equals(type)) {
            binding.tvTitle.setText("Liên hệ thú y");
            binding.etSearch.setHint("Tìm phòng khám gần bạn...");

            list.add(new InfoAdapter.InfoItem(
                    "PetHealth Veterinary Hospital",
                    "240 Âu Cơ, Quảng An, Tây Hồ, Hà Nội",
                    "07:30 - 20:30", "#4CAF50"));
            list.add(new InfoAdapter.InfoItem(
                    "Happy Pet Clinic",
                    "47 Quảng Khánh, Tây Hồ, Hà Nội",
                    "08:00 - 18:00", "#FF9800"));
            list.add(new InfoAdapter.InfoItem(
                    "Animal Care - Thụy Khuê",
                    "20 ngõ 424 Thụy Khuê, Tây Hồ, Hà Nội",
                    "07:30 - 20:30", "#4CAF50"));
        }

        InfoAdapter adapter = new InfoAdapter(list, item -> {
            Intent intent = new Intent(this, InfoDetailActivity.class);
            intent.putExtra("type", type);
            intent.putExtra("name", item.name);
            intent.putExtra("desc", item.desc);
            intent.putExtra("tag", item.tag);
            startActivity(intent);
        });

        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);
    }
}