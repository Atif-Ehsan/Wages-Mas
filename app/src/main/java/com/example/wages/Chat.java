package com.example.wages;

public class Chat {
    String sender,receiver,message,is_seen,status;
    public Chat(){}
    public Chat(String sender,String receiver,String message,String is_seen,String status){
        this.sender = sender;
        this.receiver= receiver;
        this.message = message;
        this.is_seen = is_seen;
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIs_seen() {
        return is_seen;
    }

    public void setIs_seen(String is_seen) {
        this.is_seen = is_seen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
