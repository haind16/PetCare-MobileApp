package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.NhatKy;
import java.util.List;

@Dao
public interface NhatKyDao {

    @Query("SELECT * FROM nhat_ky WHERE petId = :petId ORDER BY ngay DESC")
    List<NhatKy> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NhatKy item);

    @Query("DELETE FROM nhat_ky WHERE id = :id")
    void deleteById(String id);
}