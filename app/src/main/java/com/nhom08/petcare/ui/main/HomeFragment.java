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
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.CanNangDao;
import com.nhom08.petcare.data.model.CanNang;
import com.nhom08.petcare.data.model.NhacNho;
import com.nhom08.petcare.data.repository.NhacNhoRepository;
import com.nhom08.petcare.data.repository.PetRepository;
import com.nhom08.petcare.databinding.FragmentHomeBinding;
import com.nhom08.petcare.ui.health.reminder.AddReminderActivity;
import com.nhom08.petcare.ui.interactive.InteractActivity;
import com.nhom08.petcare.ui.pet.list.PetSelectorActivity;
import com.nhom08.petcare.utils.PetManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Fragment Trang chủ của ứng dụng.
 * Hiển thị thông tin tổng quan của thú cưng đang được chọn (Tên, tuổi, cân nặng) 
 * và danh sách các nhắc nhở chăm sóc sắp tới.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PetRepository petRepository;
    private NhacNhoRepository nhacNhoRepo;
    private ReminderAdapter reminderAdapter;
    private final List<NhacNho> reminderList = new ArrayList<>();

    // Launcher để nhận kết quả khi người dùng đổi thú cưng ở màn hình PetSelector
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

        // Nút thêm lịch nhắc nhở
        binding.btnAddSchedule.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddReminderActivity.class)));
        // Nút vào màn hình tương tác (âm thanh huấn luyện)
        binding.btnInteract.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), InteractActivity.class)));
        // Nút đổi thú cưng đang quản lý
        binding.btnChangePet.setOnClickListener(v ->
                petSelectorLauncher.launch(new Intent(getActivity(), PetSelectorActivity.class)));

        setupReminderRecyclerView();

        return binding.getRoot();
    }

    /**
     * Thiết lập danh sách nhắc nhở chăm sóc.
     */
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

    /**
     * Tải thông tin thú cưng hiện tại từ Local Database (Room).
     * Bao gồm tính toán tuổi dựa trên ngày sinh và lấy cân nặng ghi nhận gần nhất.
     */
    private void loadCurrentPet() {
        if (binding == null) return;
        PetManager pm = PetManager.getInstance(requireContext());
        if (!pm.hasPet()) { showEmptyState(); return; }

        petRepository.getPetById(pm.getCurrentPetId(), pet -> {
            if (getActivity() == null || binding == null) return;
            // Khởi tạo luồng phụ để truy vấn dữ liệu Room Database tránh block Main Thread
            new Thread(() -> {
                // Truy vấn cân nặng gần nhất từ Room
                CanNangDao canNangDao = AppDatabase.getInstance(requireContext()).canNangDao();
                List<CanNang> weightRecords = canNangDao.getAllByPet(pm.getCurrentPetId());

                float latestWeight = pet != null ? pet.canNang : 0;

                if (weightRecords != null && !weightRecords.isEmpty()) {
                    // Sắp xếp để lấy ngày gần đây nhất
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Collections.sort(weightRecords, (c1, c2) -> {
                        try {
                            Date date1 = sdf.parse(c1.ngay);
                            Date date2 = sdf.parse(c2.ngay);
                            if (date1 != null && date2 != null) {
                                return date2.compareTo(date1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return 0;
                    });
                    latestWeight = weightRecords.get(0).canNang;
                }

                final float finalWeight = latestWeight;
                // Cập nhật giao diện người dùng trên Main Thread (UI Thread)
                getActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    if (pet == null) { pm.clearCurrentPet(); showEmptyState(); return; }

                    binding.tvPetName.setText(pet.tenThuCung);
                    binding.tvPetAge.setText(tinhTuoi(pet.ngaySinh));

                    binding.tvPetWeight.setText(finalWeight > 0
                            ? "Cân nặng: " + finalWeight + " kg"
                            : "Cân nặng: Chưa có");

                    // Xử lý hiển thị ảnh thú cưng (Hỗ trợ URL và Path local)
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
            }).start();
        });
    }

    /**
     * Tải danh sách các nhắc nhở chưa hoàn thành cho thú cưng hiện tại.
     */
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

    /**
     * Hàm helper tính tuổi thú cưng dựa trên ngày sinh.
     */
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

    /**
     * Adapter nội bộ để hiển thị các item nhắc nhở trong HomeFragment.
     */
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

            // Gán icon và màu sắc phù hợp với từng loại nhắc nhở
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