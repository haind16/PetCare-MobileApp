package com.nhom08.petcare.ui.community;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nhom08.petcare.databinding.ActivityPostDetailBinding;

public class PostDetailActivity extends AppCompatActivity {

    private ActivityPostDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() { return 2; }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new LikesFragment() : new CommentsFragment();
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(
                        position == 0 ? "Thích (12)" : "Bình luận (5)")
        ).attach();
    }
}