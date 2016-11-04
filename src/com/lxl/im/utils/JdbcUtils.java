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
    //���ݿ��û���  
    private static final String USERNAME = "root";  
    //���ݿ�����  ̨ʽΪ��
//    private static final String PASSWORD = "";  
  //���ݿ�����  
    private static final String PASSWORD = "123";  
    //������Ϣ   
    private static final String DRIVER = "com.mysql.jdbc.Driver";  
    //���ݿ��ַ   Ҫ����Ϊutf_8
    private static final String URL = "jdbc:mysql://localhost:3306/im?useUnicode=true&characterEncoding=UTF-8";  
    private static Connection connection;  
    static {  
        // TODO Auto-generated constructor stub  
        try{  
            Class.forName(DRIVER);  
            System.out.println("�������سɹ�������");  
        }catch(Exception e){  
        	e.printStackTrace();
        	System.out.println("�������ش��󣡣���");  
        }  
    }  
      
    /** 
     * ������ݿ������ 
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
     * SQL ��ѯ����ѯ���ֱ�ӷ���ResultSet�� 
     * @param sql SQL��� 
     * @param params �������飬��û�в�����Ϊnull 
     * @return ����� 
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
     * ���ݽ�����id,���ң��ڸ�id��½�ɹ�ʱ����
     * @param receiverId
     * @return
     */
    public static ResultSet queryUnSendMessage(String receiverId){
    	String s="select * from im_unsend where receiverId=%s";
    	String sql=String.format(s, "'"+receiverId+"'");
    	LogUtil.d("����δ������Ϣ��"+sql);
    	return executeQueryRS(sql);
    	
    }
    
    
    
    /**
     * ��δ������Ϣ�������ݿ�
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
         //������
         if(result>0){
             System.out.println("�����ɹ�");
             return true;
         }else{
             System.out.println("����ʧ��");
             return false;
         }
    }  
    
    private static String  format(String s){
    	return "'"+s+"'";
    }
    
    
}  