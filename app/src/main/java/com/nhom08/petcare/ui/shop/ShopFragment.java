package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.databinding.FragmentShopBinding;
import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private static final String DB_URL =
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private FragmentShopBinding binding;
    private ProductAdapter adapter;

    private DatabaseReference  productRef;
    private ValueEventListener productListener;

    private final List<ProductAdapter.ProductItem> allProducts = new ArrayList<>();
    private final List<ProductAdapter.ProductItem> displayList = new ArrayList<>();
    private String currentCategory = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupCategoryButtons();
        setupSearch();
        loadProductsFromFirebase();

        binding.btnCart.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CartActivity.class)));

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new ProductAdapter(displayList, item -> addToCart(item));
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupCategoryButtons() {
        binding.btnCatFood.setOnClickListener(v -> filterByCategory("Thức ăn"));
        binding.btnAccessory.setOnClickListener(v -> filterByCategory("Phụ kiện"));
        binding.btnMedicine.setOnClickListener(v -> filterByCategory("Thuốc"));
    }

    private void filterByCategory(String category) {
        if (currentCategory.equals(category)) {
            currentCategory = "";
        } else {
            currentCategory = category;
        }
        if (binding != null) {
            applyFilter(binding.etSearch.getText().toString().trim());
        }
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                applyFilter(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilter(String keyword) {
        displayList.clear();
        for (ProductAdapter.ProductItem p : allProducts) {
            boolean matchCategory = currentCategory.isEmpty()
                    || currentCategory.equals(p.danhMuc);
            boolean matchKeyword = keyword.isEmpty()
                    || p.name.toLowerCase().contains(keyword.toLowerCase());
            if (matchCategory && matchKeyword) {
                displayList.add(p);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadProductsFromFirebase() {
        productRef = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference("products");

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;

                allProducts.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String id      = child.getKey();
                    String ten     = child.child("ten").getValue(String.class);
                    String danhMuc = child.child("danhMuc").getValue(String.class);
                    String moTa    = child.child("moTa").getValue(String.class);
                    Long   giaLong = child.child("gia").getValue(Long.class);
                    Long   daBanL  = child.child("daBan").getValue(Long.class);
                    String anhUrl  = child.child("anhUrl").getValue(String.class); // Thêm lấy URL ảnh

                    if (ten == null) continue;

                    long gia   = giaLong != null ? giaLong : 0;
                    long daBan = daBanL  != null ? daBanL  : 0;

                    allProducts.add(new ProductAdapter.ProductItem(
                            id,
                            ten,
                            danhMuc != null ? danhMuc : "",
                            moTa    != null ? moTa    : "",
                            daBan,
                            gia,
                            anhUrl  != null ? anhUrl  : "" // Truyền url ảnh vào constructor
                    ));
                }
                applyFilter(binding.etSearch.getText().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (binding == null) return;
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            "Không tải được sản phẩm!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        productRef.addValueEventListener(productListener);
    }

    private void addToCart(ProductAdapter.ProductItem item) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(getActivity(), "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference cartRef = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference("carts")
                .child(userId)
                .child(item.id);

        cartRef.child("soLuong").get().addOnCompleteListener(task -> {
            int currentQty = 0;
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                Long qty = task.getResult().getValue(Long.class);
                if (qty != null) currentQty = qty.intValue();
            }

            java.util.Map<String, Object> cartItem = new java.util.HashMap<>();
            cartItem.put("ten",     item.name);
            cartItem.put("gia",     item.gia);
            cartItem.put("danhMuc", item.danhMuc);
            cartItem.put("anhUrl",  item.anhUrl); // Lưu cả ảnh vào giỏ hàng để sau này dùng
            cartItem.put("soLuong", currentQty + 1);

            cartRef.setValue(cartItem).addOnSuccessListener(unused -> {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(),
                            "Đã thêm \"" + item.name + "\" vào giỏ!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (productRef != null && productListener != null) {
            productRef.removeEventListener(productListener);
        }
        binding = null;
    }
}