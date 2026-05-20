package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "di_ung" trong Room Database.
 * Lưu trữ thông tin về các tác nhân gây dị ứng cho thú cưng (thức ăn, thuốc, môi trường...).
 */
@Entity(tableName = "di_ung")
public class DiUng {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;         // ID thú cưng liên kết
    public String chatGayDiUng;  // Tên chất hoặc tác nhân gây dị ứng

    public DiUng() {}
}