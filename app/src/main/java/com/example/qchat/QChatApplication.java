package com.example.qchat;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.example.qchat.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

public class QChatApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化并设置服务器参数
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this, options);
        // 初始化模型层
        Model.getInstance().init(this);
        // 初始化全局上下文
        ctx = this;
    }

    // 获取全局上下文
    public static Context getGlobalApplication() {
        return ctx;
    }
}
