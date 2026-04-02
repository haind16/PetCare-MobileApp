package com.nhom08.petcare.ui.profile;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityLanguageBinding;
import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {

    private ActivityLanguageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnVietnamese.setOnClickListener(v -> {
            setLocale("vi");
            Toast.makeText(this, "Đã chuyển sang Tiếng Việt", Toast.LENGTH_SHORT).show();
        });

//        binding.btnEnglish.setOnClickListener(v -> {
//            setLocale("en");
//            Toast.makeText(this, "Switched to English", Toast.LENGTH_SHORT).show();
//        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());
        recreate();
    }
}