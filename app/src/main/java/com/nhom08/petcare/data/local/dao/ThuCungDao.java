package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.ThuCung;
import java.util.List;

/**
 * Data Access Object (DAO) cho bảng thu_cung.
 * Cung cấp các phương thức truy vấn, thêm, sửa, xóa thông tin thú cưng trong Room Database.
 */
@Dao
public interface ThuCungDao {

    /**
     * Lấy tất cả thú cưng của một người dùng cụ thể.
     * @param userId ID người dùng từ Firebase
     * @return Danh sách thú cưng
     */
    @Query("SELECT * FROM thu_cung WHERE userId = :userId")
    List<ThuCung> getAllByUser(String userId);

    /**
     * Lấy thông tin thú cưng theo ID.
     */
    @Query("SELECT * FROM thu_cung WHERE id = :petId")
    ThuCung getById(String petId);

    /**
     * Thêm hoặc thay thế thông tin thú cưng.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ThuCung item);

    /**
     * Cập nhật thông tin thú cưng đã tồn tại.
     */
    @Update
    void update(ThuCung item);

    /**
     * Xóa thú cưng theo ID.
     */
    @Query("DELETE FROM thu_cung WHERE id = :id")
    void deleteById(String id);

    /**
     * Cập nhật nhanh cân nặng của thú cưng.
     */
    @Query("UPDATE thu_cung SET canNang = :canNang WHERE id = :petId")
    void updateCanNang(String petId, float canNang);
}