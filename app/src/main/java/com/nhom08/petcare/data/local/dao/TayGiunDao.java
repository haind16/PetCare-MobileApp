package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.TayGiun;
import java.util.List;

@Dao
public interface TayGiunDao {

    @Query("SELECT * FROM tay_giun WHERE petId = :petId ORDER BY ngay DESC")
    List<TayGiun> getAllByPet(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TayGiun item);

    @Query("DELETE FROM tay_giun WHERE id = :id")
    void deleteById(String id);
}