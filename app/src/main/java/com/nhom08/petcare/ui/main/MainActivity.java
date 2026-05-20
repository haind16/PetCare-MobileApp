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

/**
 * Activity chính của ứng dụng.
 * Quản lý Navigation Bottom Bar và điều hướng giữa các Fragment: Trang chủ, Sức khỏe, Cộng đồng, Cửa hàng, Cá nhân.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mặc định chọn HomeFragment khi khởi chạy
        binding.bottomNav.setSelectedItemId(R.id.nav_home);
        loadFragment(new HomeFragment());

        // Xử lý sự kiện khi chọn các item trên thanh điều hướng dưới
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

        // Kiểm tra nếu được mở từ các thông báo hoặc điều hướng đặc biệt (thanh toán, nhắc nhở)
        handleIntent(getIntent());
    }

    // Nhận intent khi activity đang chạy (onNewIntent)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * Xử lý dữ liệu từ Intent để điều hướng đến màn hình cụ thể.
     */
    private void handleIntent(Intent intent) {
        if (intent == null) return;
        String navTo = intent.getStringExtra("nav_to");
        if ("shop".equals(navTo)) {
            binding.bottomNav.setSelectedItemId(R.id.nav_shop);
            loadFragment(new ShopFragment());
        } else if ("health".equals(navTo)) {
            binding.bottomNav.setSelectedItemId(R.id.nav_health);
            loadFragment(new HealthFragment());
        }
    }

    /**
     * Thay thế Fragment hiện tại trong Container.
     * @param fragment Fragment cần hiển thị.
     */
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}