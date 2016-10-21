package com.yiyekeji.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/13.
 */
public class ChatMessage implements Serializable {
    public static final int SEND_TYPE = 1;                    //type
    public static final int RECEIVE_TYPE = 0;
    public static final int FAILURE_STATE = -1;               //state
    public static final int SENDING_STATE = 0;
    public static final int SUCCESS_STATE = 1;
    public static final String SINGLE_CHAT_TYPE = "Single";   //chatType
    public static final String GROUP_CHAT_TYPE = "Group";
    public static final String INFORM_CHAT_TYPE = "Inform";
    public static final String DEFAULT_KIND = "0";            //kind
    public static final String IMAGE_KIND = "1";
    public static final String RED_PACKET_KIND = "2";
    public static final String GET_KIND = "3";
    public static final String RECORD_KIND = "4";
    public static final String AA_KIND = "5";
    //	public static final String TRANSFER_ACCOUNT_KIND = "4";
    public static final int UNREAD = 0;
    public static final int READ = 1;
    public static final String NOTIFICATION_MESSAGE = "通知消息";

    private String id;
    private int type;           //1为发送，0为接收

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGroupSender() {
        return groupSender;
    }

    public void setGroupSender(String groupSender) {
        this.groupSender = groupSender;
    }

    public String getGroupSenderId() {
        return groupSenderId;
    }

    public void setGroupSenderId(String groupSenderId) {
        this.groupSenderId = groupSenderId;
    }

    public String getHeadImagePath() {
        return headImagePath;
    }

    public void setHeadImagePath(String headImagePath) {
        this.headImagePath = headImagePath;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    private int state;          //-1为发送失败，0为正在发送，1为发送成功
    private String chatType;    //“Single”, “Group”
    private String kind;        //“0”普通文字,“1”图片, “2”红包, “3”转账
    private String sender;      //如果ChatType为“Single”，为用户名；如果ChatType为“Group”，为群名；
    private String receiver;    //如果ChatType为“Single”，为用户名；如果ChatType为“Group”，为群名；
    private String date;
    private byte[] content;     //当消息为普通文字，则为消息内容；图片消息，则为本地保存或缓存的地址；其他消息则保持想对应的格式内容
    private String fileName;
    private int isRead;
    private String senderId;
    private String receiverId;
    private String groupSender;
    private String groupSenderId;
    private String headImagePath;
    private int seconds;




    public ChatMessage() {
    }



}
