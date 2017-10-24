/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccs.xueyi.restfulservice.DAO;

import ccs.xueyi.restfulservice.ConnectionManager;
import ccs.xueyi.restfulservice.model.RFIDLiftData;
import ccs.xueyi.restfulservice.model.SkierMetric;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Singleton;

/**
 *
 * @author ceres
 */
@Singleton
public class SkierMetricDAO {
    private static SkierMetricDAO instance = null;
    
    private final String SKIERMETRICDAO_NAME = RFIDLiftDAO.class.getName();
    public ConnectionManager manager;
    
    public SkierMetricDAO(){
        manager = new ConnectionManager();
    }
    
    public static SkierMetricDAO getInstance(){
        if(instance == null){
            instance = new SkierMetricDAO();
        }
        return instance;
    }
    
     public SkierMetric findSkiMetricByFilter(String skierID, String dayNum) 
            throws SQLException {
        String stmt = "SELECT * FROM skiermetrics WHERE skier_id = ? and day_num = ? ";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        SkierMetric res = null;
        
        try {
            connection = ConnectionManager.connect();
            selectStmt = connection.prepareStatement(stmt);
            selectStmt.setString(1, skierID);
            selectStmt.setString(2, dayNum);
            results = selectStmt.executeQuery();
            while (results.next()) {
                res = new SkierMetric(
                        results.getString(1), 
                        results.getString(2), 
                        results.getString(3), 
                        results.getInt(4), 
                        results.getInt(5));
            }
            results.close();
            selectStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKIERMETRICDAO_NAME).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return res;
    }
     
     
     public long updateMetrics(RFIDLiftData data) throws SQLException{
         String stmt = "INSERT INTO skiermetrics (skier_id, day_num, "
                + "total_vertical, lift_num) VALUES(?,?,?,?) " + 
                "ON CONFLICT (skier_id, day_num) DO UPDATE SET " +
                "total_vertical =  skiermetrics.total_vertical + EXCLUDED.total_vertical, " +
                "lift_num = skiermetrics.lift_num + EXCLUDED.lift_num";
        Connection connection = null;
        PreparedStatement upsertStmt = null;
        long id = 0;
        
        try {
            connection = ConnectionManager.connect();
            upsertStmt = connection.prepareStatement(stmt);
            upsertStmt.setString(1, data.getSkierID());
            upsertStmt.setString(2, data.getDayNum());
            upsertStmt.setInt(3, data.getVertical());
            upsertStmt.setInt(4, 1);
            int affectedRows = upsertStmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = upsertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(SKIERMETRICDAO_NAME).log(Level.SEVERE, null, ex);
                }
            }
            upsertStmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(SKIERMETRICDAO_NAME).log(Level.SEVERE, null, ex);
        }  finally {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
        return id;
     }
     
}
