package com.nhom08.petcare.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.FragmentHomeBinding;
import com.nhom08.petcare.ui.health.reminder.AddReminderActivity;
import com.nhom08.petcare.ui.interactive.InteractActivity;
import com.nhom08.petcare.ui.pet.list.PetSelectorActivity;
import com.nhom08.petcare.utils.PetManager;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PetRepository petRepository;

    private final ActivityResultLauncher<Intent> petSelectorLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> loadCurrentPet());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        petRepository = new PetRepository(requireContext());

        binding.btnAddSchedule.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddReminderActivity.class)));
        binding.btnInteract.setOnClickListener(v -> startActivity(new Intent(getActivity(), InteractActivity.class)));
        binding.btnChangePet.setOnClickListener(v -> petSelectorLauncher.launch(new Intent(getActivity(), PetSelectorActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentPet();
    }

    private void loadCurrentPet() {
        if (binding == null) return;
        PetManager pm = PetManager.getInstance(requireContext());
        if (!pm.hasPet()) { showEmptyState(); return; }

        petRepository.getPetById(pm.getCurrentPetId(), pet -> {
            if (getActivity() == null || binding == null) return;
            getActivity().runOnUiThread(() -> {
                if (pet == null) { pm.clearCurrentPet(); showEmptyState(); return; }

                binding.tvPetName.setText(pet.tenThuCung);
                binding.tvPetAge.setText(tinhTuoi(pet.ngaySinh));

                // HIỂN THỊ CÂN NẶNG TRỰC TIẾP TỪ BẢNG THÚ CƯNG
                if (pet.canNang > 0) {
                    binding.tvPetWeight.setText("Cân nặng: " + pet.canNang + " kg");
                } else {
                    binding.tvPetWeight.setText("Cân nặng: Chưa có");
                }

                if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
                    Glide.with(requireContext()).load(new File(pet.anhUrl)).circleCrop().placeholder(R.drawable.pet_welcome).into(binding.imgPet);
                } else {
                    binding.imgPet.setImageResource(R.drawable.pet_welcome);
                }
            });
        });
    }

    private String tinhTuoi(String ngaySinh) {
        if (ngaySinh == null || ngaySinh.isEmpty()) return "Tuổi: Chưa rõ";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate born = LocalDate.parse(ngaySinh, fmt);
            LocalDate today = LocalDate.now();
            if (born.isAfter(today)) return "Tuổi: Chưa sinh";
            Period p = Period.between(born, today);
            if (p.getYears() > 0) return "Tuổi: " + p.getYears() + " tuổi";
            if (p.getMonths() > 0) return "Tuổi: " + p.getMonths() + " tháng";
            return "Tuổi: " + p.getDays() + " ngày";
        } catch (Exception e) { return "Tuổi: " + ngaySinh; }
    }

    private void showEmptyState() {
        binding.tvPetName.setText("Chưa có thú cưng");
        binding.tvPetAge.setText("Tuổi: --");
        binding.tvPetWeight.setText("Cân nặng: --");
        binding.imgPet.setImageResource(R.drawable.pet_welcome);
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}