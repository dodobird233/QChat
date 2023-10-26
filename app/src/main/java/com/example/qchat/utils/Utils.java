package com.example.qchat.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.qchat.QChatApplication;

public class Utils {
    public static final String IS_NEW_INVITE = "is_new_invite";
    private static Utils instance = new Utils();
    private static SharedPreferences sharedPreferences;

    private Utils() {

    }

    public static Utils getInstance() {
        if (sharedPreferences == null) {
            // 使用全局context防止内存泄露
            sharedPreferences = QChatApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    // 保存数据
    public void save(String key, Object value) {
        if (value instanceof String) {
            // 后台保存数据
            sharedPreferences.edit().putString(key, (String) value).apply();
        } else if (value instanceof Boolean) {
            sharedPreferences.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Integer) {
            sharedPreferences.edit().putInt(key, (Integer) value).apply();
        }
    }

    // 获取数据
    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }
}
