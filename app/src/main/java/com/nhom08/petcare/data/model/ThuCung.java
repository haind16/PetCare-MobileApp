package com.nhom08.petcare.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Entity đại diện cho bảng "thu_cung" trong Room Database.
 * Lưu trữ thông tin cơ bản của thú cưng.
 */
@Entity(tableName = "thu_cung")
public class ThuCung {

    @PrimaryKey
    @NonNull
    public String id; // Mã định danh duy nhất của thú cưng (UUID)

    public String userId;     // ID của người chủ (liên kết với Firebase User UID)
    public String tenThuCung; // Tên của thú cưng
    public String loai;       // Loại thú cưng (ví dụ: "Chó", "Mèo")
    public String giong;      // Giống loài
    public String ngaySinh;   // Ngày sinh của thú cưng
    public String gioiTinh;   // Giới tính ("Đực" / "Cái")
    public float canNang;     // Cân nặng hiện tại
    public String mauLong;    // Màu lông
    public String anhUrl;     // Đường dẫn ảnh (có thể là path local hoặc URL Cloudinary)

    public ThuCung() {}
}