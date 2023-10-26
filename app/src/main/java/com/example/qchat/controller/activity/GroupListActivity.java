package com.example.qchat.controller.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qchat.R;
import com.example.qchat.controller.adapter.GroupListAdapter;
import com.example.qchat.model.Model;
import com.example.qchat.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;
import java.util.Objects;

public class GroupListActivity extends Activity {
    private ListView lv_group_list;
    private LinearLayout ll_group_list;
    private GroupListAdapter groupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        // 点击群item触发事件
        lv_group_list.setOnItemClickListener((adapterView, view, i, l) -> {
            if (i == 0) return;// 头布局占用0位置
            Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
            // 设置参数
            intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
            EMGroup group = EMClient.getInstance().groupManager().getAllGroups().get(i - 1);
            intent.putExtra(EaseConstant.EXTRA_USER_ID, group.getGroupId());
            startActivity(intent);
        });
        lv_group_list.setOnItemLongClickListener((adapterView, view, i, l) -> {
            if (i == 0) return false;
            EMGroup group = EMClient.getInstance().groupManager().getAllGroups().get(i - 1);
            showOptionDialog(group);
            return true;
        });
        // 跳转新建群
        ll_group_list.setOnClickListener(view -> {
            Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);
            startActivity(intent);
        });
    }

    private void showOptionDialog(EMGroup group) {
        if (group == null) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 群主点击
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
            builder.setTitle("想要解散该群聊嘛？");
            String[] options = {"解散", "取消"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    dismissGroup(group);
                }
            });
        } else {// 群成员点击
            builder.setTitle("想要退出该群聊嘛？");
            String[] options = {"退出", "取消"};
            builder.setItems(options, (dialog, which) -> {
                if (which == 0) {
                    withdrawGroup(group);
                }
            });
        }
        // 显示选择框
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //解散群
    private void dismissGroup(EMGroup group) {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                EMClient.getInstance().groupManager().destroyGroup(group.getGroupId());

                //发送广播
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(GroupListActivity.this);
                Intent intent = new Intent(Constant.EXIT_GROUP);
                intent.putExtra(Constant.GROUP_ID, group.getGroupId());
                localBroadcastManager.sendBroadcast(intent);
                //刷新
                groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());

                runOnUiThread(() -> Toast.makeText(GroupListActivity.this, "解散群成功", Toast.LENGTH_SHORT).show());
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GroupListActivity.this, "解散群失败", Toast.LENGTH_SHORT).show());
            }
        });
    }


    //退群
    private void withdrawGroup(EMGroup group) {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                EMClient.getInstance().groupManager().leaveGroup(group.getGroupId());

                //发送广播
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(GroupListActivity.this);
                Intent intent = new Intent(Constant.EXIT_GROUP);
                intent.putExtra(Constant.GROUP_ID, group.getGroupId());
                localBroadcastManager.sendBroadcast(intent);
                //刷新
                groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());

                runOnUiThread(() -> Toast.makeText(GroupListActivity.this, "退出群成功", Toast.LENGTH_SHORT).show());
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GroupListActivity.this, "退出群失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void initView() {
        // 添加头布局
        lv_group_list = findViewById(R.id.lv_group_list);
        View headerView = View.inflate(this, R.layout.header_group_list, null);
        lv_group_list.addHeaderView(headerView);
        ll_group_list = headerView.findViewById(R.id.ll_group_list);

    }

    private void initData() {
        groupListAdapter = new GroupListAdapter(this);
        lv_group_list.setAdapter(groupListAdapter);
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                // 从服务器获取数据
                final List<EMGroup> groups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                runOnUiThread(() -> groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups()));
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GroupListActivity.this, "加载群信息失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }
}
