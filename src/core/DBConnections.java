package core;

import core.exceptions.DBException;
import core.utils.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static core.utils.Constants.logConstants.*;

public class DBConnections {

    private static Connection conn = null;

    public static Connection getConnection(){
        if (conn == null){
            try {
                Log.registerLog(INFO, "Connecting to database...");
                Properties prop = loadProperties();
                String url = prop.getProperty("dburl");
                conn = DriverManager.getConnection(url, prop);
                Log.registerLog(INFO, "Database connection established.");
            } catch (SQLException e){
                throw new DBException(e.getMessage());
            }
        }
        return conn;
    }

    private static Properties loadProperties(){
        Log.registerLog(INFO, "Loading properties file...");
        try(FileInputStream fs = new FileInputStream("db.properties")){
            Properties prop = new Properties();
            prop.load(fs);
            Log.registerLog(INFO, "Loaded properties file.");
            return prop;
        } catch (IOException e) {
            throw new DBException(e.getMessage());
        }
    }

    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DBException(e.getMessage());
            }
        }
    }

    public static void closeConnection(){
        if (conn != null){
            try {
                Log.registerLog(INFO, "Closing connection...");
                conn.close();
                Log.registerLog(INFO, "Connection closed.");
                conn = null;
            } catch (SQLException e){
                throw new DBException(e.getMessage());
            }
        }
    }
}
