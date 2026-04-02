package com.nhom08.petcare.ui.pet.profile;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityEditPetBinding;
import com.nhom08.petcare.utils.CloudinaryUploader;
import com.nhom08.petcare.utils.PetManager;
import java.io.File;
import java.util.UUID;

public class EditPetActivity extends AppCompatActivity {

    private ActivityEditPetBinding binding;
    private PetRepository repository;
    private ThuCung currentPet;
    private Uri selectedImageUri = null;   // Uri ảnh mới user chọn (null = chưa đổi)

    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Glide.with(this).load(uri).centerCrop().into(binding.imgPet);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new PetRepository(this);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnChangePhoto.setOnClickListener(v -> pickImage.launch("image/*"));
        binding.btnSave.setOnClickListener(v -> validateAndSave());

        setupSpinners();

        String petId = getIntent().getStringExtra("pet_id");
        if (petId != null) {
            repository.getPetById(petId, pet -> runOnUiThread(() -> {
                if (pet == null) { finish(); return; }
                currentPet = pet;
                bindPetToForm(pet);
            }));
        }
    }

    private void setupSpinners() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Đực", "Cái"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(genderAdapter);

        ArrayAdapter<String> livingAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Căn hộ vừa", "Nhà có sân", "Chung cư nhỏ"});
        livingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spLiving.setAdapter(livingAdapter);
    }

    private void bindPetToForm(ThuCung pet) {
        binding.etName.setText(pet.tenThuCung);
        binding.etBirthDate.setText(pet.ngaySinh);
        binding.etWeight.setText(pet.canNang > 0 ? String.valueOf(pet.canNang) : "");
        binding.etBreed.setText(pet.giong != null ? pet.giong : "");
        if ("Cái".equals(pet.gioiTinh)) binding.spGender.setSelection(1);
        else binding.spGender.setSelection(0);

        // Load ảnh hiện tại
        if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
            if (pet.anhUrl.startsWith("http")) {
                // Cloudinary URL
                Glide.with(this).load(pet.anhUrl)
                        .centerCrop().placeholder(R.drawable.pet_welcome)
                        .into(binding.imgPet);
            } else {
                // Local path (cũ)
                Glide.with(this).load(new File(pet.anhUrl))
                        .centerCrop().placeholder(R.drawable.pet_welcome)
                        .into(binding.imgPet);
            }
        }
    }

    private void validateAndSave() {
        if (currentPet == null) return;
        String ten = binding.etName.getText().toString().trim();
        if (ten.isEmpty()) {
            binding.etName.setError("Vui lòng nhập tên");
            return;
        }

        binding.btnSave.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (selectedImageUri != null) {
            // User đổi ảnh mới → upload Cloudinary
            CloudinaryUploader.uploadImage(this, selectedImageUri,
                    new CloudinaryUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String imageUrl) {
                            savePet(imageUrl);
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(EditPetActivity.this,
                                    "Không upload được ảnh, giữ ảnh cũ", Toast.LENGTH_SHORT).show();
                            savePet(currentPet.anhUrl); // giữ nguyên ảnh cũ
                        }
                    });
        } else {
            // Không đổi ảnh → giữ nguyên
            savePet(currentPet.anhUrl);
        }
    }

    private void savePet(String imageUrl) {
        String ten        = binding.etName.getText().toString().trim();
        String ngaySinh   = binding.etBirthDate.getText().toString().trim();
        String canNangStr = binding.etWeight.getText().toString().trim();
        String giong      = binding.etBreed.getText().toString().trim();
        String gioiTinh   = binding.spGender.getSelectedItem().toString();

        currentPet.tenThuCung = ten;
        currentPet.giong      = giong;
        currentPet.ngaySinh   = ngaySinh;
        currentPet.gioiTinh   = gioiTinh;
        currentPet.anhUrl     = imageUrl;

        try {
            String cleanWeight = canNangStr.replace("kg", "").replace("KG", "").trim();
            currentPet.canNang = cleanWeight.isEmpty() ? 0 : Float.parseFloat(cleanWeight);
        } catch (Exception e) { currentPet.canNang = 0; }

        repository.updatePet(currentPet, result -> runOnUiThread(() -> {
            // Tạo bản ghi can_nang nếu cân nặng thay đổi
            if (currentPet.canNang > 0) {
                new Thread(() -> {
                    CanNang cn  = new CanNang();
                    cn.id       = UUID.randomUUID().toString();
                    cn.petId    = currentPet.id;
                    cn.canNang  = currentPet.canNang;
                    cn.ngay     = new java.text.SimpleDateFormat(
                            "dd/MM/yyyy", java.util.Locale.getDefault()
                    ).format(new java.util.Date());
                    AppDatabase.getInstance(this).canNangDao().insert(cn);
                }).start();
            }

            PetManager pm = PetManager.getInstance(this);
            if (currentPet.id.equals(pm.getCurrentPetId())) {
                pm.setCurrentPet(currentPet.id, currentPet.tenThuCung,
                        currentPet.anhUrl != null ? currentPet.anhUrl : "");
            }

            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Đã cập nhật thông tin!", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}