package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.DonThuoc;
import java.util.List;

@Dao
public interface DonThuocDao {

    @Query("SELECT * FROM don_thuoc WHERE petId = :petId")
    List<DonThuoc> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DonThuoc item);

    @Update
    void update(DonThuoc item);

    @Query("DELETE FROM don_thuoc WHERE id = :id")
    void deleteById(String id);
}