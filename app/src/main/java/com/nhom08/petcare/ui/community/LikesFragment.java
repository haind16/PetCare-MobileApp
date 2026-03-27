package com.nhom08.petcare.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import com.nhom08.petcare.R;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    private static class LikeItem {
        String name, avatarUrl;
    }

    private final List<LikeItem> list = new ArrayList<>();
    private RecyclerView.Adapter<?> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        RecyclerView rv = view.findViewById(R.id.rvLikes);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(true);

        String postId = getArguments() != null ? getArguments().getString("postId") : null;
        if (postId == null) return view;

        DatabaseReference likesRef = FirebaseDatabase.getInstance(
                "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("posts").child(postId).child("userLikes");

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_like, parent, false);
                return new RecyclerView.ViewHolder(v) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
                LikeItem item = list.get(position);
                ((TextView) h.itemView.findViewById(R.id.tvUserName)).setText(item.name);

                // Load avatar
                de.hdodenhof.circleimageview.CircleImageView imgAvatar =
                        h.itemView.findViewById(R.id.imgAvatar);
                if (item.avatarUrl != null && !item.avatarUrl.isEmpty()) {
                    Glide.with(h.itemView.getContext())
                            .load(item.avatarUrl)
                            .placeholder(R.drawable.pet_welcome)
                            .circleCrop()
                            .into(imgAvatar);
                } else {
                    imgAvatar.setImageResource(R.drawable.pet_welcome);
                }
            }

            @Override
            public int getItemCount() { return list.size(); }
        };
        rv.setAdapter(adapter);

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    LikeItem item = new LikeItem();
                    // Hỗ trợ cả dữ liệu cũ (String) và mới (Object {name, avatarUrl})
                    if (d.hasChildren()) {
                        item.name      = d.child("name").getValue(String.class);
                        item.avatarUrl = d.child("avatarUrl").getValue(String.class);
                    } else {
                        item.name      = d.getValue(String.class); // dữ liệu cũ
                        item.avatarUrl = "";
                    }
                    if (item.name != null) list.add(item);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        return view;
    }
}