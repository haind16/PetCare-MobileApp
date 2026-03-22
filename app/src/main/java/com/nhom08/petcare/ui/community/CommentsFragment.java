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
import com.nhom08.petcare.databinding.FragmentCommentsBinding;
import com.nhom08.petcare.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentsFragment extends Fragment {

    private FragmentCommentsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCommentsBinding.inflate(inflater, container, false);

        List<String[]> comments = new ArrayList<>(Arrays.asList(
                new String[]{"Ngọc Linh", "Milu bị sao thế?"},
                new String[]{"Hải Nam", "Tiêm thôi quá"},
                new String[]{"Hương Giang", "Chúc Milu đau ít"},
                new String[]{"Hà My", "Hi vọng Milu mau khỏi"},
                new String[]{"Tuấn Tài", "Milu bị ốm hả?"}
        ));

        binding.rvComments.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        RecyclerView.Adapter adapter =
                new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(
                            @NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_comment, parent, false);
                        return new RecyclerView.ViewHolder(view) {};
                    }

                    @Override
                    public void onBindViewHolder(
                            @NonNull RecyclerView.ViewHolder holder, int position) {
                        String[] comment = comments.get(position);
                        TextView tvName = holder.itemView.findViewById(R.id.tvUserName);
                        TextView tvComment = holder.itemView.findViewById(R.id.tvComment);
                        tvName.setText(comment[0]);
                        tvComment.setText(comment[1]);
                    }

                    @Override
                    public int getItemCount() { return comments.size(); }
                };

        binding.rvComments.setAdapter(adapter);

        // Gửi bình luận
        binding.btnSendComment.setOnClickListener(v -> {
            String text = binding.etComment.getText().toString().trim();
            if (!text.isEmpty()) {
                comments.add(new String[]{"Bạn", text});
                adapter.notifyItemInserted(comments.size() - 1);
                binding.rvComments.scrollToPosition(comments.size() - 1);
                binding.etComment.setText("");
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}