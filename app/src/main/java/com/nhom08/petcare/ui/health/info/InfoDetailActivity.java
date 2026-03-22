package com.nhom08.petcare.ui.health.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityInfoDetailBinding;

public class InfoDetailActivity extends AppCompatActivity {

    private ActivityInfoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        String type = getIntent().getStringExtra("type");
        String name = getIntent().getStringExtra("name");
        String tag  = getIntent().getStringExtra("tag");

        binding.tvName.setText(name);

        if (InfoListActivity.TYPE_DISEASE.equals(type)) {
            binding.tvTitle.setText("Chi tiết bệnh");
            binding.tvContent.setText(getDiseaseContent(name));
            binding.btnAction.setVisibility(View.VISIBLE);
            binding.btnAction.setText("Liên hệ thú y ngay");
            binding.btnAction.setOnClickListener(v -> {
                Intent intent = new Intent(this, InfoListActivity.class);
                intent.putExtra("type", InfoListActivity.TYPE_VET);
                startActivity(intent);
            });

        } else if (InfoListActivity.TYPE_NUTRITION.equals(type)) {
            binding.tvTitle.setText("Chi tiết dinh dưỡng");
            binding.tvContent.setText(getNutritionContent(name));

            // Hiện nút → sang Shop
            binding.btnAction.setVisibility(View.VISIBLE);
            binding.btnAction.setText("Xem sản phẩm tại Shop");
            binding.btnAction.setOnClickListener(v -> {
                Intent intent = new Intent(this,
                        com.nhom08.petcare.ui.main.MainActivity.class);
                intent.putExtra("nav_to", "shop");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        } else if (InfoListActivity.TYPE_VET.equals(type)) {
            binding.tvTitle.setText("Chi tiết phòng khám");
            binding.tvContent.setText(getVetContent(name));
            binding.btnAction.setVisibility(View.VISIBLE);
            binding.btnAction.setText("Liên hệ ngay");
            // Gọi điện — số điện thoại lấy theo tên phòng khám
            binding.btnAction.setOnClickListener(v -> {
                String phone = getPhoneByName(name);
                startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + phone)));
            });
        }
    }

    private String getDiseaseContent(String name) {
        return "Mô tả\n" +
                name + " là tình trạng thường gặp ở thú cưng.\n\n" +
                "Triệu chứng\n" +
                "• Triệu chứng 1\n• Triệu chứng 2\n• Triệu chứng 3\n\n" +
                "Nguyên nhân\n" +
                "• Nguyên nhân 1\n• Nguyên nhân 2\n\n" +
                "Cách xử lý tại nhà\n" +
                "• Bước 1\n• Bước 2\n• Bước 3\n\n" +
                "Cần đến thú y khi\n" +
                "• Triệu chứng nặng hơn\n• Mất nước nghiêm trọng";
    }

    private String getNutritionContent(String name) {
        return "Mô tả\n" +
                name + " được thiết kế đặc biệt cho thú cưng.\n\n" +
                "Phù hợp với\n" +
                "• Độ tuổi phù hợp\n• Giống thú cưng\n\n" +
                "Lợi ích\n" +
                "• Lợi ích 1\n• Lợi ích 2\n• Lợi ích 3\n\n" +
                "Gợi ý thực phẩm\n" +
                "• Thực phẩm 1\n• Thực phẩm 2\n\n" +
                "Lưu ý\n" +
                "• Lưu ý 1\n• Lưu ý 2";
    }

    private String getVetContent(String name) {
        return "Địa chỉ\n" + getAddressByName(name) + "\n\n" +
                "Giờ mở cửa\n" + getHoursByName(name) + "\n\n" +
                "Số điện thoại\n" + getPhoneByName(name) + "\n\n" +
                "Đánh giá\n" + getRatingByName(name) + "\n\n" +
                "Dịch vụ chính\n" +
                "• Khám tổng quát\n" +
                "• Tiêm phòng\n" +
                "• Phẫu thuật\n" +
                "• Xét nghiệm máu\n" +
                "• Siêu âm\n" +
                "• Nội trú điều trị\n" +
                "• Grooming cơ bản";
    }

    private String getPhoneByName(String name) {
        if (name.contains("PetHealth")) return "0762229882";
        if (name.contains("Happy"))    return "0912345678";
        return "0987654321";
    }

    private String getAddressByName(String name) {
        if (name.contains("PetHealth")) return "240 Âu Cơ, Quảng An, Tây Hồ, Hà Nội";
        if (name.contains("Happy"))    return "47 Quảng Khánh, Tây Hồ, Hà Nội";
        return "20 ngõ 424 Thụy Khuê, Tây Hồ, Hà Nội";
    }

    private String getHoursByName(String name) {
        if (name.contains("Happy")) return "08:00 - 18:00";
        return "07:30 - 20:30";
    }

    private String getRatingByName(String name) {
        if (name.contains("PetHealth")) return "4.4 (123 đánh giá)";
        if (name.contains("Happy"))    return "4.2 (78 đánh giá)";
        return "4.3 (109 đánh giá)";
    }
}