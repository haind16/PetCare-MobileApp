package com.nhom08.petcare.data.repository;

import android.content.Context;
import com.nhom08.petcare.data.local.AppDatabase;
import com.nhom08.petcare.data.local.dao.*;
import com.nhom08.petcare.data.model.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repository quản lý thông tin chi tiết về sức khỏe của thú cưng.
 * Bao gồm: Tiêm phòng, Cân nặng, Tẩy giun, Dị ứng, Bệnh nền và Đơn thuốc.
 * Thao tác trực tiếp với Local Database (Room).
 */
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

    // ===== QUẢN LÝ TIÊM PHÒNG (VACCINE) =====
    
    /**
     * Lấy danh sách lịch tiêm phòng của thú cưng.
     */
    public void getVaccines(String petId,
                            Callback<List<LichTiemPhong>> cb) {
        executor.execute(() -> cb.onResult(
                vaccineDao.getAllByPet(petId)));
    }

    /**
     * Thêm bản ghi tiêm phòng mới.
     */
    public void addVaccine(LichTiemPhong item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            vaccineDao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa bản ghi tiêm phòng.
     */
    public void deleteVaccine(String id, Callback<Void> cb) {
        executor.execute(() -> {
            vaccineDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== QUẢN LÝ CÂN NẶNG =====
    
    /**
     * Lấy lịch sử cân nặng của thú cưng.
     */
    public void getWeights(String petId,
                           Callback<List<CanNang>> cb) {
        executor.execute(() -> cb.onResult(
                weightDao.getAllByPet(petId)));
    }

    /**
     * Thêm bản ghi cân nặng mới.
     */
    public void addWeight(CanNang item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            weightDao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa bản ghi cân nặng.
     */
    public void deleteWeight(String id, Callback<Void> cb) {
        executor.execute(() -> {
            weightDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== QUẢN LÝ TẨY GIUN =====
    
    /**
     * Lấy danh sách lịch sử tẩy giun.
     */
    public void getDewormings(String petId,
                              Callback<List<TayGiun>> cb) {
        executor.execute(() -> cb.onResult(
                dewormDao.getAllByPet(petId)));
    }

    /**
     * Thêm lịch tẩy giun mới.
     */
    public void addDeworming(TayGiun item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            dewormDao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa bản ghi tẩy giun.
     */
    public void deleteDeworming(String id, Callback<Void> cb) {
        executor.execute(() -> {
            dewormDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== QUẢN LÝ DỊ ỨNG =====
    
    /**
     * Lấy danh sách các tác nhân gây dị ứng.
     */
    public void getAllergies(String petId,
                             Callback<List<DiUng>> cb) {
        executor.execute(() -> cb.onResult(
                allergyDao.getAllByPet(petId)));
    }

    /**
     * Thêm thông tin dị ứng mới.
     */
    public void addAllergy(DiUng item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            allergyDao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa thông tin dị ứng.
     */
    public void deleteAllergy(String id, Callback<Void> cb) {
        executor.execute(() -> {
            allergyDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== QUẢN LÝ BỆNH NỀN =====
    
    /**
     * Lấy danh sách các bệnh nền của thú cưng.
     */
    public void getDiseases(String petId,
                            Callback<List<BenhNen>> cb) {
        executor.execute(() -> cb.onResult(
                diseaseDao.getAllByPet(petId)));
    }

    /**
     * Thêm thông tin bệnh nền mới.
     */
    public void addDisease(BenhNen item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            diseaseDao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa bản ghi bệnh nền.
     */
    public void deleteDisease(String id, Callback<Void> cb) {
        executor.execute(() -> {
            diseaseDao.deleteById(id);
            cb.onResult(null);
        });
    }

    // ===== QUẢN LÝ ĐƠN THUỐC =====
    
    /**
     * Lấy danh sách đơn thuốc.
     */
    public void getPrescriptions(String petId,
                                 Callback<List<DonThuoc>> cb) {
        executor.execute(() -> cb.onResult(
                prescriptionDao.getAllByPet(petId)));
    }

    /**
     * Lưu đơn thuốc mới.
     */
    public void addPrescription(DonThuoc item, Callback<Void> cb) {
        executor.execute(() -> {
            if (item.id == null || item.id.isEmpty())
                item.id = UUID.randomUUID().toString();
            prescriptionDao.insert(item);
            cb.onResult(null);
        });
    }

    /**
     * Xóa đơn thuốc.
     */
    public void deletePrescription(String id, Callback<Void> cb) {
        executor.execute(() -> {
            prescriptionDao.deleteById(id);
            cb.onResult(null);
        });
    }
}