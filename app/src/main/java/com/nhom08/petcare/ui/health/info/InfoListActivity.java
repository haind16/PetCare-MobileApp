package com.nhom08.petcare.ui.health.info;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.databinding.ActivityInfoListBinding;
import java.util.ArrayList;
import java.util.List;

public class InfoListActivity extends AppCompatActivity {

    private static final String DB_URL =
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    public static final String TYPE_DISEASE   = "disease";
    public static final String TYPE_NUTRITION = "nutrition";
    public static final String TYPE_VET       = "vet";

    private ActivityInfoListBinding binding;
    private InfoAdapter             adapter;
    private String                  currentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        currentType = getIntent().getStringExtra("type");
        setupByType(currentType);
        setupSearch();
    }

    // ----------------------------------------------------------------
    // Cài đặt tiêu đề + hint + load data theo loại
    // ----------------------------------------------------------------
    private void setupByType(String type) {
        // Khởi tạo adapter rỗng trước (hiển thị loading ngay)
        List<InfoAdapter.InfoItem> emptyList = new ArrayList<>();
        adapter = new InfoAdapter(emptyList, item -> openDetail(item, type));
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(adapter);

        if (TYPE_DISEASE.equals(type)) {
            binding.tvTitle.setText("Tra cứu bệnh lý");
            binding.etSearch.setHint("Tìm bệnh theo triệu chứng...");
            loadDiseases();

        } else if (TYPE_NUTRITION.equals(type)) {
            binding.tvTitle.setText("Tra cứu dinh dưỡng");
            binding.etSearch.setHint("Tìm theo độ tuổi thú cưng...");
            loadNutrition();

        } else if (TYPE_VET.equals(type)) {
            binding.tvTitle.setText("Liên hệ thú y");
            binding.etSearch.setHint("Tìm phòng khám gần bạn...");
            loadVets();
        }
    }

    // ----------------------------------------------------------------
    // Load bệnh lý từ Firebase
    // benh_ly: tenBenh, trieuChung, nguyenNhan, huongChamSoc, mucDoNguyHiem
    // ----------------------------------------------------------------
    private void loadDiseases() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance(DB_URL).getReference("benh_ly");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<InfoAdapter.InfoItem> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String tenBenh       = getString(child, "tenBenh");
                    String trieuChung    = getString(child, "trieuChung");
                    String nguyenNhan    = getString(child, "nguyenNhan");
                    String huongChamSoc  = getString(child, "huongChamSoc");
                    String mucDoNguyHiem = getString(child, "mucDoNguyHiem");

                    if (tenBenh == null) continue;

                    String tagColor = colorForLevel(mucDoNguyHiem);

                    InfoAdapter.InfoItem item = new InfoAdapter.InfoItem(
                            tenBenh,
                            trieuChung  != null ? trieuChung  : "",
                            mucDoNguyHiem != null ? mucDoNguyHiem : "",
                            tagColor
                    );
                    // Extra fields → dùng trong detail page
                    item.field1 = trieuChung;    // Triệu chứng
                    item.field2 = nguyenNhan;    // Nguyên nhân
                    item.field3 = huongChamSoc;  // Hướng chăm sóc
                    item.field4 = mucDoNguyHiem; // Mức độ nguy hiểm

                    list.add(item);
                }
                adapter.setData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InfoListActivity.this,
                        "Không tải được dữ liệu bệnh lý!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Load dinh dưỡng từ Firebase
    // dinh_duong: doTuoi, nhomThucPham, thucPhamNenAn, thucPhamKhongNenAn, luuYKhauPhan
    // ----------------------------------------------------------------
    private void loadNutrition() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance(DB_URL).getReference("dinh_duong");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<InfoAdapter.InfoItem> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String doTuoi              = getString(child, "doTuoi");
                    String nhomThucPham        = getString(child, "nhomThucPham");
                    String thucPhamNenAn       = getString(child, "thucPhamNenAn");
                    String thucPhamKhongNenAn  = getString(child, "thucPhamKhongNenAn");
                    String luuYKhauPhan        = getString(child, "luuYKhauPhan");

                    if (nhomThucPham == null) continue;

                    InfoAdapter.InfoItem item = new InfoAdapter.InfoItem(
                            nhomThucPham,
                            thucPhamNenAn != null ? thucPhamNenAn : "",
                            doTuoi        != null ? doTuoi        : "",
                            "#4FC3F7"
                    );
                    item.field1 = thucPhamNenAn;
                    item.field2 = thucPhamKhongNenAn;
                    item.field3 = luuYKhauPhan;
                    item.field4 = doTuoi;

                    list.add(item);
                }
                adapter.setData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InfoListActivity.this,
                        "Không tải được dữ liệu dinh dưỡng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Load phòng khám từ Firebase
    // phong_kham: ten, diaChi, soDienThoai, gioMoCua, danhGia
    // ----------------------------------------------------------------
    private void loadVets() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance(DB_URL).getReference("phong_kham");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<InfoAdapter.InfoItem> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String ten          = getString(child, "ten");
                    String diaChi       = getString(child, "diaChi");
                    String soDienThoai  = getString(child, "soDienThoai");
                    String gioMoCua     = getString(child, "gioMoCua");
                    Double danhGiaD     = child.child("danhGia").getValue(Double.class);

                    if (ten == null) continue;

                    String danhGiaStr = danhGiaD != null
                            ? "⭐ " + danhGiaD : "⭐ N/A";

                    InfoAdapter.InfoItem item = new InfoAdapter.InfoItem(
                            ten,
                            diaChi    != null ? diaChi    : "",
                            gioMoCua  != null ? gioMoCua  : "",
                            "#4CAF50"
                    );
                    item.field1 = diaChi;
                    item.field2 = soDienThoai;
                    item.field3 = gioMoCua;
                    item.field4 = danhGiaStr;

                    list.add(item);
                }
                adapter.setData(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InfoListActivity.this,
                        "Không tải được danh sách phòng khám!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Tìm kiếm realtime
    // ----------------------------------------------------------------
    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // ----------------------------------------------------------------
    // Mở màn hình detail — truyền toàn bộ data qua Intent
    // ----------------------------------------------------------------
    private void openDetail(InfoAdapter.InfoItem item, String type) {
        Intent intent = new Intent(this, InfoDetailActivity.class);
        intent.putExtra("type",   type);
        intent.putExtra("name",   item.name);
        intent.putExtra("desc",   item.desc);
        intent.putExtra("tag",    item.tag);
        intent.putExtra("field1", item.field1);
        intent.putExtra("field2", item.field2);
        intent.putExtra("field3", item.field3);
        intent.putExtra("field4", item.field4);
        startActivity(intent);
    }

    // ----------------------------------------------------------------
    // Helper: đọc String từ DataSnapshot, trả null nếu không có
    // ----------------------------------------------------------------
    private String getString(DataSnapshot snap, String key) {
        return snap.child(key).getValue(String.class);
    }

    // ----------------------------------------------------------------
    // Helper: chọn màu tag theo mức độ nguy hiểm
    // ----------------------------------------------------------------
    private String colorForLevel(String level) {
        if (level == null) return "#888888";
        switch (level) {
            case "Nguy hiểm cao":
            case "Cực kỳ nguy hiểm":
            case "Nguy hiểm":       return "#F44336"; // đỏ
            case "Hô hấp":          return "#4FC3F7"; // xanh dương
            case "Ngoài da":        return "#FF9800"; // cam
            case "Phổ biến":        return "#4CAF50"; // xanh lá
            default:                return "#888888";
        }
    }
}