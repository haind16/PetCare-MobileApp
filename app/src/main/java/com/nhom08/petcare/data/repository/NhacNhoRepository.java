package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.NhacNhoDao;
import com.nhom08.petcare.data.model.NhacNho;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NhacNhoRepository {

    private NhacNhoDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public NhacNhoRepository(Context context) {
        dao = AppDatabase.getInstance(context).nhacNhoDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAll(String petId, Callback<List<NhacNho>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllByPet(petId)));
    }

    public void getChuaHoanThanh(String petId,
                                 Callback<List<NhacNho>> cb) {
        executor.execute(() -> cb.onResult(
                dao.getChuaHoanThanh(petId)));
    }

    public void add(NhacNho item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dao.insert(item);
            cb.onResult(null);
        });
    }

    public void update(NhacNho item, Callback<Void> cb) {
        executor.execute(() -> {
            dao.update(item);
            cb.onResult(null);
        });
    }

    public void delete(String id, Callback<Void> cb) {
        executor.execute(() -> {
            dao.deleteById(id);
            cb.onResult(null);
        });
    }
}