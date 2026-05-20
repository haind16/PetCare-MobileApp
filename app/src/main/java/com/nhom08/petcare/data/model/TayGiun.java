package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "tay_giun" trong Room Database.
 * Lưu trữ lịch sử tẩy giun định kỳ của thú cưng.
 */
@Entity(tableName = "tay_giun")
public class TayGiun {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;  // ID thú cưng liên kết
    public String ghiChu; // Ghi chú về loại thuốc hoặc phản ứng
    public String ngay;   // Ngày thực hiện tẩy giun

    public TayGiun() {}
}