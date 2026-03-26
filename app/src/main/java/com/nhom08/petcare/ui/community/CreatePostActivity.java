package com.nhom08.petcare.ui.community;

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

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePostActivity extends AppCompatActivity {

    private static final String DB_URL        = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String CLOUD_NAME    = "dt9slcin9";
    private static final String UPLOAD_PRESET = "ml_default";
    private static final String UPLOAD_URL    = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";

    private ActivityCreatePostBinding binding;
    private Uri selectedImageUri;
    private DatabaseReference db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<String> pickImage =
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

        db = FirebaseDatabase.getInstance(DB_URL).getReference();

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
            binding.btnPost.setEnabled(false);
            binding.btnPost.setText("Đang xử lý...");
            fetchNameAndUpload(content);
        });
    }

    private void fetchNameAndUpload(String content) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            onPostFailed("Vui lòng đăng nhập lại");
            return;
        }

        db.child("users").child(user.getUid()).get().addOnCompleteListener(task -> {
            String finalName = "Người dùng PetCare";
            if (task.isSuccessful() && task.getResult().exists()) {
                String dbName = task.getResult().child("displayName").getValue(String.class);
                if (dbName != null && !dbName.isEmpty()) {
                    finalName = dbName;
                } else {
                    String dbUsername = task.getResult().child("username").getValue(String.class);
                    if (dbUsername != null && !dbUsername.isEmpty()) finalName = dbUsername;
                }
            }
            final String userName = finalName;

            if (selectedImageUri != null) {
                uploadToCloudinary(content, userName);
            } else {
                executePost(content, userName, "");
            }
        });
    }

    private void uploadToCloudinary(String content, String userName) {
        runOnUiThread(() -> binding.btnPost.setText("Đang tải ảnh..."));

        executor.execute(() -> {
            try {
                // Đọc ảnh từ URI
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = inputStream.readAllBytes();
                inputStream.close();

                // Tạo request multipart gửi lên Cloudinary
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "post_image.jpg",
                                RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                        .addFormDataPart("upload_preset", UPLOAD_PRESET)
                        .build();

                Request request = new Request.Builder()
                        .url(UPLOAD_URL)
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(responseBody);
                    String imageUrl = json.getString("secure_url"); // URL https public
                    runOnUiThread(() -> executePost(content, userName, imageUrl));
                } else {
                    runOnUiThread(() -> onPostFailed("Upload ảnh thất bại"));
                }

            } catch (Exception e) {
                runOnUiThread(() -> onPostFailed("Lỗi: " + e.getMessage()));
            }
        });
    }

    private void executePost(String content, String userName, String imageUrl) {
        String postId = db.child("posts").push().getKey();
        if (postId == null) {
            onPostFailed("Lỗi tạo bài viết");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("postId", postId);
        data.put("userName", userName);
        data.put("content", content);
        data.put("imageUrl", imageUrl);   // URL Cloudinary hoặc "" nếu không có ảnh
        data.put("timestamp", System.currentTimeMillis());
        data.put("likes", 0L);
        data.put("comments_count", 0L);

        db.child("posts").child(postId).setValue(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đăng bài thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> onPostFailed("Đăng bài thất bại"));
    }

    private void onPostFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        binding.btnPost.setEnabled(true);
        binding.btnPost.setText("Đăng");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}