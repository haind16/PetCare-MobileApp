package com.nhom08.petcare.ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.nhom08.petcare.R;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static class PostItem {
        public String postId, userName, content, imageUrl, avatarUrl;
        public long timestamp, likes, comments_count;
        public boolean isLiked = false;
        public PostItem() {}
    }

    public interface OnPostClickListener    { void onClick(PostItem post); }
    public interface OnCommentClickListener { void onClick(PostItem post); }

    private final List<PostItem> list;
    private final OnPostClickListener postClickListener;
    private final OnCommentClickListener commentClickListener;
    private final String myUid = FirebaseAuth.getInstance().getUid();
    private final DatabaseReference db = FirebaseDatabase.getInstance(
            "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app"
    ).getReference();

    public PostAdapter(List<PostItem> list,
                       OnPostClickListener postClickListener,
                       OnCommentClickListener commentClickListener) {
        this.list = list;
        this.postClickListener = postClickListener;
        this.commentClickListener = commentClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem item = list.get(position);

        holder.tvUserName.setText(item.userName);
        holder.tvContent.setText(item.content);
        holder.tvLikeCount.setText(String.valueOf(item.likes));
        holder.tvCommentCount.setText(String.valueOf(item.comments_count));

        // Load avatar người đăng
        if (item.avatarUrl != null && !item.avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.pet_welcome)
                    .circleCrop()
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.pet_welcome);
        }

        // Load ảnh bài đăng — hỗ trợ cả URL Cloudinary (https://) và File Internal
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Object imageSource = item.imageUrl.startsWith("http")
                    ? item.imageUrl          // URL Cloudinary
                    : new File(item.imageUrl); // File Internal (ảnh cũ)
            Glide.with(holder.itemView.getContext())
                    .load(imageSource)
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        // Lắng nghe trạng thái like của user hiện tại
        DatabaseReference postRef = db.child("posts").child(item.postId);
        postRef.child("userLikes").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                item.isLiked = s.exists();
                int color = item.isLiked
                        ? android.graphics.Color.parseColor("#FF5722")
                        : android.graphics.Color.parseColor("#BDBDBD");
                holder.imgHeart.setColorFilter(color);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        // Nút like
        holder.btnLike.setOnClickListener(v -> {
            if (!item.isLiked) {
                db.child("users").child(myUid).get().addOnSuccessListener(s -> {
                    String name   = s.child("displayName").getValue(String.class);
                    String avatar = s.child("avatarUrl").getValue(String.class);

                    // Lưu object {name, avatarUrl} thay vì chỉ String tên
                    Map<String, Object> likeData = new java.util.HashMap<>();
                    likeData.put("name",      name != null && !name.isEmpty() ? name : "Thành viên");
                    likeData.put("avatarUrl", avatar != null ? avatar : "");

                    postRef.child("userLikes").child(myUid).setValue(likeData);
                    postRef.child("likes").setValue(item.likes + 1);
                });
            } else {
                postRef.child("userLikes").child(myUid).removeValue();
                postRef.child("likes").setValue(Math.max(0, item.likes - 1));
            }
        });

        // Click vào item → mở PostDetail tab Thích
        holder.itemView.setOnClickListener(v -> {
            if (postClickListener != null) postClickListener.onClick(item);
        });

        // Click vào nút bình luận → mở PostDetail tab Bình luận
        holder.btnComment.setOnClickListener(v -> {
            if (commentClickListener != null) commentClickListener.onClick(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPost, imgHeart, imgAvatar;
        TextView tvUserName, tvContent, tvLikeCount, tvCommentCount;
        LinearLayout btnLike, btnComment;

        PostViewHolder(View v) {
            super(v);
            imgPost        = v.findViewById(R.id.imgPost);
            imgHeart       = v.findViewById(R.id.imgHeart);
            imgAvatar      = v.findViewById(R.id.imgAvatar);
            tvUserName     = v.findViewById(R.id.tvUserName);
            tvContent      = v.findViewById(R.id.tvContent);
            tvLikeCount    = v.findViewById(R.id.tvLikeCount);
            tvCommentCount = v.findViewById(R.id.tvCommentCount);
            btnLike        = v.findViewById(R.id.btnLike);
            btnComment     = v.findViewById(R.id.layoutWriteComment);
        }
    }
}