package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.CanNang;
import java.util.List;

@Dao
public interface CanNangDao {

    @Query("SELECT * FROM can_nang WHERE petId = :petId ORDER BY ngay ASC")
    List<CanNang> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CanNang item);

    @Query("DELETE FROM can_nang WHERE id = :id")
    void deleteById(String id);
}