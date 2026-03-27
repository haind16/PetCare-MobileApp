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

    private static final String DB_URL = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";
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

    private void setupByType(String type) {
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

    private void loadDiseases() {
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("benh_ly");
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
                    String anhUrl        = getString(child, "anhUrl");
                    String anhBiaUrl     = getString(child, "anhBiaUrl"); // Lấy ảnh bìa

                    if (tenBenh == null) continue;
                    String tagColor = colorForLevel(mucDoNguyHiem);

                    InfoAdapter.InfoItem item = new InfoAdapter.InfoItem(
                            tenBenh,
                            trieuChung != null ? trieuChung : "",
                            mucDoNguyHiem != null ? mucDoNguyHiem : "",
                            tagColor,
                            anhUrl != null ? anhUrl : "",
                            anhBiaUrl != null ? anhBiaUrl : "" // Truyền vào model
                    );
                    item.field1 = trieuChung; item.field2 = nguyenNhan; item.field3 = huongChamSoc; item.field4 = mucDoNguyHiem;
                    list.add(item);
                }
                adapter.setData(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadNutrition() {
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("dinh_duong");
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
                    String anhUrl              = getString(child, "anhUrl");
                    String anhBiaUrl     = getString(child, "anhBiaUrl"); // Lấy ảnh bìa

                    if (nhomThucPham == null) continue;

                    InfoAdapter.InfoItem item = new InfoAdapter.InfoItem(
                            nhomThucPham,
                            thucPhamNenAn != null ? thucPhamNenAn : "",
                            doTuoi != null ? doTuoi : "",
                            "#4FC3F7",
                            anhUrl != null ? anhUrl : "",
                            anhBiaUrl != null ? anhBiaUrl : "" // Truyền vào model
                    );
                    item.field1 = thucPhamNenAn; item.field2 = thucPhamKhongNenAn; item.field3 = luuYKhauPhan; item.field4 = doTuoi;
                    list.add(item);
                }
                adapter.setData(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadVets() {
        DatabaseReference ref = FirebaseDatabase.getInstance(DB_URL).getReference("phong_kham");
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
                    String anhUrl       = getString(child, "anhUrl");
                    String anhBiaUrl     = getString(child, "anhBiaUrl"); // Lấy ảnh bìa

                    if (ten == null) continue;
                    String danhGiaStr = danhGiaD != null ? "⭐ " + danhGiaD : "⭐ N/A";

                    InfoAdapter.InfoItem item = new InfoAdapter.InfoItem(
                            ten,
                            diaChi != null ? diaChi : "",
                            gioMoCua != null ? gioMoCua : "",
                            "#4CAF50",
                            anhUrl != null ? anhUrl : "",
                            anhBiaUrl != null ? anhBiaUrl : "" // Truyền vào model
                    );
                    item.field1 = diaChi; item.field2 = soDienThoai; item.field3 = gioMoCua; item.field4 = danhGiaStr;
                    list.add(item);
                }
                adapter.setData(list);
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { adapter.filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

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

        intent.putExtra("anhUrl", item.anhUrl);
        intent.putExtra("anhBiaUrl", item.anhBiaUrl); // ĐẨY LINK ẢNH BÌA QUA CHI TIẾT

        startActivity(intent);
    }

    private String getString(DataSnapshot snap, String key) {
        return snap.child(key).getValue(String.class);
    }

    private String colorForLevel(String level) {
        if (level == null) return "#888888";
        switch (level) {
            case "Nguy hiểm cao": case "Cực kỳ nguy hiểm": case "Nguy hiểm": return "#F44336";
            case "Hô hấp": return "#4FC3F7";
            case "Ngoài da": return "#FF9800";
            case "Phổ biến": return "#4CAF50";
            default: return "#888888";
        }
    }
}