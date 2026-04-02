package com.nhom08.petcare.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.model.NhacNho;
import com.nhom08.petcare.data.repository.NhacNhoRepository;
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
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PetRepository petRepository;
    private NhacNhoRepository nhacNhoRepo;
    private ReminderAdapter reminderAdapter;
    private final List<NhacNho> reminderList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> petSelectorLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> loadCurrentPet());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        petRepository = new PetRepository(requireContext());
        nhacNhoRepo   = new NhacNhoRepository(requireContext());

        binding.btnAddSchedule.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddReminderActivity.class)));
        binding.btnInteract.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), InteractActivity.class)));
        binding.btnChangePet.setOnClickListener(v ->
                petSelectorLauncher.launch(new Intent(getActivity(), PetSelectorActivity.class)));

        setupReminderRecyclerView();

        return binding.getRoot();
    }

    private void setupReminderRecyclerView() {
        reminderAdapter = new ReminderAdapter(reminderList);
        binding.rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvReminders.setAdapter(reminderAdapter);
        binding.rvReminders.setNestedScrollingEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCurrentPet();
        loadReminders();
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
                binding.tvPetWeight.setText(pet.canNang > 0
                        ? "Cân nặng: " + pet.canNang + " kg"
                        : "Cân nặng: Chưa có");

                if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
                    if (pet.anhUrl.startsWith("http")) {
                        Glide.with(requireContext())
                                .load(pet.anhUrl)
                                .circleCrop()
                                .placeholder(R.drawable.pet_welcome)
                                .into(binding.imgPet);
                    } else {
                        Glide.with(requireContext())
                                .load(new File(pet.anhUrl))
                                .circleCrop()
                                .placeholder(R.drawable.pet_welcome)
                                .into(binding.imgPet);
                    }
                } else {
                    binding.imgPet.setImageResource(R.drawable.pet_welcome);
                }
            });
        });
    }

    private void loadReminders() {
        if (binding == null) return;
        PetManager pm = PetManager.getInstance(requireContext());
        if (!pm.hasPet()) return;

        nhacNhoRepo.getChuaHoanThanh(pm.getCurrentPetId(), list -> {
            if (getActivity() == null || binding == null) return;
            getActivity().runOnUiThread(() -> {
                reminderList.clear();
                reminderList.addAll(list);
                reminderAdapter.notifyDataSetChanged();

                // Ẩn/hiện trạng thái trống
                if (list.isEmpty()) {
                    binding.tvNoReminder.setVisibility(View.VISIBLE);
                    binding.rvReminders.setVisibility(View.GONE);
                } else {
                    binding.tvNoReminder.setVisibility(View.GONE);
                    binding.rvReminders.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private String tinhTuoi(String ngaySinh) {
        if (ngaySinh == null || ngaySinh.isEmpty()) return "Tuổi: Chưa rõ";
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate born  = LocalDate.parse(ngaySinh, fmt);
            LocalDate today = LocalDate.now();
            if (born.isAfter(today)) return "Tuổi: Chưa sinh";
            Period p = Period.between(born, today);
            if (p.getYears() > 0)  return "Tuổi: " + p.getYears() + " tuổi";
            if (p.getMonths() > 0) return "Tuổi: " + p.getMonths() + " tháng";
            return "Tuổi: " + p.getDays() + " ngày";
        } catch (Exception e) { return "Tuổi: " + ngaySinh; }
    }

    private void showEmptyState() {
        if (binding == null) return;
        binding.tvPetName.setText("Chưa có thú cưng");
        binding.tvPetAge.setText("Tuổi: --");
        binding.tvPetWeight.setText("Cân nặng: --");
        binding.imgPet.setImageResource(R.drawable.pet_welcome);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Adapter nhắc nhở inline ───────────────────────────────────────────────
    static class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.VH> {

        private final List<NhacNho> list;

        ReminderAdapter(List<NhacNho> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reminder, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            NhacNho item = list.get(position);
            h.tvTitle.setText(item.loai);
            h.tvTime.setText(item.ngay + "  " + item.gio);

            // Icon theo loại
            int iconRes   = R.drawable.ic_bell;
            int bgRes     = R.drawable.bg_icon_blue;
            int tintColor = 0xFF1565C0;

            String loai = item.loai != null ? item.loai.toLowerCase() : "";
            if (loai.contains("ăn") || loai.contains("an")) {
                iconRes = R.drawable.ic_food; bgRes = R.drawable.bg_icon_blue; tintColor = 0xFF1565C0;
            } else if (loai.contains("tiêm") || loai.contains("tiem")) {
                iconRes = R.drawable.ic_syringe; bgRes = R.drawable.bg_icon_pink; tintColor = 0xFFAD1457;
            } else if (loai.contains("thuốc") || loai.contains("thuoc")) {
                iconRes = R.drawable.ic_pill; bgRes = R.drawable.bg_icon_yellow; tintColor = 0xFFE65100;
            } else if (loai.contains("dạo") || loai.contains("dao") || loai.contains("đi")) {
                iconRes = R.drawable.ic_walk; bgRes = R.drawable.bg_interact_btn; tintColor = 0xFF2E7D32;
            } else if (loai.contains("cắt") || loai.contains("lông")) {
                iconRes = R.drawable.ic_brush; bgRes = R.drawable.bg_icon_blue; tintColor = 0xFF6A1B9A;
            }

            h.imgIcon.setImageResource(iconRes);
            h.imgIcon.setColorFilter(tintColor);
            h.iconBg.setBackgroundResource(bgRes);
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvTime;
            ImageView imgIcon;
            LinearLayout iconBg;

            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvReminderTitle);
                tvTime  = v.findViewById(R.id.tvReminderTime);
                imgIcon = v.findViewById(R.id.imgReminderIcon);
                iconBg  = v.findViewById(R.id.layoutReminderIcon);
            }
        }
    }
}