package com.example.qchat.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qchat.R;
import com.example.qchat.controller.activity.AddContactActivity;
import com.example.qchat.controller.activity.ChatActivity;
import com.example.qchat.controller.activity.GroupListActivity;
import com.example.qchat.controller.activity.InviteActivity;
import com.example.qchat.model.Model;
import com.example.qchat.model.bean.UserInfo;
import com.example.qchat.utils.Constant;
import com.example.qchat.utils.Utils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ContactListFragment extends EaseContactListFragment {

    private ImageView iv_contact_red;
    private LocalBroadcastManager localBroadcastManager;
    private LinearLayout ll_contact_invite;
    private String Id;

    private BroadcastReceiver ContactChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 刷新页面
            refreshContact();
        }
    };
    private BroadcastReceiver ContactInviteChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 更新红点
            iv_contact_red.setVisibility(View.VISIBLE);
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);
        }
    };
    private BroadcastReceiver GroupChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 显示红点
            iv_contact_red.setVisibility(View.VISIBLE);
            Utils.getInstance().save(Utils.IS_NEW_INVITE, true);
        }
    };

    @Override
    protected void initView() {
        super.initView();
        // 头布局的加号
        titleBar.setRightImageResource(R.drawable.em_add);
        // 生成并添加头布局
        View headerView = View.inflate(getActivity(), R.layout.header_fragment_contact, null);
        listView.addHeaderView(headerView);
        // 获取红点对象
        iv_contact_red = headerView.findViewById(R.id.iv_contact_red);
        // 获取邀请信息的条目对象
        ll_contact_invite = headerView.findViewById(R.id.ll_contact_invite);

        setContactListItemClickListener(user -> {
            if (user == null) return;
            Intent intent = new Intent(getActivity(), ChatActivity.class);

            intent.putExtra(EaseConstant.EXTRA_USER_ID, user.getUsername());
            startActivity(intent);
        });

        LinearLayout ll_contact_group = headerView.findViewById(R.id.ll_contact_group);
        ll_contact_group.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GroupListActivity.class);
            startActivity(intent);
        });

    }

    @Override
    protected void setUpView() {
        super.setUpView();
        // 添加按钮点击事件
        titleBar.setRightLayoutClickListener(v -> {
            // 跳转到添加联系人页面
            Intent intent = new Intent(getActivity(), AddContactActivity.class);
            startActivity(intent);
        });
        // 初始化红点显示
        boolean isNewInvite = Utils.getInstance().getBoolean(Utils.IS_NEW_INVITE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);
        // 邀请信息条目的点击事件
        ll_contact_invite.setOnClickListener(v -> {
            // 红点消失
            iv_contact_red.setVisibility(View.GONE);
            Utils.getInstance().save(Utils.IS_NEW_INVITE, false);
            // 跳转到邀请信息邀请页面
            Intent intent = new Intent(getActivity(), InviteActivity.class);
            startActivity(intent);
        });
        // 注册广播
        localBroadcastManager = LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()));
        localBroadcastManager.registerReceiver(ContactInviteChangeReceiver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        localBroadcastManager.registerReceiver(ContactChangeReceiver, new IntentFilter(Constant.CONTACT_CHANGED));
        localBroadcastManager.registerReceiver(GroupChangeReceiver, new IntentFilter(Constant.GROUP_INVITE_CHANGED));
        // 从服务器获取所有的联系人信息
        getContactFromServer();
        // 绑定listview和contextmenu
        registerForContextMenu(listView);
    }


    private void getContactFromServer() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                // 获取到所有好友的id
                List<String> ids = EMClient.getInstance().contactManager().getAllContactsFromServer();

                // 将id转换为用户集合
                if (ids != null && !ids.isEmpty()) {
                    List<UserInfo> contacts = new ArrayList<>();
                    for (String id : ids) {
                        UserInfo userInfo = new UserInfo(id);
                        contacts.add(userInfo);
                    }

                    //保存好友信息到本地数据库
                    Model.getInstance().getDBManager().getContactTableDao().saveContacts(contacts, true);

                    if (getActivity() == null) {
                        return;
                    }
                    // 刷新页面
                    getActivity().runOnUiThread(this::refreshContact);
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        });
    }

    // 刷新页面
    private void refreshContact() {
        // 获取数据
        List<UserInfo> contacts = Model.getInstance().getDBManager().getContactTableDao().getContacts();
        // 校验
        if (contacts != null && !contacts.isEmpty()) {
            // 设置数据
            Map<String, EaseUser> contactsMap = new HashMap<>();
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getId());
                contactsMap.put(contact.getId(), easeUser);
            }
            setContactsMap(contactsMap);
            refresh();
        }
    }

    // 长按删除
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // 获取id
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);
        Id = easeUser.getUsername();
        // 添加布局
        Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.delete, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact_delete) {
            deleteContact();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // 删除选中联系人
    private void deleteContact() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                EMClient.getInstance().contactManager().deleteContact(Id);
                Model.getInstance().getDBManager().getContactTableDao().deleteContactById(Id);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                    refreshContact();
                });
            } catch (HyphenateException e) {
                e.printStackTrace();
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "删除失败！", Toast.LENGTH_SHORT).show());
            }
            refreshContact();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        localBroadcastManager.unregisterReceiver(ContactInviteChangeReceiver);
        localBroadcastManager.unregisterReceiver(ContactChangeReceiver);
        localBroadcastManager.unregisterReceiver(GroupChangeReceiver);
    }
}
