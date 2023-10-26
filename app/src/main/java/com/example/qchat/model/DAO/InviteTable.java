package com.example.qchat.model.DAO;


public class InviteTable {
    public static final String TABLE_NAME = "tab_invite";

    public static final String COL_USER_ID = "user_id";  //用户id
    public static final String COL_USER_NAME = "user_name";  //用户名

    public static final String COL_GROUP_NAME = "group_name";  //群组名
    public static final String COL_GROUP_ID = "group_id";  //群组id

    public static final String COL_REASON = "reason";  //邀请原因
    public static final String COL_STATUS = "status";  //邀请状态

    public static final String CREATE_TAB = "create table "
            + TABLE_NAME + " ("
            + COL_USER_ID + " text primary key,"
            + COL_USER_NAME + " text,"
            + COL_GROUP_ID + " text,"
            + COL_GROUP_NAME + " text,"
            + COL_REASON + " text,"
            + COL_STATUS + " integer);";
}
