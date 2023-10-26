package com.example.qchat.model.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qchat.model.bean.UserInfo;
import com.example.qchat.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;


public class ContactDao {

    private final DBHelper helper;

    public ContactDao(DBHelper helper) {
        this.helper = helper;
    }
    // 加载联系人列表
    public List<UserInfo> getContacts() {
        SQLiteDatabase db = helper.getReadableDatabase();
        // 查询
        String sql = "select * from " + ContactTable.TABLE_NAME + " where " + ContactTable.COL_IS_CONTACT + "=1";
        Cursor cursor = db.rawQuery(sql, null);

        List<UserInfo> users = new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();

            userInfo.setId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_ID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNickname(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICKNAME)));
            userInfo.setAvatar(cursor.getString(cursor.getColumnIndex(ContactTable.COL_AVATAR)));

            users.add(userInfo);
        }
        cursor.close();
        return users;
    }

    // 通过id获取联系人信息
    public UserInfo getContactById(String Id) {
        if (Id == null) return null;

        SQLiteDatabase db = helper.getReadableDatabase();
        // 查询用户信息
        String sql = "select * from " + ContactTable.TABLE_NAME + " where " + ContactTable.COL_ID + "=?";
        Cursor cursor = db.rawQuery(sql,new String[]{Id});

        UserInfo userInfo = null;
        if (cursor.moveToNext()) {
            userInfo = new UserInfo();

            userInfo.setId(cursor.getString(cursor.getColumnIndex(ContactTable.COL_ID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNickname(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICKNAME)));
            userInfo.setAvatar(cursor.getString(cursor.getColumnIndex(ContactTable.COL_AVATAR)));
        }
        cursor.close();
        return userInfo;
    }

    // 通过id列表批量获取联系人信息
    public List<UserInfo> getContactById(List<String> ids) {
        if (ids == null || ids.isEmpty()) return null;
        List<UserInfo> contacts = new ArrayList<>();
        for (String id : ids) {
            UserInfo contact = getContactById(id);
            contacts.add(contact);
        }
        return contacts;
    }

    //保存联系人
    public void saveContact(UserInfo user, boolean isContact) {
        if (user == null) return;

        SQLiteDatabase db = helper.getReadableDatabase();

        // 保存联系人
        ContentValues values = new ContentValues();
        values.put(ContactTable.COL_ID, user.getId());
        values.put(ContactTable.COL_NAME, user.getName());
        values.put(ContactTable.COL_NICKNAME, user.getNickname());
        values.put(ContactTable.COL_AVATAR, user.getAvatar());
        values.put(ContactTable.COL_IS_CONTACT, isContact ? 1 : 0);
        db.replace(ContactTable.TABLE_NAME, null, values);
    }

    // 批量保存联系人
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {
        if (contacts == null || contacts.size() == 0) return;
        for (UserInfo contact : contacts)
            saveContact(contact, isMyContact);
    }

    // 删除联系人
    public void deleteContactById(String Id) {
        if (Id == null)  return;
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(ContactTable.TABLE_NAME, ContactTable.COL_ID + "=?", new String[]{Id});
    }
}
