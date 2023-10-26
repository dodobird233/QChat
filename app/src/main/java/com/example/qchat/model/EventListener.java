package com.example.qchat.model;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qchat.model.bean.GroupInfo;
import com.example.qchat.model.bean.InvitationInfo;
import com.example.qchat.model.bean.UserInfo;
import com.example.qchat.utils.Constant;
import com.example.qchat.utils.Utils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;


// 全局事件监听
public class EventListener {

    private Context ctx;
    private LocalBroadcastManager localBroadcastManager;

    public EventListener(Context context) {
        ctx = context;
        // 创建发送广播的管理者
        localBroadcastManager = LocalBroadcastManager.getInstance(ctx);
        // 注册联系人变化监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        // 注册群信息变化监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);
    }

    // 注册联系人变化监听
    private final EMContactListener emContactListener = new EMContactListener() {

        // 联系人增加后执行
        @Override
        public void onContactAdded(String id) {
            // 更新数据库
            Model.getInstance().getDBManager().getContactTableDao().saveContact(new UserInfo(id), true);
            // 发送联系人变化广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        // 联系人删除后执行
        @Override
        public void onContactDeleted(String id) {
            // 更新数据库
            Model.getInstance().getDBManager().getContactTableDao().deleteContactById(id);
            Model.getInstance().getDBManager().getInviteTableDao().removeInvitation(id);
            // 发送联系人变化广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }

        // 收到联系人的邀请
        @Override
        public void onContactInvited(String id, String reason) {
            // 更新数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new UserInfo(id));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_INVITE);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            // 处理红点
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);
            // 发送邀请信息变化广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        // 对方同意好友请求
        @Override
        public void onFriendRequestAccepted(String id) {
            // 更新数据库
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setUser(new UserInfo(id));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            // 处理红点
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);
            // 发送邀请信息变化广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }

        // 对方同意好友请求
        @Override
        public void onFriendRequestDeclined(String s) {
            // 处理红点
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);
            // 发送邀请信息变化广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));
        }
    };

    // 群信息变化监听
    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {
        // 收到群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            // 处理红点
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);

            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }


        // 收到群申请
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, applicant));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            // 处理红点
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);

            // 发送广播
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群邀请被接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String acceptor) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, acceptor));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_ACCEPTED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);
            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群申请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName, groupId, decliner));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);

            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String inviter, String reason) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);

            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String inviter, String reason) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_DECLINED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);

            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        // 被踢出群
        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        // 群被解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        // 群邀请被自动接受
        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviterMessage) {
            InvitationInfo invitationInfo = new InvitationInfo();
            invitationInfo.setReason(inviterMessage);
            invitationInfo.setGroup(new GroupInfo(groupId, groupId, inviter));
            invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);

            localBroadcastManager.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }

        @Override
        public void onMuteListAdded(String s, List<String> list, long l) {

        }

        @Override
        public void onMuteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAdminAdded(String s, String s1) {

        }

        @Override
        public void onAdminRemoved(String s, String s1) {

        }

        @Override
        public void onOwnerChanged(String s, String s1, String s2) {

        }

        @Override
        public void onMemberJoined(String s, String s1) {

        }

        @Override
        public void onMemberExited(String s, String s1) {

        }

        @Override
        public void onAnnouncementChanged(String s, String s1) {

        }

        @Override
        public void onSharedFileAdded(String s, EMMucSharedFile emMucSharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String s, String s1) {

        }
    };
}
