package com.yiyekeji.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/10/13.
 */
public class ChatMessage implements Serializable {
    private String sender;

    public String getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "sender='" + sender + '\'' +
                ", Receiver='" + Receiver + '\'' +
                ", date='" + date + '\'' +
                ", isSingle='" + isSingle + '\'' +
                ", messageType='" + messageType + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(String isSingle) {
        this.isSingle = isSingle;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    private String Receiver;

    private String date;
    private String isSingle;
    private String messageType;
    private byte[] content;

}
