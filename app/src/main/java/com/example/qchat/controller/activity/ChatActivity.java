package com.example.qchat.controller.activity;


import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.qchat.R;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;

import java.util.Objects;

// 会话主页面
public class ChatActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
    }

    private void initData() {
        // 创建会话fragment
        EaseChatFragment easeChatFragment = new EaseChatFragment();

        easeChatFragment.setArguments(getIntent().getExtras());

        // 替换fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_chat, easeChatFragment).commit();
    }
}
