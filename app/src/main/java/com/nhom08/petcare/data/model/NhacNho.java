package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "nhac_nho" trong Room Database.
 * Lưu trữ các thông tin nhắc nhở chăm sóc thú cưng như lịch ăn, tiêm phòng, uống thuốc.
 */
@Entity(tableName = "nhac_nho")
public class NhacNho {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;        // ID thú cưng liên kết
    public String loai;         // Loại nhắc nhở (Ví dụ: "Ăn sáng", "Tiêm phòng", "Uống thuốc")
    public String ngay;         // Ngày thực hiện nhắc nhở (dd/MM/yyyy)
    public String gio;          // Giờ thực hiện nhắc nhở (HH:mm)
    public boolean daHoanThanh; // Trạng thái đã thực hiện hay chưa

    public NhacNho() {}
}