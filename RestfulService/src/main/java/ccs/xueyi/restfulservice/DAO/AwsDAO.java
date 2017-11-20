/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.DAO;

import ccs.xueyi.restfulservice.ConnectionManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class AwsDAO {
    private static AwsDAO instance = null;
    public ConnectionManager manager;
    
    public AwsDAO(){
        manager = new ConnectionManager();
    }
    
    public static AwsDAO getInstance(){
        if(instance == null){
            instance = new AwsDAO();
        }
        return instance;
    }
    
    public Map<String, String> getAwsToken() throws SQLException{
        String statement = " select * from aws_token ";
        Connection connection = null;
        PreparedStatement queryStatement = null;
        ResultSet results = null;
        Map<String, String> map = new HashMap<>();
        try {
            connection = ConnectionManager.connect();
            queryStatement = connection.prepareStatement(statement);
            results = queryStatement.executeQuery();
            while(results.next()){
                map.put("access_key_id", results.getString("access_key_id"));
                map.put("secret_access_key", results.getString("secret_access_key"));
            }
            results.close();
            queryStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(AwsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return map;
    }
}
