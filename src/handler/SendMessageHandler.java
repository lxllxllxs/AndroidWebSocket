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
import com.sun.istack.internal.NotNull;
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
	 * 接收反馈成功
	 * 接收端检查id 如果id一致则为客户端发送成功
	 */
	public void sendReceiSuccess(@NotNull IMessage iMessage){
		LogUtil.d("准备推送反馈信息：接收者"+iMessage.getSenderId());
		try {
			session.getBasicRemote().sendText(iMessage.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 发送单人聊天信息
	 * 若离线则保存到数据的未发送消息表
	 * @param jsonObject
	 * @param payload
	 */
	public void sendMessage(IMessage iMessage){
		LogUtil.d("准备推送信息：接收者"+iMessage.getReceiverId());
		for(Map.Entry<String,Session> item:ManageUtil.chatList.entrySet()){
			try {
				if(item.getKey().equals(iMessage.getReceiverId())){
					item.getValue().getBasicRemote().sendBinary(ToByteBuffer(iMessage));
					return;
				}
				LogUtil.d("该用户未登录"+iMessage.getReceiverId());
				//在这里把消息存进 数据库
				JdbcUtils.insertIntoUnsend(
						iMessage.getReceiverId(),
						iMessage.getContent(),
						iMessage.getDate(),
						iMessage.getId(),
						iMessage.getSenderId()
				);
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.d(e.getMessage()+e.toString());
			}
		}
	}
	
	/**
	 * 转bytebuffer发送
	 * @param ob
	 * @return
	 */
	public static ByteBuffer ToByteBuffer(IMessage ob){
		ByteBuffer bb=ByteBuffer.wrap(ob.toByteArray());
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
				session.getBasicRemote().sendBinary(ByteBuffer.wrap(imessage.toByteArray()));
			}
			LogUtil.d(String.format("一共有%s条好友记录", count));
		
		} catch (SQLException e) {
			e.printStackTrace();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 根据id查找改用户下未接收信息
	 * @param userId
	 * @param iMessage
	 */
	public void sendUnReceiMessage(String userId, IMessage iMessage) {
		ResultSet rs=JdbcUtils.queryUnSendMessage(userId);
		ArrayList<IMessage> list=new ArrayList<>();
		try {
			while(rs.next()){
				iMessage=iMessage.newBuilder().setContent(rs.getString("content")).mergeFrom(iMessage).build();
				iMessage=iMessage.newBuilder().setDate(rs.getString("sendDate")).mergeFrom(iMessage).build();
				iMessage=iMessage.newBuilder().setId(rs.getString("msgId")).mergeFrom(iMessage).build();
				iMessage=iMessage.newBuilder().setReceiverId(rs.getString("receiverId")).mergeFrom(iMessage).build();
				iMessage=iMessage.newBuilder().setSenderId(rs.getString("senderId")).mergeFrom(iMessage).build();
				list.add(iMessage);
				LogUtil.d(iMessage.toString());
			}
			LogUtil.d("未发送的消息共有:"+list.size());
			for(IMessage im:list){
				session.getBasicRemote().sendBinary(ToByteBuffer(im));
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
