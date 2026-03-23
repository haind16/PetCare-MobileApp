package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.HoSoYTe;
import java.util.List;

@Dao
public interface HoSoYTeDao {

    @Query("SELECT * FROM ho_so_y_te WHERE petId = :petId ORDER BY ngayKham DESC")
    List<HoSoYTe> getAllByPet(String petId);

    @Query("SELECT * FROM ho_so_y_te WHERE id = :id")
    HoSoYTe getById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HoSoYTe item);

    @Update
    void update(HoSoYTe item);

    @Query("DELETE FROM ho_so_y_te WHERE id = :id")
    void deleteById(String id);
}