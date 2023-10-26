package com.example.qchat.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.qchat.R;
import com.example.qchat.model.bean.InvitationInfo;
import com.example.qchat.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class InviteAdapter extends BaseAdapter {
    private Context ctx;
    private List<InvitationInfo> invitationInfos = new ArrayList<>();
    private OnInviteListener onInviteListener;
    private InvitationInfo invitationInfo;

    public InviteAdapter(Context context, OnInviteListener OnInviteListener) {
        ctx = context;
        onInviteListener = OnInviteListener;
    }

    public void refresh(List<InvitationInfo> invitationInfoList) {
        if (invitationInfoList != null && !invitationInfoList.isEmpty()) {
            this.invitationInfos.clear();
            this.invitationInfos.addAll(invitationInfoList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return invitationInfos == null ? 0 : invitationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return invitationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // 创建ViewHolder
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            // 获取item布局
            view = View.inflate(ctx, R.layout.item_invite, null);
            viewHolder.name = view.findViewById(R.id.tv_invite_name);
            viewHolder.reason = view.findViewById(R.id.tv_invite_reason);
            viewHolder.accept = view.findViewById(R.id.bt_invite_accept);
            viewHolder.reject = view.findViewById(R.id.bt_invite_reject);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        // 获取item数据
        invitationInfo = invitationInfos.get(position);
        // 显示item数据
        UserInfo user = invitationInfo.getUser();
        if (user != null) {// 个人邀请
            // 展示用户名
            viewHolder.name.setText(invitationInfo.getUser().getName());
            viewHolder.accept.setVisibility(View.GONE);
            viewHolder.reject.setVisibility(View.GONE);

            //原因
            if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.NEW_INVITE) {
                viewHolder.reason.setText(invitationInfo.getReason() == null ? "请求添加好友" : invitationInfo.getReason());
                viewHolder.accept.setVisibility(View.VISIBLE);
                viewHolder.reject.setVisibility(View.VISIBLE);
            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT) {
                if (invitationInfo.getReason() == null) {
                    viewHolder.reason.setText("同意");
                } else {
                    viewHolder.reason.setText(invitationInfo.getReason());
                }
            } else if (invitationInfo.getStatus() == InvitationInfo.InvitationStatus.INVITE_ACCEPT_BY_PEER) {
                if (invitationInfo.getReason() == null) {
                    viewHolder.reason.setText("已被接受");
                } else {
                    viewHolder.reason.setText(invitationInfo.getReason());
                }
            }
            // 处理点击事件
            viewHolder.accept.setOnClickListener(v -> onInviteListener.onAccept(invitationInfo));
            viewHolder.reject.setOnClickListener(v -> onInviteListener.onReject(invitationInfo));
        } else {//群组
            viewHolder.name.setText(invitationInfo.getGroup().getInvitePerson());
            // 默认隐藏，只在待处理状态下设置为可见
            viewHolder.accept.setVisibility(View.GONE);
            viewHolder.reject.setVisibility(View.GONE);
            switch(invitationInfo.getStatus()){
                case GROUP_APPLICATION_ACCEPTED:
                    viewHolder.reason.setText("您的群申请被接受");
                    break;
                case GROUP_APPLICATION_DECLINED:
                    viewHolder.reason.setText("您的群申请被拒绝");
                    break;
                case GROUP_ACCEPT_APPLICATION:
                    viewHolder.reason.setText("您已同意该申请");
                    break;
                case GROUP_REJECT_APPLICATION:
                    viewHolder.reason.setText("您已拒绝该申请");
                    break;
                case GROUP_ACCEPT_INVITE:
                    viewHolder.reason.setText("您已同意该邀请");
                    break;
                case GROUP_REJECT_INVITE:
                    viewHolder.reason.setText("您已拒绝该邀请");
                    break;
                case GROUP_INVITE_DECLINED:
                    viewHolder.reason.setText("您的邀请被拒绝");
                    break;
                case GROUP_INVITE_ACCEPTED:
                    viewHolder.reason.setText("您的邀请被同意");
                    break;
                case NEW_GROUP_INVITE:
                    viewHolder.accept.setVisibility(View.VISIBLE);
                    viewHolder.reject.setVisibility(View.VISIBLE);
                    // 设置点击事件
                    viewHolder.accept.setOnClickListener(view12 -> onInviteListener.onInviteAccept(invitationInfo));
                    viewHolder.reject.setOnClickListener(view1 -> onInviteListener.onInviteReject(invitationInfo));
                    viewHolder.reason.setText("邀请您加入群");
                    break;
                case NEW_GROUP_APPLICATION:
                    viewHolder.accept.setVisibility(View.VISIBLE);
                    viewHolder.reject.setVisibility(View.VISIBLE);
                    // 设置点击事件
                    viewHolder.accept.setOnClickListener(view13 -> onInviteListener.onApplicationAccept(invitationInfo));
                    viewHolder.reject.setOnClickListener(view14 -> onInviteListener.onApplicationReject(invitationInfo));
                    viewHolder.reason.setText("申请加入群");
                    break;
                default:
                    viewHolder.reason.setText("--------");
            }
        }
        //4.返回view
        return view;
    }

    private class ViewHolder {
        private TextView name;
        private TextView reason;
        private Button accept;
        private Button reject;
    }

    public interface OnInviteListener {
        // 联系人接受
        void onAccept(InvitationInfo invitationInfo);

        // 联系人拒绝
        void onReject(InvitationInfo invitationInfo);

        // 接受邀请
        void onInviteAccept(InvitationInfo invitationInfo);

        // 拒绝邀请
        void onInviteReject(InvitationInfo invitationInfo);

        // 同意申请

        void onApplicationAccept(InvitationInfo invitationInfo);
        // 拒绝申请
        void onApplicationReject(InvitationInfo invitationInfo);
    }
}
