package com.nhom08.petcare.ui.main;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityMainBinding;
import com.nhom08.petcare.ui.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set Home được chọn mặc định TRƯỚC khi set listener
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

        // Load HomeFragment mặc định
        loadFragment(new HomeFragment());

        // Listener SAU khi set selected
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}