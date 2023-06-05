// Source File Name:   MysqlConnectionFactory.java

package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Alexander
 */
public class MysqlConnectionFactory {
    
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/acd","tmuser","tmpass");  
            return con;
        } catch(ClassNotFoundException | SQLException e){ 
            return null;
        }  
    }
    
    public static Connection getVoiceConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");  
            Connection con=DriverManager.getConnection(  
            "jdbc:mysql://localhost:3306/ippbx","tmuser","tmpass");  
            return con;
        } catch(ClassNotFoundException | SQLException e){ 
            return null;
        }  
    }
}
