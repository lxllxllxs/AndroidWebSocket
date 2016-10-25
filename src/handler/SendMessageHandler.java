package handler;

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
	public void sendMessage(JSONObject jsonObject,byte[] payload){
		String receiverId="";
		try {
			receiverId = jsonObject.getString(ConstantUtil.RECEIVER_ID);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		ByteBuffer bb=ByteBuffer.wrap(payload);
		for(Map.Entry<String,Session> item:ManageUtil.chatList.entrySet()){
			try {
				if(item.getKey().equals(receiverId)){
					item.getValue().getBasicRemote().sendBinary(bb);
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
	 * ����δ������Ϣ
	 */
	public String sendUnReceiverMessage(String receiverId){
		ResultSet rs=JdbcUtils.queryUnSendMessage(receiverId);
		JSONArray jsonArray=new JSONArray();
		JSONObject jsonObject;
		try {
			while(rs.next()){
				jsonObject=new JSONObject(rs.getString("content"));
				jsonArray.put(jsonObject);
			}
			return jsonArray.toString();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * ��¼�ɹ�����
	 * @param receiverId
	 */
	public void sendLinkMan(String receiverId){
		try {
			JdbcUtils ju=new JdbcUtils();
			JSONObject jsonObject=new JSONObject();
			String sql=String.format("select * from im_linkman where user1id=%s","'"+receiverId+"'");
			ResultSet rs=ju.executeQueryRS(sql);
			int count=0;
			jsonObject.put(ConstantUtil.MESSAG_TYPE, MessageType.LinkMan);
			while(rs.next()){
				String user2id=rs.getString("user2id");
				String user2name=rs.getString("user2name");
				jsonObject.put(user2id,user2name);
				count++;
			}
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
