package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.NhacNho;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng nhac_nho.
 * Quản lý các truy vấn liên quan đến nhắc nhở chăm sóc thú cưng.
 */
@Dao
public interface NhacNhoDao {

    /**
     * Lấy toàn bộ danh sách nhắc nhở của một thú cưng dựa trên petId.
     */
    @Query("SELECT * FROM nhac_nho WHERE petId = :petId")
    List<NhacNho> getAllByPet(String petId);

    /**
     * Lấy danh sách các nhắc nhở chưa hoàn thành của một thú cưng.
     */
    @Query("SELECT * FROM nhac_nho WHERE petId = :petId AND daHoanThanh = 0")
    List<NhacNho> getChuaHoanThanh(String petId);

    /**
     * Thêm mới hoặc cập nhật (nếu trùng ID) một nhắc nhở.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NhacNho item);

    /**
     * Cập nhật thông tin nhắc nhở.
     */
    @Update
    void update(NhacNho item);

    /**
     * Xóa nhắc nhở theo ID.
     */
    @Query("DELETE FROM nhac_nho WHERE id = :id")
    void deleteById(String id);
}