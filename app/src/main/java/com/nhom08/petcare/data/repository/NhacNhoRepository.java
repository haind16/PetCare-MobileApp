package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.NhacNhoDao;
import com.nhom08.petcare.data.model.NhacNho;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository quản lý các nhắc nhở chăm sóc thú cưng.
 * Thực hiện các thao tác CRUD (Thêm, Sửa, Xóa, Lấy danh sách) trên bảng nhac_nho trong Room Database.
 */
public class NhacNhoRepository {

    private NhacNhoDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public NhacNhoRepository(Context context) {
        dao = AppDatabase.getInstance(context).nhacNhoDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    /**
     * Lấy toàn bộ danh sách nhắc nhở của một thú cưng.
     */
    public void getAll(String petId, Callback<List<NhacNho>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllByPet(petId)));
    }

    /**
     * Lấy danh sách các nhắc nhở chưa được đánh dấu là hoàn thành.
     */
    public void getChuaHoanThanh(String petId,
                                 Callback<List<NhacNho>> cb) {
        executor.execute(() -> cb.onResult(
                dao.getChuaHoanThanh(petId)));
    }

    /**
     * Thêm mới một nhắc nhở chăm sóc.
     */
    public void add(NhacNho item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Cập nhật trạng thái hoặc thông tin nhắc nhở.
     */
    public void update(NhacNho item, Callback<Void> cb) {
        executor.execute(() -> {
            dao.update(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa nhắc nhở theo ID.
     */
    public void delete(String id, Callback<Void> cb) {
        executor.execute(() -> {
            dao.deleteById(id);
            cb.onResult(null);
        });
    }
}