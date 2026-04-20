package com.demo.ltud_n10.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SharedPrefsManager {
    private static final String PREF_NAME = "CoffeeHRM_Prefs";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ROLE = "user_role";

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPrefsManager(@ApplicationContext Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void saveUserRole(String role) {
        sharedPreferences.edit().putString(KEY_USER_ROLE, role).apply();
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
