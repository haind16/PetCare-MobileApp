package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.TayGiun;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng tay_giun.
 * Quản lý các thao tác liên quan đến lịch sử tẩy giun của thú cưng.
 */
@Dao
public interface TayGiunDao {

    /**
     * Lấy toàn bộ danh sách các lần tẩy giun của một thú cưng, sắp xếp theo ngày mới nhất.
     */
    @Query("SELECT * FROM tay_giun WHERE petId = :petId ORDER BY ngay DESC")
    List<TayGiun> getAllByPet(String petId);

    /**
     * Thêm mới một bản ghi tẩy giun.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TayGiun item);

    /**
     * Xóa bản ghi tẩy giun theo ID.
     */
    @Query("DELETE FROM tay_giun WHERE id = :id")
    void deleteById(String id);
}