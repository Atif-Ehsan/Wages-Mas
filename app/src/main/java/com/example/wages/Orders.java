package com.example.wages;

public class Orders {
    String myId,receiver,status,myName;

    public Orders(String myId, String receiver,String myName, String status) {
        this.myId = myId;
        this.receiver = receiver;
        this.status = status;
        this.myName = myName;
    }
    public Orders(){}

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }
}
