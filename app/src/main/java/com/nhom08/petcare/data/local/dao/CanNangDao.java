package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.CanNang;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng can_nang.
 * Quản lý các thao tác truy vấn và lưu trữ lịch sử cân nặng của thú cưng.
 */
@Dao
public interface CanNangDao {

    /**
     * Lấy toàn bộ lịch sử cân nặng của một thú cưng, sắp xếp theo thời gian tăng dần.
     */
    @Query("SELECT * FROM can_nang WHERE petId = :petId ORDER BY ngay ASC")
    List<CanNang> getAllByPet(String petId);

    /**
     * Thêm mới một bản ghi cân nặng.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CanNang item);

    /**
     * Xóa bản ghi cân nặng theo ID.
     */
    @Query("DELETE FROM can_nang WHERE id = :id")
    void deleteById(String id);
}