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
	 * ���͵���������Ϣ
	 * �������򱣴浽���ݵ�δ������Ϣ��
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
				LogUtil.d("���û�δ��¼"+iMessage.getReceiverId());
				//���������Ϣ��� ���ݿ�
				JdbcUtils.insertIntoUnsend(iMessage.getReceiverId(),iMessage.getContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * תbytebuffer����
	 * @param ob
	 * @return
	 */
	public static ByteBuffer ToByteBuffer(IMessage ob){
		ByteBuffer bb=ByteBuffer.wrap(ob.toByteArray());
		return bb;
		
	}
	/**
	 * ��¼�ɹ�����
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
			LogUtil.d(String.format("һ����%s�����Ѽ�¼", count));
			LogUtil.d(imessage.toByteString());
			session.getBasicRemote().sendBinary(ByteBuffer.wrap(imessage.toByteArray()));
		} catch (SQLException e) {
			e.printStackTrace();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * ����id���Ҹ��û���δ������Ϣ
	 * @param userId
	 * @param iMessage
	 */
	public void sendUnReceiMessage(String userId, IMessage iMessage) {
		ResultSet rs=JdbcUtils.queryUnSendMessage(userId);
		ArrayList<IMessage> list=new ArrayList<>();
		try {
			while(rs.next()){
				iMessage=iMessage.newBuilder().setContent(rs.getString("content")).mergeFrom(iMessage).build();
				list.add(iMessage);
				LogUtil.d(iMessage.toString());
			}
			LogUtil.d("δ���͵���Ϣ����:"+list.size());
			session.getBasicRemote().sendBinary(ToByteBuffer(list.get(0)));
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}
