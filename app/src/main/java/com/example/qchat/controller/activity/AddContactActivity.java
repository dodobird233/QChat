package com.example.qchat.controller.activity;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qchat.R;
import com.example.qchat.model.Model;
import com.example.qchat.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddContactActivity extends Activity {
    private RelativeLayout rl_add;
    private Button bt_add_add;
    private ImageView tv_add_find;
    private EditText et_add_name;
    private TextView tv_add_name;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        initView();
        initListener();
    }
    private void initView() {
        tv_add_find = findViewById(R.id.tv_add_find);
        et_add_name = findViewById(R.id.et_add_name);
        rl_add = findViewById(R.id.rl_add);
        tv_add_name = findViewById(R.id.tv_add_name);
        bt_add_add = findViewById(R.id.bt_add_add);
    }

    private void initListener() {
        // 查找
        tv_add_find.setOnClickListener(v -> find());
        // 添加
        bt_add_add.setOnClickListener(v -> add());
    }

    private void find() {
        // 获取输入用户名
        final String name = et_add_name.getText().toString();
        // 校验输入的用户名
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(AddContactActivity.this, "输入的用户名不能为空", Toast.LENGTH_SHORT).show();
        } else {
            // todo 去服务器判断当前用户是否存在
            // 当前默认存在
            Model.getInstance().getGlobalThreadPool().execute(() -> {
                userInfo = new UserInfo(name);
                // 更新候选用户显示
                runOnUiThread(() -> {
                    rl_add.setVisibility(View.VISIBLE);
                    tv_add_name.setText(userInfo.getName());
                });
            });
        }
    }


    private void add() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            try {
                EMClient.getInstance().contactManager().addContact(userInfo.getName(), "加个好友吧~");
                runOnUiThread(() -> Toast.makeText(AddContactActivity.this, "发送好友邀请成功", Toast.LENGTH_SHORT).show());
                finish();
            } catch (final HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(AddContactActivity.this, "发送好友邀请失败" + e, Toast.LENGTH_SHORT).show());
            }
        });
    }

}