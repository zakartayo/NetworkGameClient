package com.example.networkgameclient.Activities.Classes;

/**
 * Created by 이승헌 on 2017-11-27.
 */

public class Item {

    int roomNum;
    String roomState;
    Boolean available;
    int numCount;
    String title;
    boolean lock;
    int port;

    public Item(){

    }

    public Item(int roomNum, String roomState, Boolean available, int numCount){
        this.roomNum = roomNum;
        this.roomState = roomState;
        this.available = available;
        this.numCount = numCount;
    }

    public int getRoomNum(){
        return this.roomNum;
    }
    public Boolean getAvailable(){
        return this.available;
    }
    public String getRoomState(){
        return this.roomState;
    }
    public int getNumCount(){
        return this.numCount;
    }
    public String getTitle(){
        return this.title;
    }
    public boolean getLock(){
        return this.lock;
    }
    public int getPort(){
        return this.port;
    }

    public void setRoomNum(int roomNum){
        this.roomNum = roomNum;
    }
    public void setAvailable(Boolean available){
        this.available = available;
    }
    public void setRoomState(String roomState){
        this.roomState = roomState;
    }
    public void setNumCount(int numCount){
        this.numCount = numCount;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setLock(boolean lock){
        this.lock = lock;
    }
    public void setPort(int port){
        this.port = port;
    }

}
