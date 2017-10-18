/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.DAO;

import ccs.xueyi.restfulservice.ConnectionManager;
import ccs.xueyi.restfulservice.beans.RFIDLiftData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class RFIDLiftDAO {
    private static RFIDLiftDAO instance = null;
    private static String RFIDLiftDAO_NAME = RFIDLiftDAO.class.getName();
    public ConnectionManager manager;
    
    public RFIDLiftDAO(){
        manager = new ConnectionManager();
    }
    
    public static RFIDLiftDAO getInstance(){
        if(instance == null){
            instance = new RFIDLiftDAO();
        }
        return instance;
    }
    public void getAllData() {
        String statement = "SELECT * FROM skidata" ;
        Connection connection = null;
        PreparedStatement selectStatement = null;
        ResultSet results = null;
        
        try {
            connection = ConnectionManager.connect();
            selectStatement = connection.prepareStatement(statement);
            results = selectStatement.executeQuery();
            while (results.next()) {
                System.out.println(results.getString(1) + "\t" + 
                        results.getString(2) + "\t" + 
                        results.getString(3) + "\t" + 
                        results.getString(4) + "\t" + 
                        results.getString(5) + "\t");
            }
            selectStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO_NAME).log(Level.SEVERE, null, ex);
        }
    }
    
    public long insertData(RFIDLiftData data) {
        String statement = "INSERT INTO skidata " + 
        "(resort_id, day_num, skier_id, lift_id, timestamp)  " +
        "VALUES (?, ?, ?, ?, ?);";
        Connection connection = null;
        PreparedStatement insertStatement = null;
        long id = 0;
        
        try {
            connection = ConnectionManager.connect();
            insertStatement = connection.prepareStatement(statement);
            insertStatement.setString(1, data.getResortID());
            insertStatement.setString(2, data.getDayNum());
            insertStatement.setString(3, data.getSkierID());
            insertStatement.setString(4, data.getLiftID());
            insertStatement.setString(5, data.getTimestamp());
            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = insertStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RFIDLiftDAO_NAME).log(Level.SEVERE, null, ex);
                }
            }
            insertStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO_NAME).log(Level.SEVERE, null, ex);
        }
        return id;        
    }
    
    public RFIDLiftData findData(String skierID, String dayNum){
        String statement = "SELECT * FROM skidata " + " WHERE skier_id = ? and day_num = ?";
        Connection connection = null;
        PreparedStatement selectStatement = null;
        ResultSet results = null;
        RFIDLiftData data = null;
        
        try {
            connection = ConnectionManager.connect();
            selectStatement = connection.prepareStatement(statement);
            selectStatement.setString(1, skierID);
            selectStatement.setString(2, dayNum);
            results = selectStatement.executeQuery();
            while (results.next()) {
                data = new RFIDLiftData(
                        results.getString(1), 
                        results.getString(2), 
                        results.getString(3), 
                        results.getString(4), 
                        results.getString(5), 
                        results.getString(6));
            }
            selectStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(RFIDLiftDAO_NAME).log(Level.SEVERE, null, ex);
        }
        return data;
    }
}
