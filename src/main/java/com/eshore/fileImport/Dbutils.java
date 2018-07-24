package com.eshore.fileImport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class Dbutils {
	private Connection conn = null; 
    private PreparedStatement stmt = null; 
    private ResultSet rs = null; 
   //private static String driver = "com.mysql.jdbc.Driver"; 
   //private String url = "jdbc:mysql://localhost:3306/user?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"; 
   //private String user = "root"; 
   //private String password = "123456"; 
    private static Map<String,String> map=MapRegister.getMap();
    /**
     * Get the driver
     */ 
    static { 
    } 
    /**
     * Connect the database
     */ 
    public Connection getCon() { 
        try { 
            Class.forName(map.get("driver")); 
        } catch (ClassNotFoundException e) { 
            e.printStackTrace(); 
        } 
        try { 
            conn = (Connection) DriverManager 
                    .getConnection(map.get("url"), map.get("user"), map.get("password")); 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } 
        return conn; 
    } 
   
    /**
     * @param sql
     * @param obj
     *Update
     */
    public int update(String sql, Object... obj) {
        int count = 0;
        conn = getCon();
        try {
            stmt = conn.prepareStatement(sql);
            if (obj != null) {
                for (int i = 0; i < obj.length; i++) {
                    stmt.setObject(i + 1, obj[i]);
                }
            }
            count = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return count;
    }
  
    /**
     * @param sql
     * @param obj
     * Query
     */
    public ResultSet Query(String sql, Object... obj) {
        conn = getCon();
        try {
            stmt = conn.prepareStatement(sql);
            if (obj != null) {
                for (int i = 0; i < obj.length; i++) {
                    stmt.setObject(i + 1, obj[i]);
                }
            }
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return rs;
    }
   
    /**
     * CLose the resource
     */ 
    public void close() { 
        try { 
            if (rs != null) { 
                rs.close(); 
            } 
        } catch (SQLException e) { 
            e.printStackTrace(); 
        } finally { 
            try { 
                if (stmt != null) { 
                    stmt.close(); 
                } 
            } catch (SQLException e) { 
                e.printStackTrace(); 
            } finally { 
                if (conn != null) { 
                    try { 
                        conn.close(); 
                    } catch (SQLException e) { 
                        e.printStackTrace(); 
                    } 
                } 
            } 
        } 
    } 
    
}
