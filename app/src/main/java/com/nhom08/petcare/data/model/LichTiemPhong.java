package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "lich_tiem_phong" trong Room Database.
 * Lưu trữ thông tin về các đợt tiêm chủng của thú cưng.
 */
@Entity(tableName = "lich_tiem_phong")
public class LichTiemPhong {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;       // ID thú cưng sở hữu lịch tiêm này
    public String tenVacxin;   // Tên loại vắc-xin đã tiêm hoặc cần tiêm
    public String ngayTiem;    // Ngày thực hiện tiêm
    public String ngayNhacNho; // Ngày hẹn nhắc nhở tiêm mũi tiếp theo

    public LichTiemPhong() {}
}