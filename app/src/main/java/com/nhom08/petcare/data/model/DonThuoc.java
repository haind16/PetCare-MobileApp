package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "don_thuoc" trong Room Database.
 * Lưu trữ thông tin về các loại thuốc thú cưng cần uống.
 */
@Entity(tableName = "don_thuoc")
public class DonThuoc {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;    // ID thú cưng liên kết
    public String tenThuoc; // Tên loại thuốc
    public String lieuLuong; // Liều lượng sử dụng
    public String cachDung;  // Cách dùng (ví dụ: uống sau ăn)

    public DonThuoc() {}
}