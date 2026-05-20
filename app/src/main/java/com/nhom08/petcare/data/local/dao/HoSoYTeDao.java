package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.HoSoYTe;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng ho_so_y_te.
 * Quản lý các thao tác truy vấn và lưu trữ hồ sơ y tế của thú cưng.
 */
@Dao
public interface HoSoYTeDao {

    /**
     * Lấy toàn bộ hồ sơ y tế của một thú cưng, sắp xếp theo ngày khám mới nhất lên đầu.
     */
    @Query("SELECT * FROM ho_so_y_te WHERE petId = :petId ORDER BY ngayKham DESC")
    List<HoSoYTe> getAllByPet(String petId);

    /**
     * Lấy chi tiết một hồ sơ y tế theo mã định danh.
     */
    @Query("SELECT * FROM ho_so_y_te WHERE id = :id")
    HoSoYTe getById(String id);

    /**
     * Thêm mới hoặc ghi đè hồ sơ y tế.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HoSoYTe item);

    /**
     * Cập nhật thông tin hồ sơ y tế đã tồn tại.
     */
    @Update
    void update(HoSoYTe item);

    /**
     * Xóa hồ sơ y tế theo ID.
     */
    @Query("DELETE FROM ho_so_y_te WHERE id = :id")
    void deleteById(String id);
}