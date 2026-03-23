package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.HoSoYTeDao;
import com.nhom08.petcare.data.model.HoSoYTe;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HoSoYTeRepository {

    private HoSoYTeDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public HoSoYTeRepository(Context context) {
        dao = AppDatabase.getInstance(context).hoSoYTeDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAll(String petId, Callback<List<HoSoYTe>> cb) {
        executor.execute(() -> cb.onResult(dao.getAllByPet(petId)));
    }

    public void getById(String id, Callback<HoSoYTe> cb) {
        executor.execute(() -> cb.onResult(dao.getById(id)));
    }

    public void add(HoSoYTe item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dao.insert(item);
            cb.onResult(null);
        });
    }

    public void update(HoSoYTe item, Callback<Void> cb) {
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