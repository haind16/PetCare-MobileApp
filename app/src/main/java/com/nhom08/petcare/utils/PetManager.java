package com.nhom08.petcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manager xử lý lưu trữ và truy xuất thông tin thú cưng đang được chọn hiện tại.
 * Sử dụng SharedPreferences để duy trì trạng thái thú cưng được chọn xuyên suốt ứng dụng.
 */
public class PetManager {

    private static final String PREF_NAME   = "pet_prefs";
    private static final String KEY_PET_ID   = "current_pet_id";
    private static final String KEY_PET_NAME = "current_pet_name";
    private static final String KEY_PET_TYPE = "current_pet_type";
    private static final String KEY_PET_ANH  = "current_pet_anh";

    private static PetManager instance;
    private final SharedPreferences prefs;

    private PetManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Singleton Pattern để lấy instance của PetManager.
     */
    public static PetManager getInstance(Context context) {
        if (instance == null) {
            instance = new PetManager(context);
        }
        return instance;
    }

    /**
     * Lưu thông tin thú cưng được chọn hiện tại vào SharedPreferences.
     */
    public void setCurrentPet(String petId, String petName, String anhUrl) {
        prefs.edit()
                .putString(KEY_PET_ID,   petId)
                .putString(KEY_PET_NAME, petName)
                .putString(KEY_PET_ANH,  anhUrl != null ? anhUrl : "")
                .apply();
    }

    public String getCurrentPetId() {
        return prefs.getString(KEY_PET_ID, null);
    }

    public String getCurrentPetName() {
        return prefs.getString(KEY_PET_NAME, "Thú cưng");
    }

    public String getCurrentPetType() {
        return prefs.getString(KEY_PET_TYPE, "");
    }

    public String getCurrentPetAnh() {
        return prefs.getString(KEY_PET_ANH, "");
    }

    /**
     * Kiểm tra xem người dùng đã chọn thú cưng nào để quản lý chưa.
     */
    public boolean hasPet() {
        String id = getCurrentPetId();
        return id != null && !id.isEmpty();
    }

    /**
     * Xóa thông tin thú cưng đang chọn.
     */
    public void clearCurrentPet() {
        prefs.edit()
                .remove(KEY_PET_ID)
                .remove(KEY_PET_NAME)
                .remove(KEY_PET_ANH)
                .apply();
    }

    /**
     * Xóa toàn bộ dữ liệu trong PetManager (thường dùng khi đăng xuất).
     */
    public void clear() {
        prefs.edit().clear().apply();
    }
}