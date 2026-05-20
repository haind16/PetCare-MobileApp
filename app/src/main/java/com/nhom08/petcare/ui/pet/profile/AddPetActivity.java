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
import com.google.firebase.auth.FirebaseAuth;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityAddPetBinding;
import com.nhom08.petcare.utils.CloudinaryUploader;
import com.nhom08.petcare.utils.PetManager;
import java.util.UUID;

/**
 * Activity hỗ trợ thêm mới hồ sơ thú cưng.
 * Người dùng nhập thông tin: Tên, loại, giống, giới tính, cân nặng, ngày sinh và chọn ảnh.
 * Ảnh sẽ được tải lên Cloudinary, dữ liệu lưu vào Room Database và đồng bộ lên Firebase.
 */
public class AddPetActivity extends AppCompatActivity {

    private ActivityAddPetBinding binding;
    private PetRepository repository;
    private Uri selectedImageUri = null;   // Uri của ảnh người dùng đã chọn từ thư viện

    // Khởi tạo trình chọn ảnh từ thư viện
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    // Hiển thị ảnh xem trước dạng hình tròn
                    Glide.with(this).load(uri).circleCrop().into(binding.imgPetPreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new PetRepository(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnConfirm.setOnClickListener(v -> validateAndSave());
        binding.btnPickImage.setOnClickListener(v -> pickImage.launch("image/*"));

        setupSpinners();
    }

    /**
     * Khởi tạo dữ liệu cho các Spinner (Loại thú cưng, Giới tính).
     */
    private void setupSpinners() {
        ArrayAdapter<String> loaiAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Chó", "Mèo", "Khác"});
        loaiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spLoai.setAdapter(loaiAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{"Đực", "Cái"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(genderAdapter);
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu nhập vào trước khi lưu.
     */
    private void validateAndSave() {
        String ten = binding.etName.getText().toString().trim();
        if (ten.isEmpty()) {
            binding.etName.setError("Vui lòng nhập tên");
            return;
        }

        binding.btnConfirm.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (selectedImageUri != null) {
            // Nếu người dùng có chọn ảnh, tiến hành upload lên Cloudinary trước khi lưu hồ sơ
            CloudinaryUploader.uploadImage(this, selectedImageUri, new CloudinaryUploader.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    savePet(imageUrl); // Lưu hồ sơ kèm theo URL ảnh từ Cloudinary
                }

                @Override
                public void onFailure(String error) {
                    // Nếu upload thất bại, lưu bằng đường dẫn ảnh local để đảm bảo hoạt động offline
                    Toast.makeText(AddPetActivity.this,
                            "Không upload được ảnh, lưu offline", Toast.LENGTH_SHORT).show();
                    savePet(saveImageToInternal(selectedImageUri));
                }
            });
        } else {
            // Trường hợp không chọn ảnh
            savePet(null);
        }
    }

    /**
     * Thực hiện lưu thông tin thú cưng vào cơ sở dữ liệu.
     * @param imageUrl Đường dẫn ảnh (URL hoặc local path)
     */
    private void savePet(String imageUrl) {
        String ten        = binding.etName.getText().toString().trim();
        String loai       = binding.spLoai.getSelectedItem().toString();
        String giong      = binding.etBreed.getText().toString().trim();
        String gioiTinh   = binding.spGender.getSelectedItem().toString();
        String canNangStr = binding.etWeight.getText().toString().trim();
        String ngaySinh   = binding.etBirthDate.getText().toString().trim();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ThuCung pet = new ThuCung();
        pet.id         = UUID.randomUUID().toString();
        pet.userId     = userId;
        pet.tenThuCung = ten;
        pet.loai       = loai;
        pet.giong      = giong;
        pet.gioiTinh   = gioiTinh;
        pet.canNang    = canNangStr.isEmpty() ? 0 : Float.parseFloat(canNangStr);
        pet.ngaySinh   = ngaySinh;
        pet.anhUrl     = imageUrl;

        // Lưu thông qua Repository
        repository.addPet(pet, result -> runOnUiThread(() -> {
            // Nếu có nhập cân nặng ban đầu, tự động tạo một bản ghi lịch sử cân nặng
            if (pet.canNang > 0) {
                new Thread(() -> {
                    CanNang cn = new CanNang();
                    cn.id      = UUID.randomUUID().toString();
                    cn.petId   = pet.id;
                    cn.canNang = pet.canNang;
                    cn.ngay    = new java.text.SimpleDateFormat(
                            "dd/MM/yyyy", java.util.Locale.getDefault()
                    ).format(new java.util.Date());
                    AppDatabase.getInstance(this).canNangDao().insert(cn);
                }).start();
            }

            // Thiết lập thú cưng vừa tạo làm thú cưng mặc định hiện tại
            PetManager.getInstance(this).setCurrentPet(
                    pet.id, pet.tenThuCung,
                    pet.anhUrl != null ? pet.anhUrl : "");

            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Đã thêm thú cưng thành công!", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }

    /**
     * Copy ảnh từ Uri vào bộ nhớ trong của ứng dụng để lưu trữ offline.
     */
    private String saveImageToInternal(Uri uri) {
        try {
            String fileName = "pet_" + System.currentTimeMillis() + ".jpg";
            java.io.InputStream input = getContentResolver().openInputStream(uri);
            java.io.FileOutputStream output = openFileOutput(fileName, MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) output.write(buffer, 0, length);
            output.close(); input.close();
            return getFilesDir() + "/" + fileName;
        } catch (Exception e) { return null; }
    }
}