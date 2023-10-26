package com.example.qchat.model.DAO;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.qchat.model.bean.GroupInfo;
import com.example.qchat.model.bean.InvitationInfo;
import com.example.qchat.model.bean.UserInfo;
import com.example.qchat.model.db.DBHelper;

import java.util.ArrayList;
import java.util.List;


public class InviteDao {
    private DBHelper helper;

    public InviteDao(DBHelper helper) {
        this.helper = helper;
    }

    public void addInvitation(InvitationInfo invitationInfo) {
        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON, invitationInfo.getReason());
        values.put(InviteTable.COL_STATUS, invitationInfo.getStatus().ordinal());

        UserInfo user = invitationInfo.getUser();

        if (user != null) {//联系人
            values.put(InviteTable.COL_USER_ID, invitationInfo.getUser().getId());
            values.put(InviteTable.COL_USER_NAME, invitationInfo.getUser().getName());
        } else {//群组
            values.put(InviteTable.COL_GROUP_ID, invitationInfo.getGroup().getGroupId());
            values.put(InviteTable.COL_GROUP_NAME, invitationInfo.getGroup().getGroupName());
            values.put(InviteTable.COL_USER_ID, invitationInfo.getGroup().getInvitePerson());
        }

        db.replace(InviteTable.TABLE_NAME, null, values);
    }

    public List<InvitationInfo> getInvitations() {
        SQLiteDatabase db = helper.getReadableDatabase();
        // 查询邀请
        String sql = "select * from " + InviteTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(sql, null);

        List<InvitationInfo> invitationInfoList = new ArrayList<>();

        while (cursor.moveToNext()) {
            InvitationInfo invitationInfo = new InvitationInfo();

            invitationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            invitationInfo.setStatus(ParseInviteStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));

            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_ID));
            if (groupId == null) {
                // 联系人邀请
                UserInfo userInfo = new UserInfo();
                userInfo.setId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_ID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                userInfo.setNickname(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                invitationInfo.setUser(userInfo);
            } else {
                // 群组邀请
                GroupInfo groupInfo = new GroupInfo();

                groupInfo.setGroupId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_ID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvitePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_ID)));
                invitationInfo.setGroup(groupInfo);
            }
            invitationInfoList.add(invitationInfo);
        }
        cursor.close();
        return invitationInfoList;
    }

    private InvitationInfo.InvitationStatus ParseInviteStatus(int intStatus) {

        if (intStatus == InvitationInfo.InvitationStatus.NEW_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT;
        }
        if (intStatus == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER;
        }
        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION;
        }
        if (intStatus == InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE;
        }
        return null;
    }

    // 删除邀请
    public void removeInvitation(String Id) {
        if (Id == null) return;
        SQLiteDatabase db = helper.getReadableDatabase();
        db.delete(InviteTable.TABLE_NAME, InviteTable.COL_USER_ID + "=?", new String[]{Id});
    }

    // 更新邀请状态
    public void updateInvitationStatus(InvitationInfo.InvitationStatus invitationStatus, String Id) {
        if (Id == null) return;
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(InviteTable.COL_STATUS, invitationStatus.ordinal());
        db.update(InviteTable.TABLE_NAME, contentValues, InviteTable.COL_USER_ID + "=?", new String[]{Id});
    }
}
