package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.NhacNho;
import java.util.List;

@Dao
public interface NhacNhoDao {

    @Query("SELECT * FROM nhac_nho WHERE petId = :petId")
    List<NhacNho> getAllByPet(String petId);

    @Query("SELECT * FROM nhac_nho WHERE petId = :petId AND daHoanThanh = 0")
    List<NhacNho> getChuaHoanThanh(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NhacNho item);

    @Update
    void update(NhacNho item);

    @Query("DELETE FROM nhac_nho WHERE id = :id")
    void deleteById(String id);
}