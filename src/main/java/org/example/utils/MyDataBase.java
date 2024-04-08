package org.example.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MyDataBase {
 private String url;
private String user ;
 private String pws;

private Connection connection;
private static MyDataBase instance;
private MyDataBase(){
    try {
        Properties props = new Properties();
        props.load(new FileInputStream("src/app.properties"));
        String url = props.getProperty("url"),
                user = props.getProperty("user"),
                pws = props.getProperty("pws");
        connection= DriverManager.getConnection(url,user,pws);
        System.out.println("Connected to 'helpmebalance' database");
    } catch (SQLException | IOException e) {
        System.out.println(e.getMessage());
    }
}
public static MyDataBase getInstance(){
    if (instance==null){
     instance= new MyDataBase();

    }
    return instance;
}

    public Connection getConnection() {
        return connection;
    }
}
