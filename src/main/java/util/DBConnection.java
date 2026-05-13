package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
    public static Connection getConnection(){
        final Properties props = new Properties();
        try{
            props.load(new FileInputStream(("src/main/resources/db.properties")));
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            Connection con = DriverManager.getConnection(url,user,password);
            return con;
        }catch(SQLException | IOException e){
            System.out.println("Connection rejected : " + e.getMessage());
        }
        return null;
    }
}
