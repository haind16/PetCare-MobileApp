package com.nhom08.petcare.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.FragmentProfileBinding;
import com.nhom08.petcare.ui.auth.LoginActivity;
import com.nhom08.petcare.ui.pet.list.PetListActivity;

public class ProfileFragment extends Fragment {

    private static final String DB_URL = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private FragmentProfileBinding binding;
    private DatabaseReference      userRef;
    private ValueEventListener     userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        setupButtons();
        loadUserInfo();
        return binding.getRoot();
    }

    private void setupButtons() {
        binding.btnAccount.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ProfileDetailActivity.class)));

        binding.btnChangePassword.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class)));

        binding.btnPetList.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), PetListActivity.class)));

        binding.btnLanguage.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), LanguageActivity.class)));

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    // ----------------------------------------------------------------
    // Load tên + avatar realtime từ Firebase
    // ----------------------------------------------------------------
    private void loadUserInfo() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) return;

        userRef = FirebaseDatabase.getInstance(DB_URL).getReference("users").child(userId);

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (binding == null) return;

                // Hiển thị tên
                String displayName = snapshot.child("displayName").getValue(String.class);
                if (displayName != null && !displayName.isEmpty()) {
                    binding.tvUserName.setText(displayName);
                }

                // Hiển thị avatar từ Cloudinary URL
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                if (avatarUrl != null && !avatarUrl.isEmpty() && getActivity() != null) {
                    Glide.with(getActivity())
                            .load(avatarUrl)
                            .circleCrop()
                            .placeholder(R.drawable.pet_welcome)
                            .into(binding.imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };

        // Realtime → tự cập nhật ngay khi user lưu hồ sơ xong
        userRef.addValueEventListener(userListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
        binding = null;
    }
}