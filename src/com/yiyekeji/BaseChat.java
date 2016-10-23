package com.yiyekeji;

import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.lxl.im.utils.ManageUtil;

public class BaseChat {
	private Session session;
	public void onOpen(Session session) {
		this.session=session;
		this.session.setMaxBinaryMessageBufferSize(10*1024*1024);
		ManageUtil.chatList.add(this);
	}
	
	
	
	
	public Session getSession(){
		return session;
	}
}
