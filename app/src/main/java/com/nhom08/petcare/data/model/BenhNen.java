package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "benh_nen")
public class BenhNen {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String tenBenh;

    public BenhNen() {}
}