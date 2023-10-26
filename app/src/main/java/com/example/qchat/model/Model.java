package com.example.qchat.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.qchat.model.bean.UserInfo;
import com.example.qchat.model.DAO.UserInfoDao;
import com.example.qchat.model.db.DBManager;

//所有数据经过该层
//单例模式
public class Model {
    private static Model model=new Model();
    private Context ctx;
    private DBManager dbManager;

    //线程池
    private ExecutorService executors = Executors.newCachedThreadPool();
    private UserInfoDao userInfoDao;

    //私有化构造
    private Model(){

    }
    //获取单例对象
    public static Model getInstance(){
        return model;
    }

    public void init(Context context){
        ctx = context;

        //创建用户账号数据库的操作类对象
        userInfoDao = new UserInfoDao(ctx);

        //开启全局监听
        EventListener eventListener = new EventListener(ctx);
    }
    // 获取全局线程池
    public ExecutorService getGlobalThreadPool(){
        return executors;
    }

    // 登录成功后的处理方法
    public void loginSuccess(UserInfo account){
        if(account == null) return;

        if(dbManager != null)
            dbManager.close();

        dbManager = new DBManager(ctx,account.getName());
    }

    public DBManager getDBManager(){
        return dbManager;
    }

    //获取用户账号数据库的操作类的对象
    public UserInfoDao getUSerAccountDao(){
        return userInfoDao;
    }


}

