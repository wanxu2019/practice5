package utils;

import model.Result;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtils {

    private static DataSource dataSource =  null;

    static {
        try {
            Context ctx=new InitialContext();
            dataSource=(DataSource)ctx.lookup("java:comp/env/jdbc/projectManager");
    } catch (NamingException e) {
            e.printStackTrace();
        }

    }

    public static Connection getConnection() throws SQLException {
        //从数据源中获取数据库连接
        return dataSource.getConnection();
    }

    public static void release(Connection connection, Statement statement, ResultSet resultSet){
        if(resultSet!=null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement!=null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if(connection!=null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



}
