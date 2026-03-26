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

import com.google.firebase.database.*;
import com.nhom08.petcare.databinding.FragmentCommunityBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityFragment extends Fragment {

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

        postsRef = FirebaseDatabase.getInstance(
                "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("posts");

        setupRecyclerView();
        listenForPosts();

        binding.etPostHint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));
        binding.btnPost.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new PostAdapter(
                postList,
                // Click vào bài → mở PostDetail tab Thích (mặc định)
                post -> openPostDetail(post.postId, false),
                // Click vào nút bình luận → mở PostDetail tab Bình luận
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
                Collections.reverse(postList); // Bài mới nhất lên đầu
                adapter.notifyDataSetChanged();

                if (binding.rvPosts.getVisibility() == View.GONE)
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