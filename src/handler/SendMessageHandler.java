package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.websocket.Session;

import org.json.JSONException;
import org.json.JSONObject;

import com.lxl.im.utils.ConstantUtil;
import com.lxl.im.utils.EnumUtil.MessageType;
import com.lxl.im.utils.JdbcUtils;
import com.lxl.im.utils.LogUtil;
import com.lxl.im.utils.ManageUtil;

import jdk.nashorn.internal.scripts.JS;

public class SendMessageHandler {
	
	public void sendMessage(Session session,byte[] payload){
		ByteBuffer bb=ByteBuffer.wrap(payload);
		for(Map.Entry<Session, String> item:ManageUtil.chatList.entrySet()){
			try {
				if(item.getKey().equals(session)){
					continue;
				}
				item.getKey().getBasicRemote().sendBinary(bb);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendLinkMan(Session session){
		try {
			JdbcUtils ju=new JdbcUtils();
			JSONObject jsonObject=new JSONObject();
			String userid=ManageUtil.chatList.get(session);
			String sql=String.format("select * from im_linkman where user1id=%s","'"+userid+"'");
			ResultSet rs=ju.executeQueryRS(sql);
			jsonObject.put(ConstantUtil.MESSAG_TYPE, MessageType.LinkMan);
			if(rs.next()){
				String user2id=rs.getString("user2id");
				String user2name=rs.getString("user2name");
				jsonObject.put(ConstantUtil.LinkList,user2id+","+user2name);
			}
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
