package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "nhat_ky")
public class NhatKy {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String loaiHoatDong;  // "Cho ăn" / "Đi bộ"...
    public String ngay;
    public String ghiChu;

    public NhatKy() {}
}