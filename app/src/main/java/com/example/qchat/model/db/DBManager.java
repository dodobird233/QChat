package com.example.qchat.model.db;

import android.content.Context;

import com.example.qchat.model.DAO.ContactDao;
import com.example.qchat.model.DAO.InviteDao;

public class DBManager {

    private DBHelper dbHelper;
    private ContactDao contactDao;
    private InviteDao inviteDao;


    public DBManager(Context context, String name) {
        // 创建数据库和联系人表、邀请信息表
        dbHelper = new DBHelper(context, name);
        contactDao = new ContactDao(dbHelper);
        inviteDao = new InviteDao(dbHelper);

    }

    public DBManager() {

    }

    // 获取联系人表
    public ContactDao getContactTableDao() {
        return contactDao;
    }

    // 获取邀请信息表
    public InviteDao getInviteTableDao() {
        return inviteDao;
    }

    //关闭数据库
    public void close() {
        dbHelper.close();
    }
}
