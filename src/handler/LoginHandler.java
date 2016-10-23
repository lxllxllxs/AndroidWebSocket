package handler;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.websocket.Session;

import com.lxl.im.utils.JdbcUtils;
import com.lxl.im.utils.LogUtil;
import com.lxl.im.utils.ManageUtil;

public class LoginHandler {

	public boolean isSignIn(Session session,String username,String pwd){
		JdbcUtils ju=new JdbcUtils();
		String sql="select userid from im_user where username="+"'"+username+"'"+" and pwd="+"'"+pwd+"'";
		System.out.println(sql);
		ResultSet rs=ju.executeQueryRS(sql);
		try {
			if(rs.next()){
				ManageUtil.chatList.put(session,rs.getString("userid"));
				 LogUtil.d("登录用户数为"+ManageUtil.chatList.size());
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
