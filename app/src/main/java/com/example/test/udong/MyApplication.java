package com.example.test.udong;

import android.app.Application;

public class MyApplication extends Application {
    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserArea() {
        return userArea;
    }

    public void setUserArea(String userArea) {
        this.userArea = userArea;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String userNo;
    private String userName;
    private String userArea;
    private String nickname;

}
