package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.*;
import com.nhom08.petcare.data.model.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetHealthRepository {

    private LichTiemPhongDao vaccineDao;
    private CanNangDao weightDao;
    private TayGiunDao dewormDao;
    private DiUngDao allergyDao;
    private BenhNenDao diseaseDao;
    private DonThuocDao prescriptionDao;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public PetHealthRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        vaccineDao     = db.lichTiemPhongDao();
        weightDao      = db.canNangDao();
        dewormDao      = db.tayGiunDao();
        allergyDao     = db.diUngDao();
        diseaseDao     = db.benhNenDao();
        prescriptionDao = db.donThuocDao();
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    // ===== VACCINE =====
    public void getVaccines(String petId,
                            Callback<List<LichTiemPhong>> cb) {
        executor.execute(() -> cb.onResult(
                vaccineDao.getAllByPet(petId)));
    }

    public void addVaccine(LichTiemPhong item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            vaccineDao.insert(item);
            cb.onResult(null);
        });
    }

    public void deleteVaccine(String id, Callback<Void> cb) {
        executor.execute(() -> {
            vaccineDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== CÂN NẶNG =====
    public void getWeights(String petId,
                           Callback<List<CanNang>> cb) {
        executor.execute(() -> cb.onResult(
                weightDao.getAllByPet(petId)));
    }

    public void addWeight(CanNang item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            weightDao.insert(item);
            cb.onResult(null);
        });
    }

    public void deleteWeight(String id, Callback<Void> cb) {
        executor.execute(() -> {
            weightDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== TẨY GIUN =====
    public void getDewormings(String petId,
                              Callback<List<TayGiun>> cb) {
        executor.execute(() -> cb.onResult(
                dewormDao.getAllByPet(petId)));
    }

    public void addDeworming(TayGiun item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dewormDao.insert(item);
            cb.onResult(null);
        });
    }

    public void deleteDeworming(String id, Callback<Void> cb) {
        executor.execute(() -> {
            dewormDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== DỊ ỨNG =====
    public void getAllergies(String petId,
                             Callback<List<DiUng>> cb) {
        executor.execute(() -> cb.onResult(
                allergyDao.getAllByPet(petId)));
    }

    public void addAllergy(DiUng item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            allergyDao.insert(item);
            cb.onResult(null);
        });
    }

    public void deleteAllergy(String id, Callback<Void> cb) {
        executor.execute(() -> {
            allergyDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== BỆNH NỀN =====
    public void getDiseases(String petId,
                            Callback<List<BenhNen>> cb) {
        executor.execute(() -> cb.onResult(
                diseaseDao.getAllByPet(petId)));
    }

    public void addDisease(BenhNen item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            diseaseDao.insert(item);
            cb.onResult(null);
        });
    }

    public void deleteDisease(String id, Callback<Void> cb) {
        executor.execute(() -> {
            diseaseDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== ĐƠN THUỐC =====
    public void getPrescriptions(String petId,
                                 Callback<List<DonThuoc>> cb) {
        executor.execute(() -> cb.onResult(
                prescriptionDao.getAllByPet(petId)));
    }

    public void addPrescription(DonThuoc item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            prescriptionDao.insert(item);
            cb.onResult(null);
        });
    }

    public void deletePrescription(String id, Callback<Void> cb) {
        executor.execute(() -> {
            prescriptionDao.deleteById(id);
            cb.onResult(null);
        });
    }
}