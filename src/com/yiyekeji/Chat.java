package com.yiyekeji;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.json.JSONException;
import org.json.JSONObject;

import sun.misc.BASE64Decoder;

import com.google.gson.Gson;
import com.lxl.im.utils.ConstantUtil;
import com.lxl.im.utils.EnumUtil.MessageType;
import com.lxl.im.utils.LogUtil;
import com.yiyekeji.bean.ChatMessage;

@ServerEndpoint(value = "/Chat")
public class Chat {
    /**
     * ���Ӷ��󼯺�
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
     * ������
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
        System.out.println(message);
        Chat.broadCast(message);
        
    }

    public void parseSession(){
    	
    }
    
    /**
     * �Ƚ���JSON����
     * @param message
     */
    @OnMessage
    public void receiveMessage(byte[] message){
    	LogUtil.d(message.length);
    	String jsonString;
    	JSONObject jsonObject;
		try {
			jsonString = new String(message,"utf-8").trim();
			LogUtil.d(jsonString.length());
			jsonObject= new JSONObject(jsonString);
			LogUtil.d("no problem");
			MessageType type=MessageType.valueOf(jsonObject.getString(ConstantUtil.MESSAG_TYPE));
	    	switch (type) {
			case TextMessage:
				LogUtil.d(jsonObject.getString(ConstantUtil.CONTENT));
				break;
			case ImageMessage:
				String imgString=jsonObject.getString(ConstantUtil.CONTENT);
				stringToImage(imgString.getBytes(),"C:/xx.jpg");
				break;
			default:
				break;
			}
			
		} catch (UnsupportedEncodingException | JSONException e) {
			e.printStackTrace();
		}
    }
    
    
    
    /**
     * �Ƚ���JSON����
     * @param message
     */
    @OnMessage
    public void receiveTextMessage(String message){
    	System.out.println(new String (message));
    }
    
    /** 
     * ����ת���� 
     * @param bytes 
     * @return 
     */  
    public JSONObject toObject (byte[] bytes) {     
        JSONObject obj = null;     
        try {       
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);       
            ObjectInputStream ois = new ObjectInputStream (bis);       
            obj = (JSONObject)ois.readObject();     
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
     *���� ͨ��BASE64Decoder���룬������ͼƬ 
     * @param imgStr ������string 
     */  
    public boolean stringToImage(byte[] imgStr, String imgFilePath) {  
     /*   // ���ֽ������ַ�������Base64���벢����ͼƬ  
        if (imgStr == null)  
            return false;  
        try {  
            // Base64����  
            byte[] b = new BASE64Decoder().decodeBuffer(imgStr);  
            for (int i = 0; i < b.length; ++i) {  
                if (b[i] < 0) {  
                    // �����쳣����  
                    b[i] += 256;  
                }  
            }  */
            // ����JpegͼƬ  
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
     * ������Ϣ��Ӧ
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    /**
     * ���ͻ�㲥��Ϣ
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
     * �ر�����
     */
    @OnClose
    public void onClose() {
        connections.remove(this);
        String message = String.format("System> %s, %s", this.nickName,
                " has disconnection.");
    }
}