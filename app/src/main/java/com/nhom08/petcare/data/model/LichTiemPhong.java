package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "lich_tiem_phong")
public class LichTiemPhong {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String tenVacxin;
    public String ngayTiem;
    public String ngayNhacNho;

    public LichTiemPhong() {}
}