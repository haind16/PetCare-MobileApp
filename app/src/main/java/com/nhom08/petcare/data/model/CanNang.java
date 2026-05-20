package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "can_nang" trong Room Database.
 * Lưu trữ lịch sử theo dõi cân nặng của thú cưng theo thời gian.
 */
@Entity(tableName = "can_nang")
public class CanNang {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;   // ID thú cưng liên kết
    public float canNang;  // Giá trị cân nặng (kg)
    public String ngay;    // Ngày ghi nhận cân nặng

    public CanNang() {}
}