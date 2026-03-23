package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.ThuCungDao;
import com.nhom08.petcare.data.model.ThuCung;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetRepository {

    private ThuCungDao dao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public PetRepository(Context context) {
        dao = AppDatabase.getInstance(context).thuCungDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    public void getAllPets(String userId, Callback<List<ThuCung>> callback) {
        executor.execute(() ->
                callback.onResult(dao.getAllByUser(userId)));
    }

    public void getPetById(String petId, Callback<ThuCung> callback) {
        executor.execute(() ->
                callback.onResult(dao.getById(petId)));
    }

    public void addPet(ThuCung pet, Callback<Void> callback) {
        executor.execute(() -> {
            if (pet.id == null || pet.id.isEmpty())
                pet.id = UUID.randomUUID().toString();
            dao.insert(pet);
            callback.onResult(null);
        });
    }

    public void updatePet(ThuCung pet, Callback<Void> callback) {
        executor.execute(() -> {
            dao.update(pet);
            callback.onResult(null);
        });
    }

    public void deletePet(String petId, Callback<Void> callback) {
        executor.execute(() -> {
            dao.deleteById(petId);
            callback.onResult(null);
        });
    }
}