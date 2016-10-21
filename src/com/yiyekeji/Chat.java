package com.yiyekeji;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import sun.misc.BASE64Decoder;

import com.google.gson.Gson;
import com.yiyekeji.bean.ChatMessage;

@ServerEndpoint(value = "/Chat")
public class Chat {

    /**
     * 连接对象集合
     */
    private static final Set<Chat> connections = new CopyOnWriteArraySet<Chat>();

    private String nickName;

    /**
     * WebSocket Session
     */
    private Session session;

    public Chat() {
    }

    /**
     * 打开连接
     * 
     * @param session
     * @param nickName
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        this.session.setMaxBinaryMessageBufferSize(10*1024*1024);
        connections.add(this);
        String message = String.format("System> %s %s", this.nickName,
                " has joined.");
        Chat.broadCast(message);
        
    }

    public void parseSession(){
    	
    }
    
    /**
     * 先解析JSON数据
     * @param message
     */
    @OnMessage
    public void receiveMessage(byte[] message){
    	ChatMessage chatmessage=(ChatMessage)toObject(message);
    	switch (chatmessage.getChatType()) {
		case "0":
			stringToImage(chatmessage.getContent(),"C:/xx.jpg");
			break;
		case "1":
			String text;
			try {
				text = new String(chatmessage.getContent(),"UTF-8");
				System.out.println(text);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
    }
    
    
    /**  
     * 数组转对象  
     * @param bytes  
     * @return  
     */  
    public Object toObject (byte[] bytes) {   
    	System.out.println(bytes.length);
        Object obj = null;      
        try {        
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);        
            ObjectInputStream ois = new ObjectInputStream (bis);        
            obj = ois.readObject();      
            ois.close();   
            bis.close();   
        } catch (IOException ex) {        
            ex.printStackTrace();   
        } catch (ClassNotFoundException ex) {        
            ex.printStackTrace();   
        }      
        return obj;    
    }   

    /** 
     *不想 通过BASE64Decoder解码，并生成图片 
     * @param imgStr 解码后的string 
     */  
    public boolean stringToImage(byte[] imgStr, String imgFilePath) {  
     /*   // 对字节数组字符串进行Base64解码并生成图片  
        if (imgStr == null)  
            return false;  
        try {  
            // Base64解码  
            byte[] b = new BASE64Decoder().decodeBuffer(imgStr);  
            for (int i = 0; i < b.length; ++i) {  
                if (b[i] < 0) {  
                    // 调整异常数据  
                    b[i] += 256;  
                }  
            }  */
            // 生成Jpeg图片  
            OutputStream out;
			try {
				out = new FileOutputStream(imgFilePath);
			    out.write(imgStr);  
	            out.flush();  
	            out.close();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
        
            return true;  
    }     
    
    
    /**
     * 错误信息响应
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    /**
     * 发送或广播信息
     * @param message
     */
    private static void broadCast(String message) {
        for (Chat chat : connections) {
            try {
                synchronized (chat) {
                    chat.session.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                connections.remove(chat);
                try {
                    chat.session.close();
                } catch (IOException e1) {
                }
                Chat.broadCast(String.format("System> %s %s", chat.nickName,
                        " has bean disconnection."));
            }
        }
    }
    /**
     * 关闭连接
     */
    @OnClose
    public void onClose() {
        connections.remove(this);
        String message = String.format("System> %s, %s", this.nickName,
                " has disconnection.");
    }
}