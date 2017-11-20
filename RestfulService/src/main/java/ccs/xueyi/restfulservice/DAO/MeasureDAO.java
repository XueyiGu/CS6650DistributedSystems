/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.DAO;

import ccs.xueyi.restfulservice.ConnectionManager;
import ccs.xueyi.restfulservice.client.ChartGenerator;
import ccs.xueyi.restfulservice.model.MeasureData;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    public static void main(String[] args){
        try {
            MeasureDAO.getInstance().getMeasureMetrics();
        } catch (SQLException ex) {
            Logger.getLogger(MeasureDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //generate chart for response time
            List<Long> dataList = MeasureDAO.getInstance().getResponseTime();
            long[] dataSet = new long[dataList.size()];
            for(int i = 0; i < dataList.size(); i++){
                dataSet[i] = dataList.get(i);
            }
            Arrays.sort(dataSet);
            ChartGenerator chartGenerator = new ChartGenerator();
            chartGenerator.getChart(dataSet, "Response Latancy", "Ranking", "Latancy");
        } catch (SQLException | IOException ex) {
            Logger.getLogger(MeasureDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Long> getResponseTime() throws SQLException{
        String statement = " SELECT * FROM measuredata ";
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet results = null;
        List<Long> dataList = new ArrayList<>();
        try {
            connection = ConnectionManager.connect();
            stmt = connection.prepareStatement(statement);
            results = stmt.executeQuery();
            while(results.next()){
                dataList.add((long)results.getInt("response_time"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MeasureDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        }
        return dataList;
    }
    public void getMeasureMetrics() throws SQLException{
        String countState = " SELECT COUNT(*) FROM measuredata ";
        String errorSumState = " SELECT SUM(error) FROM measuredata ";
        String avgState = "SELECT AVG(response_time) FROM measuredata ";
        String medainState = " SELECT PERCENTILE_CONT(0.5) within group "
                + " (order by response_time) FROM measuredata ";
        String per95State = "SELECT PERCENTILE_CONT(0.95) within group "
                + " (order by response_time) FROM measuredata ";
        String per99State = "SELECT PERCENTILE_CONT(0.99) within group "
                + " (order by response_time) FROM measuredata";
        
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet results = null;
        
        try {
            connection = ConnectionManager.connect();
            stmt = connection.prepareStatement(countState);
            results = stmt.executeQuery();
            while(results.next()){
                System.out.println("Total number of request: " + results.getInt(1));
            }
            
            stmt = connection.prepareStatement(errorSumState);
            results = stmt.executeQuery();
            while(results.next()){
                System.out.println("Total error number is: " + results.getInt(1));
            }
            
            stmt = connection.prepareStatement(avgState);
            results = stmt.executeQuery();
            while(results.next()){
                System.out.println("The mean latency is: " + results.getInt(1));
            }
            
            stmt = connection.prepareStatement(medainState);
            results = stmt.executeQuery();
            while(results.next()){
                System.out.println("The median latency is: " + results.getInt(1));
            }
            
            stmt = connection.prepareStatement(per95State);
            results = stmt.executeQuery();
            while(results.next()){
                System.out.println("The 95th perentile of latency is: " + results.getInt(1));
            }
            
            stmt = connection.prepareStatement(per99State);
            results = stmt.executeQuery();
            while(results.next()){
                System.out.println("The 99th perentile of latency is: " + results.getInt(1));
            }
            results.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(MeasureDAO.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }
}
