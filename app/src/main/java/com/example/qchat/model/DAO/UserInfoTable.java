package com.example.qchat.model.DAO;


public class UserInfoTable {
    public static final String TABLE_NAME = "tab_account";
    public static final String COL_NAME = "name";
    public static final String COL_ID = "id";
    public static final String COL_NICKNAME = "nickname";
    public static final String COL_AVATAR = "avatar";

    public static final String CREATE_TABLE = "create table "
            + TABLE_NAME + " ("
            + COL_ID + " text primary key,"
            + COL_NAME + " text,"
            + COL_NICKNAME + " text,"
            + COL_AVATAR + " text);";
}
