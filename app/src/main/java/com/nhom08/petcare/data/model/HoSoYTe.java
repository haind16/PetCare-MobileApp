package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "ho_so_y_te")
public class HoSoYTe {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String ngayKham;
    public String loaiKham;
    public String phongKham;
    public String bacSi;
    public String chuanDoan;
    public String donThuoc;    // đơn thuốc
    public String tiemPhong;   // thông tin tiêm phòng (field mới)

    public HoSoYTe() {}
}