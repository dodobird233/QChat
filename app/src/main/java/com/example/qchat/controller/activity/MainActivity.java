package com.example.qchat.controller.activity;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.qchat.R;
import com.example.qchat.controller.fragment.ChatFragment;
import com.example.qchat.controller.fragment.ContactListFragment;
import com.example.qchat.controller.fragment.MeFragment;
import com.example.qchat.controller.fragment.MomentFragment;

public class MainActivity extends FragmentActivity {

    private RadioGroup rg_main;
    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private MeFragment meFragment;
    private MomentFragment momentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();
        initListener();
    }

    private void initView() {
        rg_main = findViewById(R.id.rg_main);
    }

    private void initDate() {
        // 创建三个Fragment对象
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        meFragment = new MeFragment();
        momentFragment=new MomentFragment();
    }

    @SuppressLint("NonConstantResourceId")
    private void initListener() {
        //RadioGroup的选择事件
        rg_main.setOnCheckedChangeListener((group, checkedId) -> {
            Fragment fragment = null;
            switch (checkedId) {
                // 会话列表
                case R.id.rb_main_chat:
                    fragment = chatFragment;
                    break;
                // 联系人列表
                case R.id.rb_main_contact:
                    fragment = contactListFragment;
                    break;
                // 个人主页
                case R.id.rb_main_setting:
                    fragment = meFragment;
                    break;
                // 朋友圈
                case R.id.rb_main_discover:
                    fragment = momentFragment;
                    break;
            }
            // 实现fragment切换的方法，选择界面
            switchFragment(fragment);
        });
        // 默认选择会话列表页面
        rg_main.check(R.id.rb_main_chat);
    }

    // 实现fragment切换的方法
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //开启事务替换fragment
        fragmentManager.beginTransaction().replace(R.id.fl_main, fragment).commit();
    }

}
