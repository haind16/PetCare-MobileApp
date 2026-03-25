package com.nhom08.petcare.ui.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nhom08.petcare.databinding.ActivityCreatePostBinding;
import com.nhom08.petcare.ui.main.MainActivity;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CreatePostActivity extends AppCompatActivity {

    private ActivityCreatePostBinding binding;
    private Uri selectedImageUri;
    private DatabaseReference db;

    private ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
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

        // URL phải chính xác tuyệt đối như trong file JSON bạn gửi
        db = FirebaseDatabase.getInstance("https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddPhoto.setOnClickListener(v -> pickImage.launch("image/*"));
        binding.btnRemovePhoto.setOnClickListener(v -> {
            selectedImageUri = null;
            binding.layoutPreview.setVisibility(View.GONE);
        });

        binding.btnPost.setOnClickListener(v -> {
            String content = binding.etContent.getText().toString().trim();
            if (content.isEmpty()) {
                binding.etContent.setError("Vui lòng nhập nội dung");
                return;
            }
            // Khóa nút đăng để tránh bấm nhiều lần
            binding.btnPost.setEnabled(false);
            fetchNameAndUpload(content);
        });
    }

    private void fetchNameAndUpload(String content) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // Truy cập thẳng vào node users/{uid}
        db.child("users").child(user.getUid()).get().addOnCompleteListener(task -> {
            String finalName = "Người dùng PetCare";

            if (task.isSuccessful() && task.getResult().exists()) {
                // Lấy displayName từ Database (trong file JSON của bạn là "namdory")
                String dbName = task.getResult().child("displayName").getValue(String.class);
                if (dbName != null && !dbName.isEmpty()) {
                    finalName = dbName;
                } else {
                    // Nếu displayName trống, thử lấy username
                    String dbUsername = task.getResult().child("username").getValue(String.class);
                    if (dbUsername != null) finalName = dbUsername;
                }
            }

            // CHỈ KHI CÓ TÊN RỒI MỚI GỌI HÀM UPLOAD
            executePost(content, finalName);
        });
    }

    private void executePost(String content, String userName) {
        String imagePath = (selectedImageUri != null) ? saveImageToInternal(selectedImageUri) : "";
        String postId = db.child("posts").push().getKey();

        Map<String, Object> data = new HashMap<>();
        data.put("postId", postId);
        data.put("userName", userName);
        data.put("content", content);
        data.put("imageUrl", imagePath);
        data.put("timestamp", System.currentTimeMillis());
        data.put("likes", 0L);
        data.put("comments_count", 0L); // BẮT BUỘC khởi tạo là 0

        if (postId != null) {
            db.child("posts").child(postId).setValue(data).addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private String saveImageToInternal(Uri uri) {
        try {
            String fileName = "post_" + System.currentTimeMillis() + ".jpg";
            InputStream in = getContentResolver().openInputStream(uri);
            FileOutputStream out = openFileOutput(fileName, MODE_PRIVATE);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            out.close(); in.close();
            return getFilesDir() + "/" + fileName;
        } catch (Exception e) { return ""; }
    }
}