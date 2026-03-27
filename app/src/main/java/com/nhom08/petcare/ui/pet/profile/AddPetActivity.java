package com.nhom08.petcare.ui.pet.profile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityAddPetBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddPetActivity extends AppCompatActivity {

    private ActivityAddPetBinding binding;
    private PetRepository repository;
    private String savedImagePath = null;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    savedImagePath = saveImageToInternal(uri);
                    if (savedImagePath != null) {
                        Glide.with(this)
                                .load(new File(savedImagePath))
                                .circleCrop()
                                .into(binding.imgPetPreview);
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
        binding.btnPickImage.setOnClickListener(v -> pickImage.launch("image/*"));

        setupSpinners();
    }

    private void setupSpinners() {
        // Spinner Loài
        ArrayAdapter<String> loaiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Chó", "Mèo", "Khác"});
        loaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spLoai.setAdapter(loaiAdapter);

        // Spinner Giới tính
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Đực", "Cái"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(genderAdapter);
    }

    private String saveImageToInternal(Uri uri) {
        try {
            String fileName = "pet_" + System.currentTimeMillis() + ".jpg";
            InputStream input = getContentResolver().openInputStream(uri);
            FileOutputStream output = openFileOutput(fileName, MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) output.write(buffer, 0, length);
            output.close(); input.close();
            return getFilesDir() + "/" + fileName;
        } catch (Exception e) {
            return null;
        }
    }

    private void savePet() {
        String ten        = binding.etName.getText().toString().trim();
        String loai       = binding.spLoai.getSelectedItem().toString();
        String giong      = binding.etBreed.getText().toString().trim();
        String gioiTinh   = binding.spGender.getSelectedItem().toString();
        String canNangStr = binding.etWeight.getText().toString().trim();
        String ngaySinh   = binding.etBirthDate.getText().toString().trim();

        if (ten.isEmpty()) {
            binding.etName.setError("Vui lòng nhập tên");
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ThuCung pet = new ThuCung();
        pet.userId     = userId;
        pet.tenThuCung = ten;
        pet.loai       = loai;
        pet.giong      = giong;
        pet.gioiTinh   = gioiTinh;
        pet.canNang    = canNangStr.isEmpty() ? 0 : Float.parseFloat(canNangStr);
        pet.ngaySinh   = ngaySinh;
        pet.anhUrl     = savedImagePath;

        repository.addPet(pet, result -> runOnUiThread(() -> {
            Toast.makeText(this, "Đã thêm thú cưng thành công!", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}