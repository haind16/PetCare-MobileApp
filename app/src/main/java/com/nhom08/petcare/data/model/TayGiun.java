package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "tay_giun")
public class TayGiun {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String ghiChu;
    public String ngay;

    public TayGiun() {}
}