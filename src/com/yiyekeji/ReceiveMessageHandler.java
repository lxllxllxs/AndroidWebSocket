package com.yiyekeji;
import imenum.ChatMessageType;
import imenum.MainType;
import imenum.SysMessType;

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
import com.google.protobuf.InvalidProtocolBufferException;
import com.lxl.im.utils.ConstantUtil;
import com.lxl.im.utils.EnumUtil.MessageType;
import com.lxl.im.utils.LogUtil;
import com.lxl.im.utils.ManageUtil;
import com.yiyekeji.bean.IMessageFactory;
import com.yiyekeji.bean.IMessageFactory.IMessage.Builder;
import com.yiyekeji.bean.IMessageFactory.IMessage.User;

import handler.LoginHandler;
import handler.SendMessageHandler;

@ServerEndpoint(value = "/Chat")
public class ReceiveMessageHandler extends BaseChat{
    /**
     * 连接对象集合
     */
    private String nickName;
    private String  userId;
    Session session;
    /**
     * WebSocket Session
     */

    public ReceiveMessageHandler() {
    }
    /**
     * 打开连接
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
     * @param message
     */
    @OnMessage
    public void receiveMessage(byte[] message){
    	SendMessageHandler sh1=new SendMessageHandler(session);
    	IMessageFactory.IMessage iMessage=null;
		try {
			iMessage = IMessageFactory.IMessage.parseFrom(message);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
    	if(iMessage.getMainType().equals("0")){
    		switch(iMessage.getSubType()){
    		case "0":
    			Builder buidler=IMessageFactory.IMessage.newBuilder();
    			User user=iMessage.getUser(0);
    			userId=new LoginHandler().SignIn(session,user.getUsername(),user.getPassword());
				if(userId!=null){
					//重构信息
					iMessage=buidler.setResult("1").mergeFrom(iMessage).build();
				}else{
					iMessage=buidler.setResult("0").mergeFrom(iMessage).build();
					iMessage.newBuilder().setResult("0");
				}
				ByteBuffer bb=ByteBuffer.wrap(iMessage.toByteArray());
				
				try {
					session.getBasicRemote().sendBinary(bb);
				} catch (IOException e) {
					e.printStackTrace();
				}
    			break;
    		case "1":
    			sh1.sendLinkMan(userId,iMessage);
    			break;
    		case "2":
    			//应该在这里推送未接收消息
    			sh1.sendUnReceiMessage(userId,iMessage);
    			break;
    		}
    	}else if(iMessage.getMainType().equals("1")){
    		switch(iMessage.getSubType()){
    		case "0":
    			sh1.sendMessage(iMessage);
    			break;
    		}
    	}
    	
	
    }
    
    
    /**
     * 发送到指定session
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
     * 数组转对象 
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
     *不想 通过BASE64Decoder解码，并生成图片 
     * @param imgStr 解码后的string 
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
     * 错误信息响应
     * @param throwable
     */
    @OnError
    public void onError(Throwable throwable) {
    	ManageUtil.chatList.remove(this);
        System.out.println(throwable.getMessage());
    }

    /**
     * 发送或广播信息
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
     * 关闭连接
     */
    @OnClose
    public void onClose() {
    	ManageUtil.chatList.remove(session);
    	LogUtil.d(ManageUtil.chatList.size());
        String message = String.format("System> %s, %s", session.getId(),
                " has disconnection.");
    }
}