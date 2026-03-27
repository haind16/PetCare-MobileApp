package com.nhom08.petcare.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.FragmentCommunityBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityFragment extends Fragment {

    private static final String DB_URL = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private FragmentCommunityBinding binding;
    private DatabaseReference postsRef;
    private final List<PostAdapter.PostItem> postList = new ArrayList<>();
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);

        postsRef = FirebaseDatabase.getInstance(DB_URL).getReference("posts");

        setupRecyclerView();
        listenForPosts();
        loadCurrentUserInfo();

        binding.etPostHint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));
        binding.btnPost.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        return binding.getRoot();
    }

    private void loadCurrentUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(user.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (binding == null) return;

                    // Load avatar vào ô đăng bài
                    String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.pet_welcome)
                                .circleCrop()
                                .into(binding.imgCurrentUserAvatar);
                    }
                });
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter(
                postList,
                post -> openPostDetail(post.postId, false),
                post -> openPostDetail(post.postId, true)
        );

        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPosts.setAdapter(adapter);
    }

    private void openPostDetail(String postId, boolean navToComments) {
        Intent intent = new Intent(getActivity(), PostDetailActivity.class);
        intent.putExtra("postId", postId);
        intent.putExtra("nav_to_comments", navToComments);
        startActivity(intent);
    }

    private void listenForPosts() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    PostAdapter.PostItem item = data.getValue(PostAdapter.PostItem.class);
                    if (item != null) {
                        item.postId = data.getKey();
                        postList.add(item);
                    }
                }
                Collections.reverse(postList);
                adapter.notifyDataSetChanged();

                if (binding != null && binding.rvPosts.getVisibility() == View.GONE)
                    binding.rvPosts.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Lỗi kết nối database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}