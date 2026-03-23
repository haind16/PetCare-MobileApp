package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.NhatKyDao;
import com.nhom08.petcare.data.model.NhatKy;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NhatKyRepository {

    private NhatKyDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public NhatKyRepository(Context context) {
        dao = AppDatabase.getInstance(context).nhatKyDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAll(String petId, Callback<List<NhatKy>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllByPet(petId)));
    }

    public void add(NhatKy item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dao.insert(item);
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