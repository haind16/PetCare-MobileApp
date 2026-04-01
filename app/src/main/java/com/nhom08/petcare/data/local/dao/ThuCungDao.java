package com.nhom08.petcare.data.local.dao;

import androidx.room.*;
import com.nhom08.petcare.data.model.ThuCung;
import java.util.List;

@Dao
public interface ThuCungDao {

    @Query("SELECT * FROM thu_cung WHERE userId = :userId")
    List<ThuCung> getAllByUser(String userId);

    @Query("SELECT * FROM thu_cung WHERE id = :petId")
    ThuCung getById(String petId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ThuCung item);

    @Update
    void update(ThuCung item);

    @Query("DELETE FROM thu_cung WHERE id = :id")
    void deleteById(String id);

    @Query("UPDATE thu_cung SET canNang = :canNang WHERE id = :petId")
    void updateCanNang(String petId, float canNang);
}