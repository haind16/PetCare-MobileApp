package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.DonThuoc;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng don_thuoc.
 * Quản lý các thao tác truy vấn và lưu trữ danh sách các loại thuốc thú cưng đang sử dụng.
 */
@Dao
public interface DonThuocDao {

    /**
     * Lấy danh sách đơn thuốc của một thú cưng theo ID.
     */
    @Query("SELECT * FROM don_thuoc WHERE petId = :petId")
    List<DonThuoc> getAllByPet(String petId);

    /**
     * Thêm mới hoặc cập nhật thông tin thuốc.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DonThuoc item);

    /**
     * Cập nhật thông tin đơn thuốc.
     */
    @Update
    void update(DonThuoc item);

    /**
     * Xóa thuốc khỏi danh sách theo ID.
     */
    @Query("DELETE FROM don_thuoc WHERE id = :id")
    void deleteById(String id);
}