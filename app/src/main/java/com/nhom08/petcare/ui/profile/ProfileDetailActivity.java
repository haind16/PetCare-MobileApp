package com.nhom08.petcare.ui.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityProfileDetailBinding;
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

public class ProfileDetailActivity extends AppCompatActivity {

    private static final String DB_URL        = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String CLOUD_NAME    = "dt9slcin9";
    private static final String UPLOAD_PRESET = "ml_default";
    private static final String UPLOAD_URL    = "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";

    private ActivityProfileDetailBinding binding;
    private DatabaseReference            userRef;
    private Uri                          selectedImageUri;
    private String                       currentAvatarUrl = ""; // URL Cloudinary hiện tại
    private final ExecutorService        executor = Executors.newSingleThreadExecutor();

    // Launcher chọn ảnh từ gallery
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Preview ngay ảnh vừa chọn
                    binding.imgAvatar.setImageURI(uri);
                    Toast.makeText(this, "Đã chọn ảnh, nhấn Lưu để cập nhật",
                            Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnChangePhoto.setOnClickListener(v -> pickImage.launch("image/*"));
        binding.btnSave.setOnClickListener(v -> handleSave());

        initUserRef();
        loadProfileFromFirebase();
    }

    // ----------------------------------------------------------------
    // Khởi tạo ref đến users/{userId}
    // ----------------------------------------------------------------
    private void initUserRef() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) { Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show(); finish(); return; }
        userRef = FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId);
    }

    // ----------------------------------------------------------------
    // Load thông tin từ Firebase → điền vào form
    // ----------------------------------------------------------------
    private void loadProfileFromFirebase() {
        if (userRef == null) return;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String displayName = snapshot.child("displayName").getValue(String.class);
                String phone       = snapshot.child("phone").getValue(String.class);
                String email       = snapshot.child("email").getValue(String.class);
                String address     = snapshot.child("address").getValue(String.class);
                String avatarUrl   = snapshot.child("avatarUrl").getValue(String.class);

                if (displayName != null) binding.etFullName.setText(displayName);
                if (phone       != null) binding.etPhone.setText(phone);
                if (email       != null) binding.etEmail.setText(email);
                if (address     != null) binding.etAddress.setText(address);

                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    currentAvatarUrl = avatarUrl;
                    Glide.with(ProfileDetailActivity.this)
                            .load(avatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.pet_welcome)
                            .into(binding.imgAvatar);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileDetailActivity.this, "Không tải được hồ sơ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ----------------------------------------------------------------
    // Xử lý nút Lưu — upload ảnh nếu có, sau đó lưu Firebase
    // ----------------------------------------------------------------
    private void handleSave() {
        String fullName = binding.etFullName.getText().toString().trim();
        String phone    = binding.etPhone.getText().toString().trim();
        String email    = binding.etEmail.getText().toString().trim();
        String address  = binding.etAddress.getText().toString().trim();

        // Validate — không cho lưu nếu bất kỳ trường nào bị xoá trắng
        if (fullName.isEmpty()) {
            binding.etFullName.setError("Vui lòng nhập họ tên");
            binding.etFullName.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            binding.etPhone.setError("Vui lòng nhập số điện thoại");
            binding.etPhone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            binding.etEmail.setError("Vui lòng nhập email");
            binding.etEmail.requestFocus();
            return;
        }
        if (address.isEmpty()) {
            binding.etAddress.setError("Vui lòng nhập địa chỉ");
            binding.etAddress.requestFocus();
            return;
        }

        binding.btnSave.setEnabled(false);
        binding.btnSave.setText("Đang lưu...");

        if (selectedImageUri != null) {
            uploadAvatarToCloudinary();
        } else {
            saveToFirebase(currentAvatarUrl);
        }
    }

    // ----------------------------------------------------------------
    // Upload avatar lên Cloudinary → nhận URL → lưu Firebase
    // ----------------------------------------------------------------
    private void uploadAvatarToCloudinary() {
        runOnUiThread(() -> binding.btnSave.setText("Đang tải ảnh..."));

        executor.execute(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] imageBytes = inputStream.readAllBytes();
                inputStream.close();

                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "avatar.jpg",
                                RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                        .addFormDataPart("upload_preset", UPLOAD_PRESET)
                        .build();

                Request request = new Request.Builder().url(UPLOAD_URL).post(requestBody).build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    JSONObject json = new JSONObject(responseBody);
                    String avatarUrl = json.getString("secure_url");
                    runOnUiThread(() -> saveToFirebase(avatarUrl));
                } else {
                    runOnUiThread(() -> onSaveFailed("Upload ảnh thất bại"));
                }
            } catch (Exception e) {
                runOnUiThread(() -> onSaveFailed("Lỗi: " + e.getMessage()));
            }
        });
    }

    // ----------------------------------------------------------------
    // Lưu tất cả thông tin lên Firebase
    // ----------------------------------------------------------------
    private void saveToFirebase(String avatarUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", binding.etFullName.getText().toString().trim());
        updates.put("phone",       binding.etPhone.getText().toString().trim());
        updates.put("email",       binding.etEmail.getText().toString().trim());
        updates.put("address",     binding.etAddress.getText().toString().trim());
        if (!avatarUrl.isEmpty()) {
            updates.put("avatarUrl", avatarUrl);
        }

        userRef.updateChildren(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> onSaveFailed("Lưu thất bại: " + e.getMessage()));
    }

    private void onSaveFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        binding.btnSave.setEnabled(true);
        binding.btnSave.setText("Lưu");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}