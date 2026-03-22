package com.nhom08.petcare.ui.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.FragmentShopBinding;
import java.util.ArrayList;
import java.util.List;

public class ShopFragment extends Fragment {

    private FragmentShopBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);

        // Nút giỏ hàng
        binding.btnCart.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CartActivity.class)));

        // Data mẫu sản phẩm
        List<ProductAdapter.ProductItem> products = new ArrayList<>();
        products.add(new ProductAdapter.ProductItem(
                "Thức ăn hộp cho chó Jazzy 400g", "320", "120,000đ"));
        products.add(new ProductAdapter.ProductItem(
                "Thức ăn ướt cho chó Butcher's Grain 400g", "245", "150,000đ"));
        products.add(new ProductAdapter.ProductItem(
                "Thức ăn hạt cho chó Jazzy 2.5kg", "180", "450,000đ"));
        products.add(new ProductAdapter.ProductItem(
                "Bánh thưởng cho chó 200g", "95", "65,000đ"));

        ProductAdapter adapter = new ProductAdapter(products, item -> {
            // TODO: thêm vào giỏ hàng
        });

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvProducts.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}