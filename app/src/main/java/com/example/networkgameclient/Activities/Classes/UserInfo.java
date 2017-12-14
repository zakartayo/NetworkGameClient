package com.example.networkgameclient.Activities.Classes;

/**
 * Created by 이승헌 on 2017-12-07.
 */

public class UserInfo {
    private String nickname = "";

    public UserInfo(String name) {
        this.nickname = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
