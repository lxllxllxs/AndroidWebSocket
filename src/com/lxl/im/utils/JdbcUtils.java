package com.lxl.im.utils;
import java.lang.reflect.Field;  
import java.sql.Connection;  
import java.sql.DriverManager;  
import java.sql.PreparedStatement;  
import java.sql.ResultSet;  
import java.sql.ResultSetMetaData;  
import java.sql.SQLException;  
import java.util.ArrayList;  
import java.util.HashMap;  
import java.util.List;  
import java.util.Map;  
  
  
  
public class JdbcUtils {  
    //���ݿ��û���  
    private static final String USERNAME = "root";  
    //���ݿ�����  
    private static final String PASSWORD = "123";  
    //������Ϣ   
    private static final String DRIVER = "com.mysql.jdbc.Driver";  
    //���ݿ��ַ  
    private static final String URL = "jdbc:mysql://localhost:3306/im";  
    private Connection connection;  
    private ResultSet resultSet;
	private PreparedStatement preparedStatement;  
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
    public Connection getConnection(){
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
    public ResultSet executeQueryRS(String sql) {  
            try {
				preparedStatement = getConnection().prepareStatement(sql);
				 resultSet = preparedStatement.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}  
        return resultSet;  
    }  
    
  
}  