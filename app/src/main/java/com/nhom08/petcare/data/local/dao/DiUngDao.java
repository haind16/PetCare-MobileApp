package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.DiUng;
import java.util.List;

@Dao
public interface DiUngDao {

    @Query("SELECT * FROM di_ung WHERE petId = :petId")
    List<DiUng> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DiUng item);

    @Query("DELETE FROM di_ung WHERE id = :id")
    void deleteById(String id);
}