package com.nhom08.petcare.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.nhom08.petcare.data.local.dao.*;
import com.nhom08.petcare.data.model.*;

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

    // Khai báo tất cả DAO
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

    // Singleton — chỉ tạo 1 lần
    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "petcare_db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}