package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "benh_nen" trong Room Database.
 * Lưu trữ danh sách các bệnh lý nền hoặc bệnh mãn tính của thú cưng.
 */
@Entity(tableName = "benh_nen")
public class BenhNen {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;   // ID thú cưng liên kết
    public String tenBenh; // Tên bệnh nền

    public BenhNen() {}
}