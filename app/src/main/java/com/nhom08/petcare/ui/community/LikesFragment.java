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
import com.nhom08.petcare.R;
import java.util.Arrays;
import java.util.List;

public class LikesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RecyclerView rv = new RecyclerView(requireContext());
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        List<String> names = Arrays.asList(
                "Hà My", "Hải Nam", "Hương Giang",
                "Nguyễn Kiên", "Ngọc Linh", "Tuấn Tài");

        rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(
                    @NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_like, parent, false);
                return new RecyclerView.ViewHolder(view) {};
            }

            @Override
            public void onBindViewHolder(
                    @NonNull RecyclerView.ViewHolder holder, int position) {
                TextView tvName = holder.itemView.findViewById(R.id.tvUserName);
                tvName.setText(names.get(position));
            }

            @Override
            public int getItemCount() { return names.size(); }
        });

        return rv;
    }
}