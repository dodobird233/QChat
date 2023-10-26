package com.example.qchat.controller.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qchat.R;
import com.example.qchat.model.Model;
import com.example.qchat.model.bean.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class LoginActivity extends Activity {
    private EditText et_login_name;// 输入的用户名
    private EditText et_login_pwd;// 输入的密码
    private Button bt_login_register;// 注册按钮
    private Button bt_login_login;// 登录按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void initView() {
        et_login_name = findViewById(R.id.et_login_name);
        et_login_pwd = findViewById(R.id.et_login_pwd);
        bt_login_register = findViewById(R.id.bt_login_register);
        bt_login_login = findViewById(R.id.bt_login_login);
    }

    private void initListener() {
        // 注册按钮
        bt_login_register.setOnClickListener(v -> register());
        // 登录按钮
        bt_login_login.setOnClickListener(v -> login());
    }

    private void login() {
        // 获取输入的用户名和密码
        String loginName = et_login_name.getText().toString();
        String loginPwd = et_login_pwd.getText().toString();

        // 校验
        if (TextUtils.isEmpty(loginName) || TextUtils.isEmpty(loginPwd)) {
            Toast.makeText(LoginActivity.this, "用户名和密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 服务器验证
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 请求服务器登录
            EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                @Override
                public void onSuccess() {
                    // 通知Model层用户已经切换，创建或加载相应数据库
                    Model.getInstance().loginSuccess(new UserInfo(loginName));
                    // 保存用户信息到数据库
                    Model.getInstance().getUSerAccountDao().addAccount(new UserInfo(loginName));
                    // UI层线程处理
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });
                }

                @Override
                public void onError(int i, String s) {
                    // 登录失败
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "登录失败：" + s, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onProgress(int i, String s) {
                    // 登录中
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "正在请求服务器..." + s, Toast.LENGTH_SHORT).show());
                }
            });
        });
    }


    private void register() {
        // 获取输入的用户名和密码
        String registerName = et_login_name.getText().toString();
        String registerPwd = et_login_pwd.getText().toString();
        // 校验
        if (TextUtils.isEmpty(registerName) || TextUtils.isEmpty(registerPwd)) {
            Toast.makeText(LoginActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // 开始注册
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 请求服务器注册账号
            try {
                EMClient.getInstance().createAccount(registerName, registerPwd);
                // 注册成功
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "注册成功！", Toast.LENGTH_SHORT).show());
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "注册失败：" + e, Toast.LENGTH_SHORT).show());
            }
        });
    }
}