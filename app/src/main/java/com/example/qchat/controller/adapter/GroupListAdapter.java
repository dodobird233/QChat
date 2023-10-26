package com.example.qchat.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.qchat.R;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

// 群列表适配器
public class GroupListAdapter extends BaseAdapter {
    private Context ctx;
    private List<EMGroup> emGroupList=new ArrayList<>();
    public GroupListAdapter(Context ctx){
        this.ctx=ctx;
    }
    public void refresh(List<EMGroup> groupList){
        if(groupList!=null&&!groupList.isEmpty()){
            emGroupList.clear();
            emGroupList.addAll(groupList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if(emGroupList==null) return 0;
        return emGroupList.size();
    }

    @Override
    public Object getItem(int i) {
        return emGroupList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 获取viewholer
        ViewHolder viewHolder;
        if(view==null){
            viewHolder=new ViewHolder();
            view=View.inflate(ctx, R.layout.item_group_list,null);
            viewHolder.name=view.findViewById(R.id.tv_group_list_name);
            view.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) view.getTag();
        }
        // 获取item数据
        EMGroup emGroup=emGroupList.get(i);
        // 显示数据
        viewHolder.name.setText(emGroup.getGroupName());
        // 返回数据
        return view;
    }

    private class ViewHolder{
        TextView name;
    }
}
