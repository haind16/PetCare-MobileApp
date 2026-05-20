package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "nhat_ky" trong Room Database.
 * Lưu trữ nhật ký các hoạt động hàng ngày của thú cưng.
 */
@Entity(tableName = "nhat_ky")
public class NhatKy {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;        // ID thú cưng liên kết
    public String loaiHoatDong; // Tên loại hoạt động (ví dụ: "Cho ăn", "Đi dạo", "Vệ sinh")
    public String ngay;         // Ngày thực hiện hoạt động
    public String ghiChu;       // Ghi chú chi tiết về hoạt động

    public NhatKy() {}
}