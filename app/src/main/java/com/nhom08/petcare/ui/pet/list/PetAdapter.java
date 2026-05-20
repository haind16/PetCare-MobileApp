package com.nhom08.petcare.ui.pet.list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nhom08.petcare.R;
import com.nhom08.petcare.data.model.ThuCung;
import com.nhom08.petcare.ui.pet.profile.PetProfileActivity;
import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Adapter hiển thị danh sách thú cưng trong RecyclerView.
 * Hỗ trợ hiển thị thông tin tóm tắt (ảnh, tên, tuổi, giống) và các thao tác Chỉnh sửa, Xóa.
 */
public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private List<ThuCung> petList;
    private OnDeleteListener deleteListener;

    /**
     * Interface lắng nghe sự kiện xóa thú cưng.
     */
    public interface OnDeleteListener {
        void onDelete(ThuCung pet);
    }

    public PetAdapter(List<ThuCung> petList) {
        this.petList = petList;
    }

    public void setOnDeleteListener(OnDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        ThuCung pet = petList.get(position);
        holder.tvPetName.setText(pet.tenThuCung);
        holder.tvGender.setText(pet.gioiTinh);
        holder.tvBreed.setText(pet.giong);

        // Hiển thị tuổi được tính toán từ ngày sinh
        if (pet.ngaySinh != null && !pet.ngaySinh.isEmpty()) {
            holder.tvAge.setText(tinhTuoi(pet.ngaySinh));
        } else {
            holder.tvAge.setText("Chưa rõ");
        }

        // Tải ảnh thú cưng sử dụng Glide (hỗ trợ cả URL và Path nội bộ)
        if (pet.anhUrl != null && !pet.anhUrl.isEmpty()) {
            if (pet.anhUrl.startsWith("http")) {
                Glide.with(holder.itemView.getContext())
                        .load(pet.anhUrl)
                        .circleCrop()
                        .placeholder(R.drawable.pet_welcome)
                        .into(holder.imgPet);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(new File(pet.anhUrl))
                        .circleCrop()
                        .placeholder(R.drawable.pet_welcome)
                        .into(holder.imgPet);
            }
        } else {
            holder.imgPet.setImageResource(R.drawable.pet_welcome);
        }

        // Sự kiện khi nhấn Chỉnh sửa/Xem hồ sơ
        holder.btnEdit.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            ThuCung current = petList.get(pos);
            Intent intent = new Intent(v.getContext(), PetProfileActivity.class);
            intent.putExtra("pet_id", current.id);
            intent.putExtra("pet_name", current.tenThuCung);
            v.getContext().startActivity(intent);
        });

        // Sự kiện khi nhấn nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            ThuCung current = petList.get(pos);
            if (deleteListener != null) deleteListener.onDelete(current);
        });
    }

    @Override
    public int getItemCount() { return petList.size(); }

    /**
     * Hàm tính tuổi dựa trên ngày sinh (dd/MM/yyyy).
     */
    private String tinhTuoi(String ngaySinh) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate born = LocalDate.parse(ngaySinh, fmt);
            LocalDate now = LocalDate.now();

            if (born.isAfter(now)) {
                return "Chưa sinh";
            }

            Period period = Period.between(born, now);
            if (period.getYears() > 0) return period.getYears() + " tuổi";
            if (period.getMonths() > 0) return period.getMonths() + " tháng tuổi";
            if (period.getDays() > 0) return period.getDays() + " ngày tuổi";
            return "Mới sinh";
        } catch (Exception e) {
            return ngaySinh;
        }
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgPet;
        TextView tvPetName, tvGender, tvAge, tvBreed;
        Button btnEdit, btnDelete;

        PetViewHolder(View itemView) {
            super(itemView);
            imgPet    = itemView.findViewById(R.id.imgPet);
            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvGender  = itemView.findViewById(R.id.tvGender);
            tvAge     = itemView.findViewById(R.id.tvAge);
            tvBreed   = itemView.findViewById(R.id.tvBreed);
            btnEdit   = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}