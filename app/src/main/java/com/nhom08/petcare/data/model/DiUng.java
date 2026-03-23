package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "di_ung")
public class DiUng {

    @PrimaryKey
    @NonNull
    public String id;

    public String petId;
    public String chatGayDiUng;

    public DiUng() {}
}