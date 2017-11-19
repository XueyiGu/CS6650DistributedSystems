/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.DAO;

import ccs.xueyi.restfulservice.ConnectionManager;
import ccs.xueyi.restfulservice.model.MeasureData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ceres
 */
public class MeasureDAO {
    private static MeasureDAO instance = null;
    private static final String MEASUREDAO_NAME = MeasureDAO.class.getSimpleName();
    public ConnectionManager manager;
    
    public MeasureDAO(){
        manager = new ConnectionManager();
    }
    
    public static MeasureDAO getInstance(){
        if(instance == null){
            instance = new MeasureDAO();
        }
        return instance;
    }
    
    public void createTable() throws SQLException{
        String statement = " drop table if exists measuredata; " +
                " create table measuredata(id serial PRIMARY KEY NOT NULL," +
                " response_time INTEGER NOT NULL, " +
                " error INTEGER NOT NULL) ";
        Connection connection = null;
        PreparedStatement createStatement = null;
        try {
            connection = ConnectionManager.connect();
            createStatement = connection.prepareStatement(statement);
            createStatement.execute();
            createStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(MeasureDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }
            
    }
    public void bathInsertData(List<MeasureData> dataList) throws SQLException{
        String statement = "INSERT INTO measuredata " + 
        "(response_time, error)  " +
        "VALUES (?, ?);";
        Connection connection = null;
        PreparedStatement insertStatement = null;
        try {
            connection = ConnectionManager.connect();
            insertStatement = connection.prepareStatement(statement);
            int size = dataList.size();
            for(int i = 0; i < size; i++){
                insertStatement.setInt(1, dataList.get(i).getResponseTime());
                insertStatement.setInt(2, dataList.get(i).getError());
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();
            
            insertStatement.close();
        } catch (SQLException ex) {
            Logger.getLogger(MEASUREDAO_NAME).log(Level.SEVERE, null, ex);
        }
        finally{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }
    }
}