package com.lxl.im.utils;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;  
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  
import java.sql.ResultSetMetaData;  
import java.sql.SQLException;  
import java.sql.Statement;
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  

import com.mysql.jdbc.log.Log;
  
  
  
public class JdbcUtils {  
    //数据库用户名  
    private static final String USERNAME = "root";  
    //数据库密码  台式为空
//    private static final String PASSWORD = "";  
  //数据库密码  
    private static final String PASSWORD = "123";  
    //驱动信息   
    private static final String DRIVER = "com.mysql.jdbc.Driver";  
    //数据库地址   要设置为utf_8
    private static final String URL = "jdbc:mysql://localhost:3306/im?useUnicode=true&characterEncoding=UTF-8";  
    private static Connection connection;  
    static {  
        // TODO Auto-generated constructor stub  
        try{  
            Class.forName(DRIVER);  
            System.out.println("驱动加载成功！！！");  
        }catch(Exception e){  
        	e.printStackTrace();
        	System.out.println("驱动加载错误！！！");  
        }  
    }  
      
    /** 
     * 获得数据库的连接 
     * @return 
     */  
    public static Connection getConnection(){
    	if(connection==null){
		    try {  
		        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);  
		    } catch (SQLException e) {  
		        // TODO Auto-generated catch block  
		        e.printStackTrace();  
		    }  
    	}
        return connection;  
    }  
  

    /** 
     * SQL 查询将查询结果直接放入ResultSet中 
     * @param sql SQL语句 
     * @param params 参数数组，若没有参数则为null 
     * @return 结果集 
     */  
    public static ResultSet executeQueryRS(String sql) {  
    	PreparedStatement ps;  
    	ResultSet resultSet=null;
    	try {
    		ps = getConnection().prepareStatement(sql);
				 resultSet = ps.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}  
        return resultSet;  
    }  
    
    /**
     * 根据接送人id,查找，在该id登陆成功时推送
     * @param receiverId
     * @return
     */
    public static ResultSet queryUnSendMessage(String receiverId){
    	String s="select * from im_unsend where receiverId=%s";
    	String sql=String.format(s, "'"+receiverId+"'");
    	LogUtil.d("查找未发送消息："+sql);
    	return executeQueryRS(sql);
    	
    }
    
    
    
    /**
     * 将未发送信息插入数据库
     * @param receiverId
     * @param jsonString
     * @return
     */
    public static boolean insertIntoUnsend(String receiverId,String content,
    		String sendDate,String msgId,String senderId) { 
    	 String s = "insert into im_unsend(receiverId,content,sendDate,msgId,senderId) values(%s,%s,%s,%s,%s)";
    	 String sql=String.format(s,
    			 format(receiverId),
    			 format(content),
    			 format(sendDate),
    			 format(msgId),
    			 format(senderId)
    			 );
    	 Statement st;
    	 System.out.println("insertIntoUnsend = "+sql);
         int result=0;
		try {
			st= getConnection().createStatement();
			result = st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
         //处理结果
         if(result>0){
             System.out.println("操作成功");
             return true;
         }else{
             System.out.println("操作失败");
             return false;
         }
    }  
    
    private static String  format(String s){
    	return "'"+s+"'";
    }
    
    
}  