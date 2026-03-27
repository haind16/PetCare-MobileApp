package com.nhom08.petcare.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityMainBinding;
import com.nhom08.petcare.ui.community.CommunityFragment;
import com.nhom08.petcare.ui.health.HealthFragment;
import com.nhom08.petcare.ui.profile.ProfileFragment;
import com.nhom08.petcare.ui.shop.ShopFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setSelectedItemId(R.id.nav_home);
        loadFragment(new HomeFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            } else if (id == R.id.nav_shop) {
                loadFragment(new ShopFragment());
            } else if (id == R.id.nav_chat) {
                loadFragment(new CommunityFragment());
            } else if (id == R.id.nav_health) {
                loadFragment(new HealthFragment());
            }
            return true;
        });

        // Kiểm tra nếu được mở từ thanh toán
        handleIntent(getIntent());
    }

    // Nhận intent khi activity đang chạy
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        String navTo = intent.getStringExtra("nav_to");
        if ("shop".equals(navTo)) {
            binding.bottomNav.setSelectedItemId(R.id.nav_shop);
            loadFragment(new ShopFragment());
        } else if ("health".equals(navTo)) {          // ← thêm case này
            binding.bottomNav.setSelectedItemId(R.id.nav_health);
            loadFragment(new HealthFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}