package handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.websocket.Session;

import com.lxl.im.utils.JdbcUtils;
import com.lxl.im.utils.LogUtil;
import com.lxl.im.utils.ManageUtil;

public class LoginHandler {

	public String SignIn(Session session,String username,String pwd){
		String sql="select userid from im_user where username="+"'"+username+"'"+" and pwd="+"'"+pwd+"'";
		System.out.println(sql);
		ResultSet rs=JdbcUtils.executeQueryRS(sql);
		try {
			if(rs.next()){
				String receiverid=rs.getString("userid");
				ManageUtil.chatList.put(receiverid,session);
				 LogUtil.d("登录用户数为"+ManageUtil.chatList.size());
				return receiverid;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
