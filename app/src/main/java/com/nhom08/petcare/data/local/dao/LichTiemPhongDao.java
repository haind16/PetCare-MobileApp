package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.LichTiemPhong;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng lich_tiem_phong.
 * Thực hiện các thao tác truy vấn liên quan đến lịch tiêm chủng vắc-xin của thú cưng.
 */
@Dao
public interface LichTiemPhongDao {

    /**
     * Lấy toàn bộ lịch sử tiêm phòng của một thú cưng theo petId.
     */
    @Query("SELECT * FROM lich_tiem_phong WHERE petId = :petId")
    List<LichTiemPhong> getAllByPet(String petId);

    /**
     * Thêm mới hoặc cập nhật một bản ghi tiêm phòng.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LichTiemPhong item);

    /**
     * Cập nhật thông tin lịch tiêm phòng.
     */
    @Update
    void update(LichTiemPhong item);

    /**
     * Xóa bản ghi lịch tiêm phòng theo mã định danh.
     */
    @Query("DELETE FROM lich_tiem_phong WHERE id = :id")
    void deleteById(String id);
}