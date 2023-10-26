package com.example.qchat.controller.activity;

import androidx.annotation.NonNull;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.qchat.R;
import com.example.qchat.model.Model;
import com.example.qchat.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

public class PendingActivity extends Activity {


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (isFinishing()) return;
            // 根据是否登录过，判断进入主页面还是登录页面
            MainOrLoginActivity();
        }
    };

    private void MainOrLoginActivity() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 请求服务端该用户是否登录过
            if (EMClient.getInstance().isLoggedInBefore()) {
                // 获取当前登录用户的登录信息
                UserInfo usr = Model.getInstance().getUSerAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                if (usr == null) {
                    // 跳转到登录页面
                    Intent intent = new Intent(PendingActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Model.getInstance().loginSuccess(usr);
                    // 跳转到主页面
                }
                // 跳转到主页面
                Intent intent = new Intent(PendingActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // 跳转到登录页面
                Intent intent = new Intent(PendingActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            // 结束当前页
            finish();
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        // 开屏界面延时 2 秒
        handler.sendMessageDelayed(Message.obtain(), 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}