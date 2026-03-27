package com.nhom08.petcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

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

    public static PetManager getInstance(Context context) {
        if (instance == null) {
            instance = new PetManager(context);
        }
        return instance;
    }

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

    // ✅ Fix: check cả null lẫn empty string
    public boolean hasPet() {
        String id = getCurrentPetId();
        return id != null && !id.isEmpty();
    }

    public void clearCurrentPet() {
        prefs.edit()
                .remove(KEY_PET_ID)
                .remove(KEY_PET_NAME)
                .remove(KEY_PET_ANH)
                .apply();
    }

    // Xóa toàn bộ (dùng khi logout)
    public void clear() {
        prefs.edit().clear().apply();
    }
}