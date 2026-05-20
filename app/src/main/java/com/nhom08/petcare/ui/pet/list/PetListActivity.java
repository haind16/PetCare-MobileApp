package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.ActivityPetListBinding;
import com.nhom08.petcare.ui.pet.profile.AddPetActivity;
import com.nhom08.petcare.ui.pet.profile.PetProfileActivity;
import com.nhom08.petcare.utils.PetManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity hiển thị danh sách tất cả thú cưng của người dùng hiện tại.
 * Cho phép người dùng xem, thêm mới hoặc xóa hồ sơ thú cưng.
 */
public class PetListActivity extends AppCompatActivity {

    private ActivityPetListBinding binding;
    private PetRepository repository;
    private PetAdapter adapter;
    private List<ThuCung> petList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPetListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new PetRepository(this);

        binding.btnBack.setOnClickListener(v -> finish());
        
        // Mở màn hình thêm thú cưng mới
        binding.btnAddPet.setOnClickListener(v ->
                startActivity(new Intent(this, AddPetActivity.class)));

        setupRecyclerView();
        loadPets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách mỗi khi quay lại màn hình để cập nhật thông tin mới nhất
        loadPets();
    }

    /**
     * Thiết lập RecyclerView và Adapter để hiển thị danh sách thú cưng.
     * Cung cấp listener để xử lý sự kiện xóa thú cưng.
     */
    private void setupRecyclerView() {
        adapter = new PetAdapter(petList);
        adapter.setOnDeleteListener(pet ->
                repository.deletePet(pet.id, result ->
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Đã xóa " + pet.tenThuCung,
                                    Toast.LENGTH_SHORT).show();
                            loadPets(); // Tải lại sau khi xóa
                        })));
        binding.rvPetList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPetList.setAdapter(adapter);
    }

    /**
     * Lấy danh sách thú cưng của người dùng hiện tại từ Repository.
     */
    private void loadPets() {
        String userId = FirebaseAuth.getInstance()
                .getCurrentUser().getUid();

        repository.getAllPets(userId, pets -> {
            runOnUiThread(() -> {
                petList.clear();
                petList.addAll(pets);
                adapter.notifyDataSetChanged();
            });
        });
    }
}