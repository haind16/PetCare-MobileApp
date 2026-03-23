package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.LichTiemPhong;
import java.util.List;

@Dao
public interface LichTiemPhongDao {

    @Query("SELECT * FROM lich_tiem_phong WHERE petId = :petId")
    List<LichTiemPhong> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LichTiemPhong item);

    @Update
    void update(LichTiemPhong item);

    @Query("DELETE FROM lich_tiem_phong WHERE id = :id")
    void deleteById(String id);
}