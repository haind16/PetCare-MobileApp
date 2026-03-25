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
import com.google.firebase.database.*;
import com.nhom08.petcare.R;
import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {
    private List<String> names = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        RecyclerView rv = view.findViewById(R.id.rvLikes);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(true);

        String postId = getArguments() != null ? getArguments().getString("postId") : "post1";
        DatabaseReference likesRef = FirebaseDatabase.getInstance("https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("posts").child(postId).child("userLikes");

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
                return new RecyclerView.ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_like, p, false)) {};
            }
            @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int p) {
                ((TextView)h.itemView.findViewById(R.id.tvUserName)).setText(names.get(p));
            }
            @Override public int getItemCount() { return names.size(); }
        };
        rv.setAdapter(adapter);

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                names.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    String n = d.getValue(String.class); // Lấy tên thật đã lưu
                    if (n != null) names.add(n);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        return view;
    }
}