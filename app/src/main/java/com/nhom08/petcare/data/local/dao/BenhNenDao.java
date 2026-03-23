package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.BenhNen;
import java.util.List;

@Dao
public interface BenhNenDao {

    @Query("SELECT * FROM benh_nen WHERE petId = :petId")
    List<BenhNen> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BenhNen item);

    @Query("DELETE FROM benh_nen WHERE id = :id")
    void deleteById(String id);
}