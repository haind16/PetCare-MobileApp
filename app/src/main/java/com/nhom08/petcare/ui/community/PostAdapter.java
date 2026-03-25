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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static class PostItem {
        public String postId, userName, content, imageUrl;
        public long timestamp, likes, comments_count; // Dùng comments_count
        public boolean isLiked = false;
        public PostItem() {}
    }

    private List<PostItem> list;
    private OnPostClickListener listener;
    private String myUid = FirebaseAuth.getInstance().getUid();
    private DatabaseReference db = FirebaseDatabase.getInstance("https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    public interface OnPostClickListener { void onClick(PostItem post); }

    public PostAdapter(List<PostItem> list, OnPostClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        PostItem item = list.get(position);
        holder.tvUserName.setText(item.userName);
        holder.tvContent.setText(item.content);
        holder.tvLikeCount.setText(String.valueOf(item.likes));
        holder.tvCommentCount.setText(String.valueOf(item.comments_count));

        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            holder.imgPost.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext()).load(new File(item.imageUrl)).into(holder.imgPost);
        } else {
            holder.imgPost.setVisibility(View.GONE);
        }

        DatabaseReference postRef = db.child("posts").child(item.postId);
        postRef.child("userLikes").child(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                item.isLiked = s.exists();
                int color = item.isLiked ? android.graphics.Color.parseColor("#FF5722") : android.graphics.Color.parseColor("#BDBDBD");
                holder.imgHeart.setColorFilter(color);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        holder.btnLike.setOnClickListener(v -> {
            if (!item.isLiked) {
                db.child("users").child(myUid).get().addOnSuccessListener(s -> {
                    String name = s.child("displayName").getValue(String.class);
                    postRef.child("userLikes").child(myUid).setValue(name != null ? name : "Thành viên");
                    postRef.child("likes").setValue(item.likes + 1);
                });
            } else {
                postRef.child("userLikes").child(myUid).removeValue();
                postRef.child("likes").setValue(Math.max(0, item.likes - 1));
            }
        });

        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(item); });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPost, imgHeart;
        TextView tvUserName, tvContent, tvLikeCount, tvCommentCount;
        LinearLayout btnLike;
        PostViewHolder(View v) {
            super(v);
            imgPost = v.findViewById(R.id.imgPost);
            imgHeart = v.findViewById(R.id.imgHeart);
            tvUserName = v.findViewById(R.id.tvUserName);
            tvContent = v.findViewById(R.id.tvContent);
            tvLikeCount = v.findViewById(R.id.tvLikeCount);
            tvCommentCount = v.findViewById(R.id.tvCommentCount);
            btnLike = v.findViewById(R.id.btnLike);
        }
    }
}