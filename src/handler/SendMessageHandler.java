package handler;

import imenum.ChatMessageType;
import imenum.MainType;
import imenum.SysMessType;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonObject;
import com.lxl.im.utils.ConstantUtil;
import com.lxl.im.utils.EnumUtil.MessageType;
import com.lxl.im.utils.JdbcUtils;
import com.lxl.im.utils.LogUtil;
import com.lxl.im.utils.ManageUtil;
import com.mysql.jdbc.log.Log;
import com.yiyekeji.bean.IMessageFactory.IMessage;
import com.yiyekeji.bean.IMessageFactory.IMessage.User;
import com.yiyekeji.bean.IMessageFactory.IMessage.User.Builder;

import jdk.nashorn.internal.scripts.JS;

public class SendMessageHandler {
	public SendMessageHandler(){
	}
	private Session session;
	public SendMessageHandler(Session session){
		this.session=session;
	}
	
	/**
	 * 发送单人聊天信息
	 * 若离线则保存到数据的未发送消息表
	 * @param jsonObject
	 * @param payload
	 */
	public void sendMessage(IMessage iMessage){
		
		for(Map.Entry<String,Session> item:ManageUtil.chatList.entrySet()){
			try {
				if(item.getKey().equals(iMessage.getReceiverId())){
					item.getValue().getBasicRemote().sendBinary(ToByteBuffer(iMessage));
					return;
				}
				LogUtil.d("该用户未登录"+iMessage.getReceiverId());
				//在这里把消息存进 数据库
				JdbcUtils.insertIntoUnsend(iMessage.getReceiverId(),iMessage.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 发送离线信息
	 * 主类型 聊天信息
	 * 子类型 文本消息
	 * 单条发送
	 */
	public void sendUnReceiverMessage(String userId){
		ResultSet rs=JdbcUtils.queryUnSendMessage(userId);
		JSONObject jsonObject=null;
		ArrayList<JSONObject> list=new ArrayList<JSONObject>();
		try {
			while(rs.next()){
				jsonObject=new JSONObject(rs.getString("content"));
				jsonObject.put(MainType.getName(), MainType.ChatMessageType);
				jsonObject.put(ChatMessageType.getName(), ChatMessageType.TextMessage);
//				jsonArray.put(jsonObject);
				list.add(jsonObject);
				LogUtil.d("未发送的消息:"+list.size()+jsonObject.toString());
				session.getBasicRemote().sendBinary(ToByteBuffer(jsonObject));
			}
//			jsonObject.put(ChatMessageType.TextMessage.toString(), jsonArray);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 转bytebuffer发送
	 * @param ob
	 * @return
	 */
	public static ByteBuffer ToByteBuffer(Object ob){
		ByteBuffer bb=ByteBuffer.wrap(ob.toString().getBytes());
		return bb;
		
	}
	/**
	 * 登录成功后发送
	 * @param receiverId
	 */
	public  void sendLinkMan(String userId,IMessage imessage){
		try {
			JdbcUtils ju=new JdbcUtils();
			String sql=String.format("select * from im_linkman where user1id=%s","'"+userId+"'");
			ResultSet rs=ju.executeQueryRS(sql);
			int count=0;
			while(rs.next()){
				String user2id=rs.getString("user2id");
				String user2name=rs.getString("user2name");
				Builder userBuidler=IMessage.User.newBuilder(); 
				userBuidler.setUserId(user2id);
				userBuidler.setUsername(user2name);
				imessage=imessage.newBuilder().addUser(userBuidler.build()).mergeFrom(imessage).build();
				count++;
			}
			LogUtil.d(String.format("一共有%s条好友记录", count));
			LogUtil.d(imessage.toByteString());
			session.getBasicRemote().sendBinary(ByteBuffer.wrap(imessage.toByteArray()));
		} catch (SQLException e) {
			e.printStackTrace();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
