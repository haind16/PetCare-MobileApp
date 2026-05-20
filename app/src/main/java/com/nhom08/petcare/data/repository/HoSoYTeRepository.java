package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.HoSoYTeDao;
import com.nhom08.petcare.data.model.HoSoYTe;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository quản lý Hồ sơ y tế của thú cưng.
 * Lưu trữ các thông tin như đơn thuốc, phiếu khám, kết quả xét nghiệm dưới dạng hồ sơ điện tử trong Room Database.
 */
public class HoSoYTeRepository {

    private HoSoYTeDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public HoSoYTeRepository(Context context) {
        dao = AppDatabase.getInstance(context).hoSoYTeDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    /**
     * Lấy toàn bộ hồ sơ y tế của một thú cưng.
     */
    public void getAll(String petId, Callback<List<HoSoYTe>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllByPet(petId)));
    }

    /**
     * Lấy chi tiết một hồ sơ y tế theo ID.
     */
    public void getById(String id, Callback<HoSoYTe> cb) {
        executor.execute(() -> cb.onResult(dao.getById(id)));
    }

    /**
     * Thêm mới hồ sơ y tế.
     */
    public void add(HoSoYTe item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Cập nhật thông tin hồ sơ y tế.
     */
    public void update(HoSoYTe item, Callback<Void> cb) {
        executor.execute(() -> {
            dao.update(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa hồ sơ y tế.
     */
    public void delete(String id, Callback<Void> cb) {
        executor.execute(() -> {
            dao.deleteById(id);
            cb.onResult(null);
        });
    }
}