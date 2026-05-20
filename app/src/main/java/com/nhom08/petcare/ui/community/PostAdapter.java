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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Adapter quản lý việc hiển thị danh sách bài viết trong cộng đồng.
 * Xử lý việc hiển thị nội dung bài viết, hình ảnh, thời gian đăng, 
 * và các tương tác như Like, xem bình luận.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    /**
     * Model đại diện cho một bài đăng.
     */
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
        holder.tvTime.setText(formatTime(item.timestamp));

        // Tải ảnh đại diện người đăng
        if (item.avatarUrl != null && !item.avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.avatarUrl)
                    .placeholder(R.drawable.pet_welcome)
                    .circleCrop()
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.pet_welcome);
        }

        // Tải hình ảnh bài đăng (Hỗ trợ URL Cloudinary và ảnh Local)
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Object imageSource = item.imageUrl.startsWith("http")
                    ? item.imageUrl
                    : new File(item.imageUrl);
            Glide.with(holder.itemView.getContext())
                    .load(imageSource)
                    .into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        // Lắng nghe và cập nhật trạng thái Like của bài viết theo thời gian thực
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

        // Xử lý sự kiện khi nhấn nút Like
        holder.btnLike.setOnClickListener(v -> {
            if (!item.isLiked) {
                db.child("users").child(myUid).get().addOnSuccessListener(s -> {
                    String name   = s.child("displayName").getValue(String.class);
                    String avatar = s.child("avatarUrl").getValue(String.class);

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

        // Mở chi tiết bài viết
        holder.itemView.setOnClickListener(v -> {
            if (postClickListener != null) postClickListener.onClick(item);
        });

        // Chuyển trực tiếp đến phần bình luận
        holder.btnComment.setOnClickListener(v -> {
            if (commentClickListener != null) commentClickListener.onClick(item);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPost, imgHeart, imgAvatar;
        TextView tvUserName, tvContent, tvLikeCount, tvCommentCount, tvTime;
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
            tvTime         = v.findViewById(R.id.tvTime);
            btnLike        = v.findViewById(R.id.btnLike);
            btnComment     = v.findViewById(R.id.layoutWriteComment);
        }
    }

    /**
     * Chuyển đổi timestamp sang chuỗi thời gian thân thiện (vừa xong, phút trước, ngày trước...).
     */
    private String formatTime(long timestamp) {
        if (timestamp == 0) return "";
        long now  = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60_000)                return "Vừa xong";
        if (diff < 3_600_000)             return (diff / 60_000) + " phút trước";
        if (diff < 86_400_000)            return (diff / 3_600_000) + " giờ trước";
        if (diff < 7 * 86_400_000L)       return (diff / 86_400_000) + " ngày trước";

        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(timestamp));
    }
}