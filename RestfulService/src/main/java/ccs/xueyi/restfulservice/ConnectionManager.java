/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ceres
 */
public class ConnectionManager {
    private static final String url = "jdbc:postgresql://"
            + "restinstance.cxq5ozjlz4tr.us-west-2.rds.amazonaws.com:5432/postgresql";
    private static final String user = "xueyigu";
    private static final String password = "adminadmin";
    
    public static Connection connect() throws SQLException{
        return DriverManager.getConnection(url, user, password);
    }
    
    public void closeConnection(Connection connection)throws SQLException{
        connection.close();
    }
}

