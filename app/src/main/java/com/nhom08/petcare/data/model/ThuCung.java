package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "thu_cung")
public class ThuCung {

    @PrimaryKey
    @NonNull
    public String id;

    public String userId;
    public String tenThuCung;
    public String loai;         // "Chó" / "Mèo"
    public String giong;
    public String ngaySinh;
    public String gioiTinh;     // "Đực" / "Cái"
    public float canNang;
    public String mauLong;
    public String anhUrl;       // đường dẫn ảnh local

    public ThuCung() {}
}