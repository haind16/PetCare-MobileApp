package com.nhom08.petcare.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.nhom08.petcare.databinding.FragmentHomeBinding;
import com.nhom08.petcare.ui.health.AddReminderActivity;
import com.nhom08.petcare.ui.interactive.InteractActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // Kết nối nút Thêm lịch mới
        binding.btnAddSchedule.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddReminderActivity.class)));

        // Kết nối nút Tương tác
        binding.btnInteract.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), InteractActivity.class)));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}