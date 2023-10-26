package com.example.qchat.model.bean;

public class PeopleChosen {
    private UserInfo user;
    boolean isChosen;

    public PeopleChosen(UserInfo user, boolean isChosen) {
        this.user = user;
        this.isChosen = isChosen;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }
}
