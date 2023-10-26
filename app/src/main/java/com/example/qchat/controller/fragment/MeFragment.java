package com.example.qchat.controller.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.qchat.R;
import com.example.qchat.controller.activity.LoginActivity;
import com.example.qchat.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.Objects;

public class MeFragment extends Fragment {
    private Button bt_setting_out;
    private TextView tv_me_id;

    private ImageView me_avatar;

    private LinearLayout ll_me_moment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_me, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bt_setting_out = view.findViewById(R.id.bt_setting_out);
        tv_me_id = view.findViewById(R.id.tv_me_id);
        me_avatar = view.findViewById(R.id.me_avatar);
        ll_me_moment = view.findViewById(R.id.ll_me_moment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        // 显示用户名
        tv_me_id.setText("QChat:" + EMClient.getInstance().getCurrentUser());
        // 退出登录
        bt_setting_out.setOnClickListener(v -> Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                // 通知服务器退出登录
                EMClient.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        // 关闭数据库
                        Model.getInstance().getDBManager().close();

                        Objects.requireNonNull(getActivity()).runOnUiThread(() -> {

                            Toast.makeText(getActivity(), "退出成功", Toast.LENGTH_SHORT).show();
                            // 回到登录页面
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            // 关闭当前页面
                            getActivity().finish();
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        Toast.makeText(getActivity(), "退出失败:" + s, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int i, String s) {
                        Toast.makeText(getActivity(), "退出中..." + s, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }));

        // 更换头像
        me_avatar.setOnClickListener(view -> {
            // todo 更换头像功能
        });

        // 跳转到朋友圈
        ll_me_moment.setOnClickListener(view -> {
            // todo 跳转至朋友圈
        });

    }
}
