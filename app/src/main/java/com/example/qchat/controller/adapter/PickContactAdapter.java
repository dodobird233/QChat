package com.example.qchat.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.qchat.R;
import com.example.qchat.model.bean.PeopleChosen;

import java.util.ArrayList;
import java.util.List;

// 创建群时选择联系人的适配器
public class PickContactAdapter extends BaseAdapter {

    private Context ctx;
    private List<PeopleChosen> peopleChosenList = new ArrayList<>();

    public PickContactAdapter(Context ctx, List<PeopleChosen> list) {
        this.ctx = ctx;
        if (list != null && !list.isEmpty()) {
            peopleChosenList.clear();
            peopleChosenList.addAll(list);
        }
    }

    @Override
    public int getCount() {
        if (peopleChosenList == null) return 0;
        return peopleChosenList.size();
    }

    @Override
    public Object getItem(int i) {
        return peopleChosenList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(ctx, R.layout.item_pick_contact, null);
            viewHolder.checkBox = view.findViewById(R.id.cb_pick);
            viewHolder.textView = view.findViewById(R.id.tv_pick_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        PeopleChosen peopleChosen = peopleChosenList.get(i);
        viewHolder.textView.setText(peopleChosen.getUser().getName());
        viewHolder.checkBox.setChecked(peopleChosen.isChosen());
        return view;
    }

    public List<String> getChosenPeople(){
        List<String> list=new ArrayList<>();
        for(PeopleChosen people:peopleChosenList){
            if(people.isChosen()){
                list.add(people.getUser().getName());
            }
        }
        return list;
    }
    private class ViewHolder {
        private CheckBox checkBox;
        private TextView textView;
    }
}
