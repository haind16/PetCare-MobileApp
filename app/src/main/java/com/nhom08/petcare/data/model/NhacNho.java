package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "nhac_nho")
public class NhacNho {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String loai;     // "Ăn sáng" / "Tiêm phòng" / "Uống thuốc"...
    public String ngay;
    public String gio;
    public boolean daHoanThanh;

    public NhacNho() {}
}