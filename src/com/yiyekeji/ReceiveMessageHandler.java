package com.yiyekeji;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
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
import com.lxl.im.utils.ManageUtil;
import com.yiyekeji.bean.ChatMessage;

import handler.LoginHandler;
import handler.SendMessageHandler;

@ServerEndpoint(value = "/Chat")
public class ReceiveMessageHandler extends BaseChat{
    /**
     * ���Ӷ��󼯺�
     */
    private String nickName;
    Session session;
    /**
     * WebSocket Session
     */

    public ReceiveMessageHandler() {
    }
    /**
     * ������
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
    	this.session=session;
    	String message = String.format("System %s %s", session.getId(),
                " has joined.");
        System.out.println(message);
        ReceiveMessageHandler.broadCast(message);
    }
    
  
    
    /**
     * �Ƚ���JSON����
     * @param message
     */
    @OnMessage
    public void receiveMessage(byte[] message){
    	String jsonString;
    	JSONObject jsonObject;
		try {
			jsonString = new String(message,"utf-8").trim();
//			LogUtil.d(jsonString.length());
			jsonObject= new JSONObject(jsonString);
			MessageType type=MessageType.valueOf(jsonObject.getString(ConstantUtil.MESSAG_TYPE));
	    	switch (type) {
			case TextMessage:
				LogUtil.d(jsonObject.getString(ConstantUtil.CONTENT));
				new SendMessageHandler().sendMessage(jsonObject,message);
				break;
			case ImageMessage:
				String imgString=jsonObject.getString(ConstantUtil.CONTENT);
				stringToImage(imgString.getBytes(),"C:/xx.jpg");
				break;
			case Login:
				String username=jsonObject.getString(ConstantUtil.USER_NAME);
				String password=jsonObject.getString(ConstantUtil.PASSWORD);
				String  receiverId=new LoginHandler().SignIn(session,username, password);
				if(receiverId!=null){
					jsonObject.put(ConstantUtil.RESULT,true);
					//Ӧ�����������ͺ����б� ����δ������Ϣ
					SendMessageHandler sh=new SendMessageHandler(session);
					sh.sendLinkMan(receiverId);
					sh.sendUnReceiverMessage(receiverId);
				}
				break;
			default:
				break;
			}
		} catch (UnsupportedEncodingException | JSONException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * ���͵�ָ��session
     * @param message
     */
    private void relay(byte[] message) {
    	ByteBuffer buf = ByteBuffer.wrap(message);
    	/*for(BaseChat chat:ManageUtil.chatList){
//    		if (getSession().getId().equals(chat.getSession().getId())) {
    			try {
					chat.getSession().getBasicRemote().sendText(new String(message));
				} catch (IOException e) {
					e.printStackTrace();
				};
//			}
    	}*/
		
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
    	ManageUtil.chatList.remove(this);
        System.out.println(throwable.getMessage());
    }

    /**
     * ���ͻ�㲥��Ϣ
     * @param message
     */
    private static void broadCast(String message) {
    /*    for (Chat chat : connections) {
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
        }*/
    }
    /**
     * �ر�����
     */
    @OnClose
    public void onClose() {
    	ManageUtil.chatList.remove(session);
    	LogUtil.d(ManageUtil.chatList.size());
        String message = String.format("System> %s, %s", session.getId(),
                " has disconnection.");
    }
}