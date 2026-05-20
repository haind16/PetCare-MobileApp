package com.nhom08.petcare.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.nhom08.petcare.data.local.dao.*;
import com.nhom08.petcare.data.model.*;

/**
 * Lớp quản lý cơ sở dữ liệu Room (SQLite local).
 * Chứa các bảng (Entities) liên quan đến quản lý sức khỏe, nhắc nhở và hồ sơ thú cưng.
 */
@Database(
        entities = {
                ThuCung.class,
                LichTiemPhong.class,
                CanNang.class,
                TayGiun.class,
                DiUng.class,
                BenhNen.class,
                DonThuoc.class,
                NhacNho.class,
                NhatKy.class,
                HoSoYTe.class
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    // Khai báo các Data Access Object (DAO) để thao tác với dữ liệu
    public abstract ThuCungDao thuCungDao();
    public abstract LichTiemPhongDao lichTiemPhongDao();
    public abstract CanNangDao canNangDao();
    public abstract TayGiunDao tayGiunDao();
    public abstract DiUngDao diUngDao();
    public abstract BenhNenDao benhNenDao();
    public abstract DonThuocDao donThuocDao();
    public abstract NhacNhoDao nhacNhoDao();
    public abstract NhatKyDao nhatKyDao();
    public abstract HoSoYTeDao hoSoYTeDao();

    /**
     * Singleton Pattern để đảm bảo chỉ có duy nhất một instance của database trong suốt vòng đời ứng dụng.
     * @param context Context của ứng dụng
     * @return Instance của AppDatabase
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "petcare_db"
                    )
                    .fallbackToDestructiveMigration() // Xóa và tạo lại database nếu version thay đổi mà không có migration
                    .build();
        }
        return instance;
    }
}