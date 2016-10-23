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
    //数据库用户名  
    private static final String USERNAME = "root";  
    //数据库密码  
    private static final String PASSWORD = "123";  
    //驱动信息   
    private static final String DRIVER = "com.mysql.jdbc.Driver";  
    //数据库地址  
    private static final String URL = "jdbc:mysql://localhost:3306/im";  
    private Connection connection;  
    private ResultSet resultSet;
	private PreparedStatement preparedStatement;  
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
     * SQL 查询将查询结果直接放入ResultSet中 
     * @param sql SQL语句 
     * @param params 参数数组，若没有参数则为null 
     * @return 结果集 
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