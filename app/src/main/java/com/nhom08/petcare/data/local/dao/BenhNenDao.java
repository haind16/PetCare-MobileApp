package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.BenhNen;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng benh_nen.
 * Quản lý các thao tác truy vấn và lưu trữ danh sách bệnh lý nền của thú cưng.
 */
@Dao
public interface BenhNenDao {

    /**
     * Lấy danh sách bệnh nền của một thú cưng cụ thể.
     */
    @Query("SELECT * FROM benh_nen WHERE petId = :petId")
    List<BenhNen> getAllByPet(String petId);

    /**
     * Thêm mới một bệnh lý nền vào hồ sơ.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BenhNen item);

    /**
     * Xóa bản ghi bệnh nền theo mã định danh.
     */
    @Query("DELETE FROM benh_nen WHERE id = :id")
    void deleteById(String id);
}