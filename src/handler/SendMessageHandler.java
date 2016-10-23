package handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import javax.websocket.Session;

import com.lxl.im.utils.LogUtil;
import com.lxl.im.utils.ManageUtil;

public class SendMessageHandler {
	
	public void sendMessage(Session session,byte[] payload){
		ByteBuffer bb=ByteBuffer.wrap(payload);
		for(Map.Entry<Session, String> item:ManageUtil.chatList.entrySet()){
			try {
				if(item.getKey().equals(session)){
					continue;
				}
				item.getKey().getBasicRemote().sendBinary(bb);
				item.getKey().getBasicRemote().sendText(new String(payload));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
