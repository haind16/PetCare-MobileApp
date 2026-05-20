package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.ThuCungDao;
import com.nhom08.petcare.data.model.ThuCung;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository quản lý dữ liệu Thú cưng.
 * Thực hiện cơ chế Single Source of Truth: Dữ liệu được lưu ở Local (Room) và đồng bộ với Remote (Firebase).
 */
public class PetRepository {

    private static final String DB_URL = "https://petcare-1ce14-default-rtdb.asia-southeast1.firebasedatabase.app";

    private ThuCungDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    // Tham chiếu đến node "pets" trên Firebase Realtime Database
    private DatabaseReference petsRef =
            FirebaseDatabase.getInstance(DB_URL).getReference("pets");

    public PetRepository(Context context) {
        dao = AppDatabase.getInstance(context).thuCungDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    /**
     * Lấy danh sách thú cưng của người dùng từ cơ sở dữ liệu local (Room).
     */
    public void getAllPets(String userId, Callback<List<ThuCung>> callback) {
        executor.execute(() ->
                callback.onResult(dao.getAllByUser(userId)));
    }

    /**
     * Lấy thông tin chi tiết của một thú cưng theo ID từ Room.
     */
    public void getPetById(String petId, Callback<ThuCung> callback) {
        executor.execute(() ->
                callback.onResult(dao.getById(petId)));
    }

    /**
     * Thêm thú cưng mới.
     * Lưu vào Room Database trước để hỗ trợ offline, sau đó đẩy lên Firebase Realtime Database.
     */
    public void addPet(ThuCung pet, Callback<Void> callback) {
        if (pet.id == null || pet.id.isEmpty())
            pet.id = UUID.randomUUID().toString();

        // 1. Lưu vào Room (Local)
        executor.execute(() -> {
            dao.insert(pet);

            // 2. Đồng bộ lên Firebase (Remote)
            petsRef.child(pet.userId).child(pet.id)
                    .setValue(toFirebaseMap(pet))
                    .addOnSuccessListener(unused -> {})
                    .addOnFailureListener(e -> {
                        android.util.Log.e("PetRepository",
                                "Firebase addPet failed: " + e.getMessage());
                    });

            callback.onResult(null);
        });
    }

    /**
     * Cập nhật thông tin thú cưng ở cả local và remote.
     */
    public void updatePet(ThuCung pet, Callback<Void> callback) {
        executor.execute(() -> {
            dao.update(pet);

            petsRef.child(pet.userId).child(pet.id)
                    .setValue(toFirebaseMap(pet))
                    .addOnFailureListener(e ->
                            android.util.Log.e("PetRepository",
                                    "Firebase updatePet failed: " + e.getMessage()));

            callback.onResult(null);
        });
    }

    /**
     * Xóa thú cưng khỏi database local và remote.
     */
    public void deletePet(String petId, Callback<Void> callback) {
        executor.execute(() -> {
            ThuCung pet = dao.getById(petId);
            dao.deleteById(petId);

            if (pet != null && pet.userId != null) {
                petsRef.child(pet.userId).child(petId).removeValue()
                        .addOnFailureListener(e ->
                                android.util.Log.e("PetRepository",
                                        "Firebase deletePet failed: " + e.getMessage()));
            }

            callback.onResult(null);
        });
    }

    /**
     * Đồng bộ dữ liệu từ Firebase về Room.
     * Thường dùng khi người dùng đăng nhập lần đầu trên thiết bị mới.
     */
    public void syncFromFirebase(String userId, Callback<Void> callback) {
        petsRef.child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<ThuCung> pets = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ThuCung pet = child.getValue(ThuCung.class);
                            if (pet != null) pets.add(pet);
                        }
                        if (!pets.isEmpty()) {
                            executor.execute(() -> {
                                for (ThuCung pet : pets) dao.insert(pet);
                                if (callback != null) callback.onResult(null);
                            });
                        } else {
                            if (callback != null) callback.onResult(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        android.util.Log.e("PetRepository",
                                "Firebase sync failed: " + error.getMessage());
                        if (callback != null) callback.onResult(null);
                    }
                });
    }

    /**
     * Chuyển đổi đối tượng ThuCung sang Map để lưu trữ trên Firebase.
     * Lưu ý: Không lưu đường dẫn ảnh local lên Firebase vì nó không có ý nghĩa trên thiết bị khác.
     */
    private java.util.Map<String, Object> toFirebaseMap(ThuCung pet) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("id",         pet.id);
        map.put("userId",     pet.userId);
        map.put("tenThuCung", pet.tenThuCung);
        map.put("loai",       pet.loai);
        map.put("giong",      pet.giong);
        map.put("gioiTinh",   pet.gioiTinh);
        map.put("ngaySinh",   pet.ngaySinh);
        map.put("canNang",    pet.canNang);
        if (pet.anhUrl != null && pet.anhUrl.startsWith("http")) {
            map.put("anhUrl", pet.anhUrl);
        }
        return map;
    }
}