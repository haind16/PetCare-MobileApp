package com.nhom08.petcare.ui.community;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nhom08.petcare.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class PostAdapter extends
        RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    public static class PostItem {
        public String userName, time, content;
        public int likes, comments;
        public boolean isLiked = false;

        public PostItem(String userName, String time,
                        String content, int likes, int comments) {
            this.userName = userName;
            this.time = time;
            this.content = content;
            this.likes = likes;
            this.comments = comments;
        }
    }

    public interface OnPostClickListener {
        void onClick(PostItem post);
    }

    private List<PostItem> list;
    private OnPostClickListener listener;

    public PostAdapter(List<PostItem> list, OnPostClickListener listener) {
        this.list = list;
        this.listener = listener;
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
        holder.tvTime.setText(item.time);
        holder.tvContent.setText(item.content);
        holder.tvLikeCount.setText(String.valueOf(item.likes));
        holder.tvCommentCount.setText(String.valueOf(item.comments));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        updateLikeButton(holder, item);

        holder.btnLike.setOnClickListener(v -> {
            item.isLiked = !item.isLiked;
            if (item.isLiked) {
                item.likes++;
            } else {
                item.likes--;
            }
            holder.tvLikeCount.setText(String.valueOf(item.likes));
            updateLikeButton(holder, item);
        });

        holder.btnComment.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });

        holder.layoutWriteComment.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    private void updateLikeButton(PostViewHolder holder, PostItem item) {
        if (item.isLiked) {
            holder.imgHeart.setColorFilter(
                    android.graphics.Color.parseColor("#FF5722"));
            holder.tvLikeCount.setTextColor(
                    android.graphics.Color.parseColor("#FF5722"));
        } else {
            holder.imgHeart.setColorFilter(
                    android.graphics.Color.parseColor("#BDBDBD"));
            holder.tvLikeCount.setTextColor(
                    android.graphics.Color.parseColor("#888888"));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        ImageView imgHeart;
        TextView tvUserName, tvTime, tvContent;
        TextView tvLikeCount, tvCommentCount;
        LinearLayout btnLike, btnComment, layoutWriteComment;

        PostViewHolder(View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnComment = itemView.findViewById(R.id.btnComment);
            layoutWriteComment = itemView.findViewById(R.id.layoutWriteComment);
        }
    }
}