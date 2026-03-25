package com.nhom08.petcare.ui.health.info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.R; // Đừng quên import R
import com.nhom08.petcare.databinding.ActivityInfoDetailBinding;

public class InfoDetailActivity extends AppCompatActivity {

    private ActivityInfoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Nhận data từ InfoListActivity
        String type   = getIntent().getStringExtra("type");
        String name   = getIntent().getStringExtra("name");
        String field1 = getIntent().getStringExtra("field1");
        String field2 = getIntent().getStringExtra("field2");
        String field3 = getIntent().getStringExtra("field3");
        String field4 = getIntent().getStringExtra("field4");

        binding.tvName.setText(name != null ? name : "");

        if (InfoListActivity.TYPE_DISEASE.equals(type)) {
            setupDisease(name, field1, field2, field3, field4);

        } else if (InfoListActivity.TYPE_NUTRITION.equals(type)) {
            setupNutrition(name, field1, field2, field3, field4);

        } else if (InfoListActivity.TYPE_VET.equals(type)) {
            setupVet(name, field1, field2, field3, field4);
        }
    }

    // ----------------------------------------------------------------
    // Hàm Helper để set icon và nội dung cho từng khối dễ dàng
    // ----------------------------------------------------------------
    private void setupSection(LinearLayout layout, ImageView iv, TextView tvTitle, TextView tvContent,
                              int iconResId, String title, String content, boolean isBullet) {
        if (content == null || content.trim().isEmpty()) {
            layout.setVisibility(View.GONE); // Ẩn nếu không có dữ liệu
            return;
        }

        layout.setVisibility(View.VISIBLE);
        iv.setImageResource(iconResId);
        tvTitle.setText(title);

        if (isBullet) {
            tvContent.setText(formatBullets(content));
        } else {
            tvContent.setText(content);
        }
    }

    // ----------------------------------------------------------------
    // Bệnh lý
    // ----------------------------------------------------------------
    private void setupDisease(String name, String trieuChung, String nguyenNhan, String huongChamSoc, String mucDo) {
        binding.tvTitle.setText("Chi tiết bệnh");

        // Set các khối (Đổi tên R.drawable.ic_... thành tên file thật của bạn)
        setupSection(binding.layoutSection1, binding.ivIcon1, binding.tvTitle1, binding.tvContent1,
                R.drawable.ic_warning, "Mức độ nguy hiểm", mucDo, false);

        setupSection(binding.layoutSection2, binding.ivIcon2, binding.tvTitle2, binding.tvContent2,
                R.drawable.ic_medical, "Triệu chứng", trieuChung, true);

        setupSection(binding.layoutSection3, binding.ivIcon3, binding.tvTitle3, binding.tvContent3,
                R.drawable.ic_danger, "Nguyên nhân", nguyenNhan, true);

        setupSection(binding.layoutSection4, binding.ivIcon4, binding.tvTitle4, binding.tvContent4,
                R.drawable.ic_pill, "Hướng chăm sóc tại nhà", huongChamSoc, true);

        // Khối 5: Lưu ý
        String luuYText = "Nếu triệu chứng kéo dài hoặc nặng hơn, hãy đưa thú cưng đến phòng khám thú y ngay.";
        setupSection(binding.layoutSection5, binding.ivIcon5, binding.tvTitle5, binding.tvContent5,
                R.drawable.ic_note, "Lưu ý", luuYText, true);

        // Nút hành động
        binding.btnAction.setVisibility(View.VISIBLE);
        binding.btnAction.setText("Liên hệ thú y ngay");
        binding.btnAction.setOnClickListener(v -> {
            // Logic chuyển trang
        });
    }

    // ----------------------------------------------------------------
    // Dinh dưỡng
    // ----------------------------------------------------------------
    private void setupNutrition(String name, String nenAn, String khongNenAn, String luuY, String doTuoi) {
        binding.tvTitle.setText("Chi tiết dinh dưỡng");

        setupSection(binding.layoutSection1, binding.ivIcon1, binding.tvTitle1, binding.tvContent1,
                R.drawable.ic_walk, "Đối tượng", doTuoi, false);

        setupSection(binding.layoutSection2, binding.ivIcon2, binding.tvTitle2, binding.tvContent2,
                R.drawable.ic_check, "Thực phẩm nên ăn", nenAn, true);

        setupSection(binding.layoutSection3, binding.ivIcon3, binding.tvTitle3, binding.tvContent3,
                R.drawable.ic_cancel, "Thực phẩm không nên ăn", khongNenAn, true);

        setupSection(binding.layoutSection4, binding.ivIcon4, binding.tvTitle4, binding.tvContent4,
                R.drawable.ic_clipboard, "Lưu ý khẩu phần", luuY, true);

        // Ẩn khối 5 vì dinh dưỡng chỉ có 4 mục
        binding.layoutSection5.setVisibility(View.GONE);

        // Nút hành động
        binding.btnAction.setVisibility(View.VISIBLE);
        binding.btnAction.setText("Xem sản phẩm tại Shop");
        binding.btnAction.setOnClickListener(v -> {
            // Logic chuyển trang
        });
    }

    // ----------------------------------------------------------------
    // Phòng khám
    // ----------------------------------------------------------------
    private void setupVet(String name, String diaChi, String soDienThoai, String gioMoCua, String danhGia) {
        binding.tvTitle.setText("Chi tiết phòng khám");

        setupSection(binding.layoutSection1, binding.ivIcon1, binding.tvTitle1, binding.tvContent1,
                R.drawable.ic_location, "Địa chỉ", diaChi, false);

        setupSection(binding.layoutSection2, binding.ivIcon2, binding.tvTitle2, binding.tvContent2,
                R.drawable.ic_clock, "Giờ mở cửa", gioMoCua, false);

        setupSection(binding.layoutSection3, binding.ivIcon3, binding.tvTitle3, binding.tvContent3,
                R.drawable.ic_phone, "Số điện thoại", soDienThoai, false);

        setupSection(binding.layoutSection4, binding.ivIcon4, binding.tvTitle4, binding.tvContent4,
                R.drawable.ic_star, "Đánh giá", danhGia, false);

        // Khối 5: Dịch vụ chính
        String dichVu = "Khám tổng quát, Tiêm phòng, Tẩy giun định kỳ, Phẫu thuật, Xét nghiệm máu, Siêu âm, Nội trú điều trị, Grooming cơ bản";
        setupSection(binding.layoutSection5, binding.ivIcon5, binding.tvTitle5, binding.tvContent5,
                R.drawable.ic_hospital, "Dịch vụ chính", dichVu, true);

        // Nút hành động
        binding.btnAction.setVisibility(View.VISIBLE);
        binding.btnAction.setText("Gọi ngay: " + safe(soDienThoai));
        binding.btnAction.setOnClickListener(v -> {
            if (soDienThoai != null && !soDienThoai.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + soDienThoai)));
            }
        });
    }

    // ----------------------------------------------------------------
    // Helper: format chuỗi thành dạng bullet list (trả về String)
    // ----------------------------------------------------------------
    private String formatBullets(String text) {
        if (text == null || text.trim().isEmpty()) return "• Không có thông tin";

        StringBuilder sb = new StringBuilder();
        String[] parts = text.split("[,;،]");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                sb.append("• ").append(trimmed).append("\n");
            }
        }
        return sb.toString().trim(); // Cắt bỏ dấu \n thừa ở cuối
    }

    private String safe(String s) {
        return s != null ? s : "Không có thông tin";
    }
}