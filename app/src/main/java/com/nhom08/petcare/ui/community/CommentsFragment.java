package com.nhom08.petcare.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.nhom08.petcare.databinding.FragmentCommentsBinding;
import com.nhom08.petcare.R;

import java.util.*;

public class CommentsFragment extends Fragment {

    private static final String DB_URL = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private FragmentCommentsBinding binding;
    private DatabaseReference postRef;
    private final List<Comment> list = new ArrayList<>();
    private RecyclerView.Adapter<?> adapter;

    public static class Comment {
        public String userName, content;
        public long timestamp;
        public Comment() {}
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);

        String postId = getArguments() != null ? getArguments().getString("postId") : null;
        if (postId == null) {
            Toast.makeText(getContext(), "Không tìm thấy bài viết", Toast.LENGTH_SHORT).show();
            return binding.getRoot();
        }

        postRef = FirebaseDatabase.getInstance(DB_URL)
                .getReference("posts")
                .child(postId);

        setupRecyclerView();
        listenForComments();

        binding.btnSendComment.setOnClickListener(v -> sendComment());

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_comment, parent, false);
                return new RecyclerView.ViewHolder(v) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Comment c = list.get(position);
                ((TextView) holder.itemView.findViewById(R.id.tvUserName)).setText(c.userName);
                ((TextView) holder.itemView.findViewById(R.id.tvComment)).setText(c.content);
            }

            @Override
            public int getItemCount() { return list.size(); }
        };

        binding.rvComments.setAdapter(adapter);
    }

    private void listenForComments() {
        postRef.child("comments_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    // Bỏ qua nếu node này là String rỗng (dữ liệu cũ)
                    if (!d.hasChildren()) continue;
                    Comment c = d.getValue(Comment.class);
                    if (c != null) list.add(c);
                }
                // Sắp xếp theo thời gian tăng dần (cmt cũ nhất lên trên)
                list.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getContext() != null)
                    Toast.makeText(getContext(), "Lỗi tải bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendComment() {
        if (binding == null) return;

        String text = binding.etComment.getText().toString().trim();
        if (text.isEmpty()) {
            binding.etComment.setError("Vui lòng nhập bình luận");
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Khóa nút gửi tránh bấm nhiều lần
        binding.btnSendComment.setEnabled(false);

        // Lấy displayName từ Firebase — PHẢI dùng cùng DB_URL
        FirebaseDatabase.getInstance(DB_URL)
                .getReference("users")
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String name = snapshot.child("displayName").getValue(String.class);
                    if (name == null || name.isEmpty()) {
                        name = snapshot.child("username").getValue(String.class);
                    }
                    final String finalName = (name != null && !name.isEmpty()) ? name : "Thành viên";

                    Map<String, Object> data = new HashMap<>();
                    data.put("userName", finalName);
                    data.put("content", text);
                    data.put("timestamp", System.currentTimeMillis());

                    postRef.child("comments_data").push().setValue(data)
                            .addOnSuccessListener(aVoid -> {
                                // Tăng comments_count bằng transaction (tránh race condition)
                                postRef.child("comments_count").runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData md) {
                                        Long current = md.getValue(Long.class);
                                        md.setValue(current == null ? 1L : current + 1L);
                                        return Transaction.success(md);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError error,
                                                           boolean committed,
                                                           @Nullable DataSnapshot snapshot) {}
                                });

                                if (binding != null) {
                                    binding.etComment.setText("");
                                    binding.btnSendComment.setEnabled(true);
                                }
                            })
                            .addOnFailureListener(e -> {
                                if (getContext() != null)
                                    Toast.makeText(getContext(), "Gửi bình luận thất bại", Toast.LENGTH_SHORT).show();
                                if (binding != null)
                                    binding.btnSendComment.setEnabled(true);
                            });
                })
                .addOnFailureListener(e -> {
                    if (getContext() != null)
                        Toast.makeText(getContext(), "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show();
                    if (binding != null)
                        binding.btnSendComment.setEnabled(true);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}