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
import com.yiyekeji.bean.IMessageFactory.IMessage.User;

import handler.LoginHandler;
import handler.SendMessageHandler;

@ServerEndpoint(value = "/Chat")
public class ReceiveMessageHandler extends BaseChat{
    /**
     * ���Ӷ��󼯺�
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
    public void s(byte[] message){
    	String jsonString;
    	JSONObject jsonObject;
		try {
			jsonString = new String(message,"utf-8").trim();
//			LogUtil.d(jsonString.length());
			jsonObject= new JSONObject(jsonString);
			MainType type=MainType.valueOf(jsonObject.getString(MainType.getName()));
	    	switch (type) {
			case ChatMessageType:
				ChatMessageType chatMessageType=ChatMessageType.valueOf(jsonObject.getString(ChatMessageType.getName()));
				switch (chatMessageType) {
					case TextMessage:
						LogUtil.d(jsonObject.getString(ConstantUtil.CONTENT));
						SendMessageHandler sh=new SendMessageHandler();
						sh.sendMessage(jsonObject);
						break;
					case UnReceiveMessage:
						SendMessageHandler sh1=new SendMessageHandler(session);
						sh1.sendUnReceiverMessage(userId);//
						break;
					default:
						break;
					}
				break;
			case SysMessType:
				SysMessType Type=SysMessType.valueOf(jsonObject.getString(SysMessType.getName()));
				switch (Type) {
					case Login:
						String username=jsonObject.getString(ConstantUtil.USER_NAME);
						String password=jsonObject.getString(ConstantUtil.PASSWORD);
						userId=new LoginHandler().SignIn(session,username, password);
						SendMessageHandler sh1=new SendMessageHandler(session);
						if(userId!=null){
							jsonObject.put(ConstantUtil.RESULT,true);
							//Ӧ�����������ͺ����б� ����δ������Ϣ
							sh1.sendLinkMan(userId,username,jsonObject);
						}else{
							jsonObject.put(ConstantUtil.RESULT,false);
						}
						break;
					default:
						break;
					}
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
    public void receiveMessage(byte[] message){
    	IMessageFactory.IMessage iMessage=null;
		try {
			iMessage = IMessageFactory.IMessage.parseFrom(message);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
    	if(iMessage.getMainType().equals("0")){
    		switch(iMessage.getSubType()){
    		case "0":
    			User user=iMessage.getUser(0);
    			userId=new LoginHandler().SignIn(session,user.getUsername(),user.getPassword());
				SendMessageHandler sh1=new SendMessageHandler(session);
				if(userId!=null){
					iMessage.newBuilder().setResult("1");
					ByteBuffer bb=ByteBuffer.wrap(iMessage.toByteArray());
					try {
						session.getBasicRemote().sendBinary(bb);
						//Ӧ�����������ͺ����б� ����δ������Ϣ
//						sh1.sendLinkMan(userId,username,jsonObject);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else{
					iMessage.newBuilder().setResult("0");
				}
    			break;
    		}
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