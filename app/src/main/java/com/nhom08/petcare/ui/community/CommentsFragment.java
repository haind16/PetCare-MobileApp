package com.nhom08.petcare.ui.community;

import android.os.Bundle;
import android.util.Log;
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
    private FragmentCommentsBinding binding;
    private DatabaseReference postRef;
    private List<Comment> list = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    public static class Comment {
        public String userName, content;
        public long timestamp;
        public Comment() {}
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);
        String postId = getArguments() != null ? getArguments().getString("postId") : "post1";
        postRef = FirebaseDatabase.getInstance("https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("posts").child(postId);

        binding.rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_comment, p, false)) {};
            }
            @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int p) {
                Comment c = list.get(p);
                ((TextView)h.itemView.findViewById(R.id.tvUserName)).setText(c.userName);
                ((TextView)h.itemView.findViewById(R.id.tvComment)).setText(c.content);
            }
            @Override public int getItemCount() { return list.size(); }
        };
        binding.rvComments.setAdapter(adapter);

        postRef.child("comments_data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Comment c = d.getValue(Comment.class);
                    if (c != null) list.add(c);
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });

        binding.btnSendComment.setOnClickListener(v -> sendComment());
        return binding.getRoot();
    }

    private void sendComment() {
        String text = binding.etComment.getText().toString().trim();
        if (text.isEmpty()) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        // 1. Lấy tên hiển thị của Nam từ node users
        FirebaseDatabase.getInstance().getReference("users").child(uid).get().addOnSuccessListener(snapshot -> {
            String name = snapshot.child("displayName").getValue(String.class);

            Map<String, Object> data = new HashMap<>();
            data.put("userName", name != null ? name : "Thành viên");
            data.put("content", text);
            data.put("timestamp", System.currentTimeMillis());

            // 2. Ghi nội dung bình luận vào node comments_data
            postRef.child("comments_data").push().setValue(data)
                    .addOnSuccessListener(aVoid -> {
                        // 3. CHỈ KHI GHI XONG CMT MỚI TĂNG BIẾN ĐẾM
                        // Đổi sang comments_count để tránh trùng tên với biến cũ gây lỗi
                        postRef.child("comments_count").runTransaction(new Transaction.Handler() {
                            @NonNull @Override public Transaction.Result doTransaction(@NonNull MutableData md) {
                                // Dùng Long để khớp với Firebase
                                Long current = md.getValue(Long.class);
                                md.setValue(current == null ? 1L : current + 1L);
                                return Transaction.success(md);
                            }
                            @Override public void onComplete(@Nullable DatabaseError e, boolean b, @Nullable DataSnapshot s) {
                                if (e != null) Log.e("FirebaseErr", e.getMessage());
                            }
                        });
                        binding.etComment.setText("");
                        Toast.makeText(getContext(), "Đã gửi bình luận!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}