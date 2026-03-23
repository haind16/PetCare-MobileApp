package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "don_thuoc")
public class DonThuoc {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String tenThuoc;
    public String lieuLuong;
    public String cachDung;

    public DonThuoc() {}
}