package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.NhatKy;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng nhat_ky.
 * Cung cấp các phương thức để thao tác với dữ liệu nhật ký hoạt động của thú cưng trong Room Database.
 */
@Dao
public interface NhatKyDao {

    /**
     * Lấy toàn bộ danh sách nhật ký của một thú cưng, sắp xếp theo ngày mới nhất.
     */
    @Query("SELECT * FROM nhat_ky WHERE petId = :petId ORDER BY ngay DESC")
    List<NhatKy> getAllByPet(String petId);

    /**
     * Thêm mới hoặc cập nhật một bản ghi nhật ký.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NhatKy item);

    /**
     * Xóa một bản ghi nhật ký theo mã định danh.
     */
    @Query("DELETE FROM nhat_ky WHERE id = :id")
    void deleteById(String id);
}