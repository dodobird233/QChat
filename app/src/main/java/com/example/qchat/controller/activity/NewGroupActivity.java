package com.example.qchat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.qchat.R;
import com.example.qchat.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

public class NewGroupActivity extends Activity {
    private EditText et_new_group_name;
    private EditText et_new_group_description;

    private CheckBox cb_new_group_public;

    private Button bt_new_group_create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        initView();
        initListener();
    }

    private void initListener() {
        bt_new_group_create.setOnClickListener(view -> {
            Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {// 创建群成功
            createGroup(intent.getStringArrayExtra("members"));
        }
    }

    private void createGroup(String[] members) {
        String name = et_new_group_name.getText().toString();
        String desc = et_new_group_description.getText().toString();

        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 配置群信息
            EMGroupOptions ops = new EMGroupOptions();
            EMGroupManager.EMGroupStyle groupStyle = null;
            if (cb_new_group_public.isChecked()) {// 开放加入
                groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
            }
            ops.maxUsers = 100;
            ops.style = groupStyle;
            // 请求服务器创建群
            try {
                EMClient.getInstance().groupManager().createGroup(name, desc, members, "申请加群", ops);
                runOnUiThread(() -> Toast.makeText(NewGroupActivity.this, "创建群聊成功！", Toast.LENGTH_SHORT).show());
            } catch (HyphenateException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(NewGroupActivity.this, "创建群聊失败！", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void initView() {
        et_new_group_name = findViewById(R.id.et_new_group_name);
        et_new_group_description = findViewById(R.id.et_new_group_description);
        cb_new_group_public = findViewById(R.id.cb_new_group_public);
        bt_new_group_create = findViewById(R.id.bt_new_group_create);
    }
}
