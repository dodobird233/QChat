package com.example.qchat.model.bean;

public class UserInfo {
    private String name; // 用户名
    private String id; // 用户id
    private String nickname; // 昵称
    private String avatar; // 头像

    public UserInfo(){

    }

    public UserInfo(String name) {
        this.name = name;
        this.id = name;
        this.nickname = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", nickname='" + nickname + '\'' +
                ", photo='" + avatar + '\'' +
                '}';
    }
}

