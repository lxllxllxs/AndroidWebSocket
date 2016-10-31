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
	public void sendMessage(JSONObject jsonObject){
		String receiverId="";
		try {
			receiverId = jsonObject.getString(ConstantUtil.RECEIVER_ID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		for(Map.Entry<String,Session> item:ManageUtil.chatList.entrySet()){
			try {
				if(item.getKey().equals(receiverId)){
					item.getValue().getBasicRemote().sendBinary(JOToByteBuffer(jsonObject));
					return;
				}
				LogUtil.d("���û�δ��¼"+receiverId);
				//���������Ϣ��� ���ݿ�
				JdbcUtils.insertIntoUnsend(receiverId,jsonObject.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ����������Ϣ
	 * ������ ������Ϣ
	 * ������ �ı���Ϣ
	 * ��������
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
				LogUtil.d("δ���͵���Ϣ:"+list.size()+jsonObject.toString());
				session.getBasicRemote().sendBinary(JOToByteBuffer(jsonObject));
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
	 * תbytebuffer����
	 * @param ob
	 * @return
	 */
	public static ByteBuffer JOToByteBuffer(Object ob){
		ByteBuffer bb=ByteBuffer.wrap(ob.toString().getBytes());
		return bb;
		
	}
	/**
	 * ��¼�ɹ�����
	 * @param receiverId
	 */
	public  void sendLinkMan(String userId,String username,JSONObject jsonObject){
		try {
			JdbcUtils ju=new JdbcUtils();
			String sql=String.format("select * from im_linkman where user1id=%s","'"+userId+"'");
			ResultSet rs=ju.executeQueryRS(sql);
			int count=0;
			while(rs.next()){
				String user2id=rs.getString("user2id");
				String user2name=rs.getString("user2name");
				jsonObject.put(user2id,user2name);
				count++;
			}
			//�û���Ϣ
			jsonObject.put(userId,username);
			LogUtil.d(String.format("һ����%s�����Ѽ�¼", count));
			session.getBasicRemote().sendBinary(ByteBuffer.wrap(jsonObject.toString().getBytes()));
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
