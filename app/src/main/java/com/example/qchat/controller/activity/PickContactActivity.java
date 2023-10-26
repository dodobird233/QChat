package com.example.qchat.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qchat.R;
import com.example.qchat.controller.adapter.PickContactAdapter;
import com.example.qchat.model.Model;
import com.example.qchat.model.bean.PeopleChosen;
import com.example.qchat.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;


public class PickContactActivity extends Activity {
    private TextView tv_pick_save;

    private ListView lv_pick;

    private List<PeopleChosen> peopleChosenList;
    private PickContactAdapter pickContactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        // 联系人条目点击事件
        lv_pick.setOnItemClickListener((adapterView, view, i, l) -> {
            CheckBox cb=view.findViewById(R.id.cb_pick);
            cb.setChecked(!cb.isChecked());
            PeopleChosen peopleChosen = peopleChosenList.get(i);
            peopleChosen.setChosen(cb.isChecked());
            pickContactAdapter.notifyDataSetChanged();
        });
        // 保存按钮的点击时间
        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> list=pickContactAdapter.getChosenPeople();
                Intent intent = new Intent();
                // 添加到数据库表
                intent.putExtra("members",list.toArray(new String[0]));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initData() {
        List<UserInfo> contacts = Model.getInstance().getDBManager().getContactTableDao().getContacts();
        peopleChosenList = new ArrayList<>();
        if (contacts != null && !contacts.isEmpty()) {
            for (UserInfo usr : contacts) {
                PeopleChosen peopleChosen = new PeopleChosen(usr, false);
                peopleChosenList.add(peopleChosen);
            }
        }
        pickContactAdapter = new PickContactAdapter(this, peopleChosenList);
        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initView() {
        tv_pick_save = findViewById(R.id.tv_pick_save);
        lv_pick = findViewById(R.id.lv_pick);
    }
}
