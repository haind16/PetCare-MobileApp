package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "ho_so_y_te" trong Room Database.
 * Lưu trữ thông tin về các lần thăm khám y tế của thú cưng.
 */
@Entity(tableName = "ho_so_y_te")
public class HoSoYTe {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất (UUID)

    public String petId;      // ID thú cưng liên kết
    public String ngayKham;   // Ngày thực hiện khám bệnh
    public String loaiKham;   // Loại hình khám (khám định kỳ, cấp cứu...)
    public String phongKham;  // Tên phòng khám/bệnh viện thú y
    public String bacSi;      // Tên bác sĩ điều trị
    public String chuanDoan;  // Kết luận chẩn đoán bệnh
    public String donThuoc;   // Thông tin đơn thuốc đi kèm
    public String tiemPhong;  // Thông tin tiêm phòng bổ sung trong lần khám

    public HoSoYTe() {}
}