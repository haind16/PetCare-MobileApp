package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "can_nang")
public class CanNang {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public float canNang;
    public String ngay;

    public CanNang() {}
}