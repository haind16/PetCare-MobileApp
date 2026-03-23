package com.nhom08.petcare.ui.pet.profile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityAddPetBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddPetActivity extends AppCompatActivity {

    private ActivityAddPetBinding binding;
    private PetRepository repository;
    private String savedImagePath = null; // đường dẫn ảnh đã lưu

    // Launcher chọn ảnh từ gallery
    private ActivityResultLauncher<String> pickImage =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            // Lưu ảnh vào bộ nhớ trong
                            savedImagePath = saveImageToInternal(uri);
                            if (savedImagePath != null) {
                                // Hiển thị preview ảnh
                                com.bumptech.glide.Glide.with(this)
                                        .load(new File(savedImagePath))
                                        .circleCrop()
                                        .into(binding.imgPetPreview);
                                Toast.makeText(this, "Đã chọn ảnh!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new PetRepository(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnConfirm.setOnClickListener(v -> savePet());

        // Nút chọn ảnh
        binding.btnPickImage.setOnClickListener(v ->
                pickImage.launch("image/*"));
    }

    private String saveImageToInternal(Uri uri) {
        try {
            String fileName = "pet_" +
                    System.currentTimeMillis() + ".jpg";
            InputStream input = getContentResolver()
                    .openInputStream(uri);
            FileOutputStream output = openFileOutput(
                    fileName, MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.close();
            input.close();

            return getFilesDir() + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void savePet() {
        String ten        = binding.etName.getText().toString().trim();
        String gioiTinh   = binding.etGender.getText().toString().trim();
        String canNangStr = binding.etWeight.getText().toString().trim();
        String ngaySinh   = binding.etBirthDate.getText().toString().trim();
        String giong      = binding.etBreed.getText().toString().trim();
        String dieuKien   = binding.etLivingCondition.getText().toString().trim();

        if (ten.isEmpty()) {
            binding.etName.setError("Vui lòng nhập tên");
            return;
        }

        String userId = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();

        ThuCung pet = new ThuCung();
        pet.userId     = userId;
        pet.tenThuCung = ten;
        pet.gioiTinh   = gioiTinh;
        pet.canNang    = canNangStr.isEmpty() ? 0 :
                Float.parseFloat(canNangStr);
        pet.ngaySinh   = ngaySinh;
        pet.giong      = giong;
        pet.anhUrl     = savedImagePath; // lưu đường dẫn ảnh
        pet.loai       = "";

        repository.addPet(pet, result -> runOnUiThread(() -> {
            Toast.makeText(this, "Đã thêm " + ten + "!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}