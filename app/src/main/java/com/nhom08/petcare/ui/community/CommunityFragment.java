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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.databinding.FragmentCommunityBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private DatabaseReference postsRef;
    private List<PostAdapter.PostItem> postList = new ArrayList<>();
    private PostAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);

        // 1. Khởi tạo Firebase Reference đến node "posts"
        postsRef = FirebaseDatabase.getInstance("https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("posts");

        setupRecyclerView();
        listenForPosts();

        // Ô đăng bài → mở CreatePostActivity
        binding.etPostHint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));
        binding.btnPost.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        // Khởi tạo adapter với list trống và xử lý click vào bài viết
        adapter = new PostAdapter(postList, post -> {
            Intent intent = new Intent(getActivity(), PostDetailActivity.class);
            // Quan trọng: Truyền postId để các màn hình sau biết bài nào mà load dữ liệu
            intent.putExtra("postId", post.postId);
            startActivity(intent);
        });

        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPosts.setAdapter(adapter);
    }

    private void listenForPosts() {
        // 2. Lắng nghe dữ liệu thực tế từ Firebase
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    // Chuyển dữ liệu từ Firebase thành Object (Nam nhớ kiểm tra class PostItem nhé)
                    PostAdapter.PostItem item = data.getValue(PostAdapter.PostItem.class);
                    if (item != null) {
                        item.postId = data.getKey(); // Lưu lại ID để dùng cho Like/Comment
                        postList.add(item);
                    }
                }

                // Đảo ngược danh sách để bài mới nhất hiện lên đầu
                Collections.reverse(postList);

                adapter.notifyDataSetChanged();

                // Ẩn ProgressBar nếu Nam có thêm vào layout
                if (binding.rvPosts.getVisibility() == View.GONE) {
                    binding.rvPosts.setVisibility(View.VISIBLE);
                }
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