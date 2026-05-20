package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.DiUng;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng di_ung.
 * Quản lý các thao tác liên quan đến danh sách các tác nhân gây dị ứng cho thú cưng.
 */
@Dao
public interface DiUngDao {

    /**
     * Lấy danh sách dị ứng của một thú cưng theo petId.
     */
    @Query("SELECT * FROM di_ung WHERE petId = :petId")
    List<DiUng> getAllByPet(String petId);

    /**
     * Thêm mới một tác nhân gây dị ứng.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiUng item);

    /**
     * Xóa bản ghi dị ứng theo mã định danh.
     */
    @Query("DELETE FROM di_ung WHERE id = :id")
    void deleteById(String id);
}