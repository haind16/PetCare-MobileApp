package com.nhom08.petcare.ui.community;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityCreatePostBinding;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;
    private Uri selectedImageUri;

    private ActivityResultLauncher<String> pickImage =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            selectedImageUri = uri;
                            binding.layoutPreview.setVisibility(View.VISIBLE);
                            binding.imgPreview.setImageURI(uri);
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Chọn ảnh
        binding.btnAddPhoto.setOnClickListener(v ->
                pickImage.launch("image/*"));

        // Xóa ảnh
        binding.btnRemovePhoto.setOnClickListener(v -> {
            selectedImageUri = null;
            binding.imgPreview.setImageURI(null);
            binding.layoutPreview.setVisibility(View.GONE);
        });

        // Đăng bài
        binding.btnPost.setOnClickListener(v -> {
            String content = binding.etContent.getText().toString().trim();
            if (content.isEmpty()) {
                binding.etContent.setError("Vui lòng nhập nội dung");
                return;
            }
            // TODO: upload lên Firebase
            Toast.makeText(this, "Đã đăng bài!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}