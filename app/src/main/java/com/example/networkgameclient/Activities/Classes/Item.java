package com.example.networkgameclient.Activities.Classes;

/**
 * Created by 이승헌 on 2017-11-27.
 */

public class Item {

    String roomNum;
    String roomState;
    String state;
    String numCount;

    public String getRoomNum(){
        return this.roomNum;
    }
    public String getState(){
        return this.state;
    }
    public String getRoomState(){
        return this.roomState;
    }
    public String getNumCount(){
        return this.numCount;
    }

    public void setRoomNum(String roomNum){
        this.roomNum = roomNum;
    }
    public void setState(String state){
        this.state = state;
    }
    public void setRoomState(String roomState){
        this.roomState = roomState;
    }
    public void setNumCount(String numCount){
        this.numCount = numCount;
    }

}
