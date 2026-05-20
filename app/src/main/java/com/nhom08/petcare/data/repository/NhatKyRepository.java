package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.NhatKyDao;
import com.nhom08.petcare.data.model.NhatKy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository quản lý nhật ký hoạt động của thú cưng.
 * Cung cấp các phương thức để lưu trữ và truy vấn các hoạt động hàng ngày (ăn uống, vui chơi, sức khỏe).
 */
public class NhatKyRepository {

    private NhatKyDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public NhatKyRepository(Context context) {
        dao = AppDatabase.getInstance(context).nhatKyDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    /**
     * Lấy toàn bộ danh sách nhật ký của một thú cưng.
     */
    public void getAll(String petId, Callback<List<NhatKy>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllByPet(petId)));
    }

    /**
     * Thêm một bản ghi nhật ký mới.
     */
    public void add(NhatKy item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa bản ghi nhật ký theo ID.
     */
    public void delete(String id, Callback<Void> cb) {
        executor.execute(() -> {
            dao.deleteById(id);
            cb.onResult(null);
        });
    }
}