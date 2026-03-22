package com.nhom08.petcare.ui.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nhom08.petcare.databinding.FragmentCommunityBinding;
import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);

        // Ô đăng bài → mở CreatePostActivity
        binding.etPostHint.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));
        binding.btnPost.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        // Data mẫu
        List<PostAdapter.PostItem> posts = new ArrayList<>();
        posts.add(new PostAdapter.PostItem(
                "Nguyễn Văn A", "2 giờ trước",
                "Hôm nay đưa Milu đi tiêm", 12, 5));
        posts.add(new PostAdapter.PostItem(
                "Trần Thị B", "3 giờ trước",
                "Nhím giờ mà ghét thế Milu", 22, 8));

        PostAdapter adapter = new PostAdapter(posts, post ->
                startActivity(new Intent(getActivity(), PostDetailActivity.class)));

        binding.rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvPosts.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}