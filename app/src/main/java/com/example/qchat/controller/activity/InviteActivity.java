package com.example.qchat.controller.activity;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qchat.R;
import com.example.qchat.controller.adapter.InviteAdapter;
import com.example.qchat.model.Model;
import com.example.qchat.model.bean.InvitationInfo;
import com.example.qchat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

public class InviteActivity extends Activity {
    private ListView lv_invite;
    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager localBroadcastManager;

    private BroadcastReceiver inviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 邀请信息变化则刷新页面
            refresh();
        }
    };

    // 实现监听的几个方法：接受拒绝（群）邀请/申请
    private InviteAdapter.OnInviteListener onInviteListener = new InviteAdapter.OnInviteListener() {
        //同意好友申请
        @Override
        public void onAccept(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    EMClient.getInstance().contactManager().acceptInvitation(invitationInfo.getUser().getId());
                    Model.getInstance().getDBManager().getInviteTableDao().updateInvitationStatus(InvitationInfo.InvitationStatus.INVITE_ACCEPT, invitationInfo.getUser().getId());
                    runOnUiThread(() -> {
                        Toast.makeText(InviteActivity.this, "同意加为好友", Toast.LENGTH_SHORT).show();
                        refresh();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(InviteActivity.this, "加为好友失败", Toast.LENGTH_SHORT).show());
                }
            });
        }

        // 拒绝好友申请
        @Override
        public void onReject(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    EMClient.getInstance().contactManager().declineInvitation(invitationInfo.getUser().getId());
                    Model.getInstance().getDBManager().getInviteTableDao().removeInvitation(invitationInfo.getUser().getId());
                    runOnUiThread(() -> {
                        Toast.makeText(InviteActivity.this, "已拒绝", Toast.LENGTH_SHORT).show();
                        refresh();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(InviteActivity.this, "拒绝失败了", Toast.LENGTH_SHORT).show());
                }
            });
        }

        // 接受群邀请
        @Override
        public void onInviteAccept(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    // 告诉服务器接受了邀请
                    EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson());
                    // 更新数据库
                    invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_INVITE);
                    Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);
                    // 内存变化
                    runOnUiThread(() -> {
                        Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                        refresh();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(InviteActivity.this, "接受邀请失败", Toast.LENGTH_SHORT).show());
                }
            });
        }

        // 拒绝群邀请
        @Override
        public void onInviteReject(final InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    // 通知服务器拒绝邀请
                    EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson(), "拒绝邀请");
                    // 更新数据库
                    invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_INVITE);
                    Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

                    // 更新内存
                    runOnUiThread(() -> {
                        Toast.makeText(InviteActivity.this, "已拒绝邀请", Toast.LENGTH_SHORT).show();
                        // 刷新页面
                        refresh();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
        }

        //同意群申请
        @Override
        public void onApplicationAccept(InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    // 通知服务器拒绝邀请
                    EMClient.getInstance().groupManager().acceptInvitation(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson());
                    // 更新数据库
                    invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_ACCEPT_APPLICATION);
                    Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

                    // 更新内存
                    runOnUiThread(() -> {
                        Toast.makeText(InviteActivity.this, "已接受群申请", Toast.LENGTH_SHORT).show();
                        // 刷新页面
                        refresh();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
        }

        //拒绝群申请
        @Override
        public void onApplicationReject(InvitationInfo invitationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                try {
                    // 通知服务器拒绝邀请
                    EMClient.getInstance().groupManager().declineInvitation(invitationInfo.getGroup().getGroupId(), invitationInfo.getGroup().getInvitePerson(), "拒绝申请");
                    // 更新数据库
                    invitationInfo.setStatus(InvitationInfo.InvitationStatus.GROUP_REJECT_APPLICATION);
                    Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invitationInfo);

                    // 更新内存
                    runOnUiThread(() -> {
                        Toast.makeText(InviteActivity.this, "已拒绝该申请", Toast.LENGTH_SHORT).show();
                        // 刷新页面
                        refresh();
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        initView();
        initData();
    }

    private void initView() {
        lv_invite = findViewById(R.id.lv_invite);
    }

    private void initData() {
        // 初始化listview
        inviteAdapter = new InviteAdapter(this, onInviteListener);
        // 设置适配器
        lv_invite.setAdapter(inviteAdapter);
        // 刷新
        refresh();
        // 注册邀请信息变化的广播
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(inviteChangedReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        localBroadcastManager.registerReceiver(inviteChangedReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void refresh() {
        // 获取数据库中的所有邀请信息
        List<InvitationInfo> invitationInfoList = Model.getInstance().getDBManager().getInviteTableDao().getInvitations();
        // 刷新适配器
        inviteAdapter.refresh(invitationInfoList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(inviteChangedReceiver);
    }
}