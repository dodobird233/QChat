package com.example.qchat.model.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qchat.model.db.UserAccountDB;


public class UserInfoDao {

    private final UserAccountDB mHelper;

    public UserInfoDao(Context context) {
        mHelper = new UserAccountDB(context);
    }

    public void addAccount(com.example.qchat.model.bean.UserInfo user) {

        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        // 填入相应的用户信息
        values.put(UserInfoTable.COL_ID, user.getId());
        values.put(UserInfoTable.COL_NAME, user.getName());
        values.put(UserInfoTable.COL_NICKNAME, user.getNickname());
        values.put(UserInfoTable.COL_AVATAR, user.getAvatar());

        db.replace(UserInfoTable.TABLE_NAME, null, values);
    }

    public com.example.qchat.model.bean.UserInfo getAccountByHxId(String Id) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        // 查询用户信息
        String sql = "select * from " + UserInfoTable.TABLE_NAME + " where " + UserInfoTable.COL_ID + "=?";
        Cursor cursor = db.rawQuery(sql, new String[]{Id});
        com.example.qchat.model.bean.UserInfo userInfo = null;
        if (cursor.moveToNext()) {
            userInfo = new com.example.qchat.model.bean.UserInfo();
            userInfo.setId(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_ID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_NAME)));
            userInfo.setNickname(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_NICKNAME)));
            userInfo.setAvatar(cursor.getString(cursor.getColumnIndex(UserInfoTable.COL_AVATAR)));
        }
        // 释放资源
        cursor.close();

        return userInfo;
    }
}
