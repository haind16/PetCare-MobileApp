package com.nhom08.petcare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PetManager {

    private static final String PREF_NAME = "pet_prefs";
    private static final String KEY_PET_ID = "current_pet_id";
    private static final String KEY_PET_NAME = "current_pet_name";
    private static final String KEY_PET_TYPE = "current_pet_type";

    private static PetManager instance;
    private SharedPreferences prefs;

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

    // Lưu pet đang chọn
    public void setCurrentPet(String petId, String petName, String petType) {
        prefs.edit()
                .putString(KEY_PET_ID, petId)
                .putString(KEY_PET_NAME, petName)
                .putString(KEY_PET_TYPE, petType)
                .apply();
    }

    // Lấy thông tin pet hiện tại
    public String getCurrentPetId() {
        return prefs.getString(KEY_PET_ID, null);
    }

    public String getCurrentPetName() {
        return prefs.getString(KEY_PET_NAME, "Thú cưng");
    }

    public String getCurrentPetType() {
        return prefs.getString(KEY_PET_TYPE, "");
    }

    public boolean hasPet() {
        return getCurrentPetId() != null;
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}