package com.nhom08.petcare.ui.community;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.*;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivityPostDetailBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    private ActivityPostDetailBinding binding;
    private String postId;
    private DatabaseReference postRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postId = getIntent().getStringExtra("postId");
        if (postId == null) { finish(); return; }

        postRef = FirebaseDatabase.getInstance(
                "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("posts").child(postId);

        binding.btnBack.setOnClickListener(v -> finish());

        setupViewPager();
        listenToPostChanges();
    }

    private void setupViewPager() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override public int getItemCount() { return 2; }
            @NonNull @Override public Fragment createFragment(int position) {
                Fragment f = (position == 0) ? new LikesFragment() : new CommentsFragment();
                Bundle b = new Bundle();
                b.putString("postId", postId);
                f.setArguments(b);
                return f;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, pos) -> tab.setText(pos == 0 ? "Thích" : "Bình luận")
        ).attach();

        // Nếu được mở từ nút bình luận → nhảy thẳng sang tab Bình luận
        boolean navToComments = getIntent().getBooleanExtra("nav_to_comments", false);
        if (navToComments) {
            binding.viewPager.post(() -> binding.viewPager.setCurrentItem(1, false));
        }
    }

    private void listenToPostChanges() {
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user      = snapshot.child("userName").getValue(String.class);
                String content   = snapshot.child("content").getValue(String.class);
                String img       = snapshot.child("imageUrl").getValue(String.class);
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                Long likes       = snapshot.child("likes").getValue(Long.class);
                Long cmts        = snapshot.child("comments_count").getValue(Long.class);

                binding.tvUserName.setText(user != null ? user : "Người dùng");
                binding.tvContent.setText(content != null ? content : "");
                binding.tvLikeCount.setText(String.valueOf(likes != null ? likes : 0));
                binding.tvCommentCount.setText(String.valueOf(cmts != null ? cmts : 0));

                // Hiện thời gian đăng
                Long ts = snapshot.child("timestamp").getValue(Long.class);
                binding.tvTime.setText(ts != null ? formatTime(ts) : "");

                // Load avatar
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(PostDetailActivity.this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.pet_welcome)
                            .circleCrop()
                            .into(binding.imgAvatar);
                } else {
                    binding.imgAvatar.setImageResource(R.drawable.pet_welcome);
                }

                // Load ảnh bài đăng
                if (img != null && !img.isEmpty()) {
                    binding.imgPost.setVisibility(View.VISIBLE);
                    Object imageSource = img.startsWith("http") ? img : new File(img);
                    Glide.with(PostDetailActivity.this).load(imageSource).into(binding.imgPost);
                } else {
                    binding.imgPost.setVisibility(View.GONE);
                }

                // Cập nhật tiêu đề tab
                if (binding.tabLayout.getTabAt(0) != null)
                    binding.tabLayout.getTabAt(0).setText("Thích (" + (likes != null ? likes : 0) + ")");
                if (binding.tabLayout.getTabAt(1) != null)
                    binding.tabLayout.getTabAt(1).setText("Bình luận (" + (cmts != null ? cmts : 0) + ")");
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String formatTime(long timestamp) {
        long now  = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60_000)          return "Vừa xong";
        if (diff < 3_600_000)       return (diff / 60_000) + " phút trước";
        if (diff < 86_400_000)      return (diff / 3_600_000) + " giờ trước";
        if (diff < 7 * 86_400_000L) return (diff / 86_400_000) + " ngày trước";

        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(timestamp));
    }
}